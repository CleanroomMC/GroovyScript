package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class DescriptorHelper {

    /**
     * Holds a mapping from Java type names to native type codes.
     */
    private static final Object2ObjectMap<Class<?>, String> PRIMITIVE_TO_TERM;
    /**
     * Blocks checking of methods that are a bridge, not public, from Object, or annotated with {@link GroovyBlacklist}.
     */
    private static final Predicate<Method> DEFAULT_EXCLUSION;

    static {
        // Note: better to be String than a char here because we always have to convert to a String right away
        PRIMITIVE_TO_TERM = new Object2ObjectOpenHashMap<>();
        PRIMITIVE_TO_TERM.put(byte.class, "B");
        PRIMITIVE_TO_TERM.put(char.class, "C");
        PRIMITIVE_TO_TERM.put(short.class, "S");
        PRIMITIVE_TO_TERM.put(int.class, "I");
        PRIMITIVE_TO_TERM.put(long.class, "J");
        PRIMITIVE_TO_TERM.put(float.class, "F");
        PRIMITIVE_TO_TERM.put(double.class, "D");
        PRIMITIVE_TO_TERM.put(void.class, "V");
        PRIMITIVE_TO_TERM.put(boolean.class, "Z");
        var objectMethods = new ObjectOpenHashSet<>(Arrays.asList(Object.class.getMethods()));
        DEFAULT_EXCLUSION = m -> m.isBridge() || !Modifier.isPublic(m.getModifiers()) || objectMethods.contains(m) || m.isAnnotationPresent(GroovyBlacklist.class);
    }

    /**
     * @param method the method to get the descriptor of
     * @return the method name and descriptor
     */
    public static String getDescriptor(Method method) {
        var result = new StringBuilder();
        result.append(method.getName());
        result.append('(');
        var parameterTypes = method.getParameterTypes();
        for (var parameterType : parameterTypes) {
            result.append(getDescriptor(parameterType));
        }
        result.append(')');
        result.append(getDescriptor(method.getReturnType()));
        return result.toString();
    }

    /**
     * @param clazz the class to get the descriptor of
     * @return the descriptor string of the target class
     */
    private static String getDescriptor(Class<?> clazz) {
        if (clazz.isArray()) return "[" + getDescriptor(clazz.getComponentType());
        if (clazz.isPrimitive()) return PRIMITIVE_TO_TERM.get(clazz);
        return "L" + clazz.getName().replace('.', '/') + ";";
    }

    /**
     * @param clazz the class to generate the {@link OfClass} of
     * @return a helper class for processing the methods of an object for use in GroovyScript
     */
    public static OfClass generateOfClass(Class<?> clazz) {
        return new OfClass(clazz, DEFAULT_EXCLUSION);
    }

    public static class OfClass {

        private static final Function<String, List<String>> DESCRIPTOR_LIST = k -> new ArrayList<>();
        private static final Function<Class<? extends Annotation>, List<MethodAnnotation<?>>> ANNOTATION_LIST = k -> new ArrayList<>();

        private final List<Method> validMethods = new ObjectArrayList<>();
        private final Map<String, Method> descriptorToMethod = new Object2ObjectOpenHashMap<>();
        private final Map<String, Method> nameToMethod = new Object2ObjectOpenHashMap<>();
        private final Map<Class<? extends Annotation>, List<MethodAnnotation<?>>> annotationToMethods = new Object2ObjectOpenHashMap<>();
        private final Map<String, List<String>> duplicateMethodNames = new Object2ObjectOpenHashMap<>();
        private final Class<?> clazz;

        public OfClass(Class<?> clazz, Predicate<Method> exclude) {
            this.clazz = clazz;
            for (var method : clazz.getMethods()) {
                if (exclude.test(method)) continue;
                validMethods.add(method);
                addBasicMethod(method);
                for (var annotation : method.getDeclaredAnnotations()) {
                    addAnnotation(method, annotation);
                }
            }
        }

        /**
         * Performs multiple operations to add the given method
         * to the relevant maps.
         * Primarily handles methods with duplicate names.
         *
         * @param method method to map if not a duplicate
         */
        private void addBasicMethod(Method method) {
            var name = method.getName();
            var descriptor = DescriptorHelper.getDescriptor(method);
            descriptorToMethod.put(descriptor, method);
            // if the method name a duplicate, add further descriptors to the duplicate list
            if (duplicateMethodNames.containsKey(name)) {
                duplicateMethodNames.get(name).add(descriptor);
                return;
            }
            // otherwise, check if the method name is already set, and remove it and mark it invalid if it is
            if (nameToMethod.containsKey(name)) {
                var list = duplicateMethodNames.computeIfAbsent(name, DESCRIPTOR_LIST);
                list.add(descriptor);
                list.add(DescriptorHelper.getDescriptor(nameToMethod.remove(name)));
            } else {
                nameToMethod.put(name, method);
            }
        }

        /**
         * Adds the given method and annotation to the relevant map.
         * Can be called outside this method to support overrides.
         */
        public void addAnnotation(Method method, Annotation annotation) {
            annotationToMethods.computeIfAbsent(annotation.annotationType(), ANNOTATION_LIST)
                    .add(new MethodAnnotation<>(method, annotation));
        }

        /**
         * @return all methods that pass the exclusion filter
         */
        public List<Method> getValidMethods() {
            return validMethods;
        }

        /**
         * @param annotation the target annotation
         * @param <A>        the annotation type
         * @return all methods in the class that are annotated with the given method or were overridden via {@link #addAnnotation(Method, Annotation)}
         */
        @SuppressWarnings("unchecked")
        public <A extends Annotation> @NotNull List<MethodAnnotation<A>> getMethods(Class<A> annotation) {
            return (List<MethodAnnotation<A>>) (Object) annotationToMethods.getOrDefault(annotation, new ArrayList<>());
        }

        /**
         * The method can be obtained via the method name, provided that there is only a single
         * method with the given name.
         * Otherwise, the method can only be obtained via the full method descriptor.
         *
         * @param target the name of the method or the method descriptor
         * @return the method that is being targeted, if there is one and only one, otherwise log an error and return null
         */
        public @Nullable Method getMethod(@NotNull String target) {
            var output = descriptorToMethod.get(target);
            if (output != null) return output;
            if (duplicateMethodNames.containsKey(target)) {
                GroovyLog.msg("The target '{}' is a duplicate name, use one of the following descriptors instead", target)
                        .add("'" + String.join("', '", duplicateMethodNames.get(target)) + "'")
                        .warn()
                        .post();
            }
            output = nameToMethod.get(target);
            if (output == null) {
                GroovyLog.msg("Could not find target '{}' matching any method names or descriptors in the class {}", target, clazz)
                        .error()
                        .post();
            }
            return output;
        }
    }

    public static class MethodAnnotation<A extends Annotation> {

        private final Method method;
        private final A annotation;

        public MethodAnnotation(Method method, A annotation) {
            this.method = method;
            this.annotation = annotation;
        }

        public Method getMethod() {
            return method;
        }

        public A getAnnotation() {
            return annotation;
        }
    }
}
