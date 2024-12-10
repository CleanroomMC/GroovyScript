package com.cleanroommc.groovyscript.sandbox.expand;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.Hidden;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import com.cleanroommc.groovyscript.sandbox.meta.Getter;
import com.cleanroommc.groovyscript.sandbox.meta.Setter;
import groovy.lang.*;
import groovy.transform.Internal;
import org.apache.groovy.util.BeanUtils;
import org.codehaus.groovy.reflection.*;
import org.codehaus.groovy.runtime.HandleMetaClass;
import org.codehaus.groovy.runtime.metaclass.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExpansionHelper {

    public static ExpandoMetaClass getExpandoClass(Class<?> clazz) {
        return getExpandoClass(GroovySystem.getMetaClassRegistry().getMetaClass(clazz));
    }

    public static ExpandoMetaClass getExpandoClass(MetaClass clazz) {
        if (clazz instanceof HandleMetaClass handleMetaClass) {
            clazz = (MetaClass) handleMetaClass.replaceDelegate();
        }

        if (!(clazz instanceof ExpandoMetaClass)) {
            if (clazz instanceof DelegatingMetaClass delegatingMetaClass && delegatingMetaClass.getAdaptee() instanceof ExpandoMetaClass emc) {
                clazz = emc;
            } else {
                ExpandoMetaClass emc = new ExpandoMetaClass(clazz.getTheClass(), true, true);
                emc.initialize();
                return emc;
            }
        }

        return (ExpandoMetaClass) clazz;
    }

    public static MixinInMetaClass getMixinMetaClass(Class<?> clazz, ExpandoMetaClass emc) {
        return new MixinInMetaClass(emc, ReflectionCache.getCachedClass(clazz));
    }

    public static MixinInMetaClass getMixinMetaClass(MetaClass metaClass, ExpandoMetaClass emc) {
        return getMixinMetaClass(metaClass.getTheClass(), emc);
    }

    public static void mixinProperties(ExpandoMetaClass emc, MetaClass metaClass, MixinInMetaClass mixin) {
        final CachedClass cachedCategoryClass = ReflectionCache.getCachedClass(metaClass.getClass());

        for (MetaProperty prop : metaClass.getProperties()) {
            if (isValidProperty(prop) && emc.getMetaProperty(prop.getName()) == null) {
                emc.registerBeanProperty(prop.getName(), new MixinInstanceMetaProperty(prop, mixin));
            }
        }

        for (MetaProperty prop : cachedCategoryClass.getFields()) {
            if (isValidProperty(prop) && emc.getMetaProperty(prop.getName()) == null) {
                emc.registerBeanProperty(prop.getName(), new MixinInstanceMetaProperty(prop, mixin));
            }
        }
    }

    private static boolean isValidProperty(MetaProperty prop) {
        if (prop instanceof MetaBeanProperty beanProperty) {
            if (!isValid(beanProperty.getField())) return false;
            if (!(beanProperty.getGetter() instanceof CachedMethod method) || isValid(method))
                return false;
            return beanProperty.getSetter() instanceof CachedMethod cachedMethod && !isValid(cachedMethod);
        }
        if (prop instanceof MethodMetaProperty methodMetaProperty && methodMetaProperty.getMetaMethod() instanceof CachedMethod) {
            return isValid((CachedMethod) ((MethodMetaProperty) prop).getMetaMethod());
        }
        if (prop instanceof CachedField cachedField) {
            return isValid(cachedField);
        }
        return false;
    }

    public static void mixinClasses(Class<?> self, Collection<Class<?>> mixinClasses) {
        ExpandoMetaClass emc = getExpandoClass(self);
        for (Class<?> clazz : mixinClasses) {
            mixinClass(emc, getMixinMetaClass(clazz, emc), clazz);
        }
    }

    public static void mixinClass(Class<?> self, Class<?> mixinClass) {
        ExpandoMetaClass emc = getExpandoClass(self);
        MixinInMetaClass mixin = getMixinMetaClass(mixinClass, emc);
        mixinClass(emc, mixin, mixinClass);
    }

    private static void mixinClass(ExpandoMetaClass emc, MixinInMetaClass mixin, Class<?> clazz) {
        MetaClass other = GroovySystem.getMetaClassRegistry().getMetaClass(clazz);
        mixinProperties(emc, other, mixin);
        for (MetaMethod method : other.getMethods()) {
            if (method instanceof CachedMethod cachedMethod && clazz == cachedMethod.getCachedMethod().getDeclaringClass()) {
                mixinMethod(emc, cachedMethod, mixin);
            }
        }
    }

    public static void mixinClosure(Class<?> self, String name, Closure<?> closure) {
        ExpandoMetaClass emc = getExpandoClass(self);
        emc.registerInstanceMethod(name, closure);
    }

    public static void mixinMethod(Class<?> self, Method method) {
        ExpandoMetaClass emc = getExpandoClass(self);
        MixinInMetaClass mixin = Modifier.isStatic(method.getModifiers()) ? null : getMixinMetaClass(method.getDeclaringClass(), emc);
        mixinMethod(emc, CachedMethod.find(method), mixin);
    }

    public static void mixinMethod(Class<?> self, Class<?> other, String methodName) {
        ExpandoMetaClass emc = getExpandoClass(self);
        MixinInMetaClass mixin = getMixinMetaClass(other, emc);
        for (Method method : other.getDeclaredMethods()) {
            if (methodName.equals(method.getName())) {
                mixinMethod(emc, CachedMethod.find(method), mixin);
            }
        }
    }

    public static void mixinMethod(Class<?> self, String name, Function<Object[], ?> function) {
        ExpandoMetaClass emc = getExpandoClass(self);
        emc.registerInstanceMethod(name, ClosureHelper.of(function));
    }

    private static void mixinMethod(ExpandoMetaClass self, CachedMethod method, MixinInMetaClass mixin) {
        final int mod = method.getModifiers();
        if (!isValid(method)) {
            return;
        }
        CachedClass[] paramTypes = method.getParameterTypes();
        MetaMethod metaMethod;
        if (Modifier.isStatic(mod)) {
            if (paramTypes.length > 0 && paramTypes[0].isAssignableFrom(self.getTheClass())) {
                // instance method disguised as static method
                if (paramTypes[0].getTheClass() == self.getTheClass())
                    metaMethod = new NewInstanceMetaMethod(method);
                else
                    metaMethod = new NewInstanceMetaMethod(method) {

                        @Override
                        public CachedClass getDeclaringClass() {
                            return ReflectionCache.getCachedClass(self.getTheClass());
                        }
                    };
            } else {
                // true static method
                metaMethod = new NewStaticMetaMethod(self.getTheCachedClass(), method);
            }
        } else if (method.getDeclaringClass().getTheClass() != Object.class || "toString".equals(method.getName())) {
            metaMethod = new MixinInstanceMetaMethod(method, mixin);
        } else {
            return;
        }
        self.registerInstanceMethod(metaMethod);
    }

    public static <T, S> void mixinConstProperty(Class<S> self, String name, T obj, boolean hidden) {
        Objects.requireNonNull(obj, "Can't add null property to class!");
        Class<T> type = (Class<T>) obj.getClass();
        mixinProperty(self, name, type, s -> obj, null, hidden);
    }

    public static <T, S> void mixinProperty(Class<S> self,
                                            String name,
                                            Class<T> type,
                                            @Nullable Supplier<T> getter,
                                            @Nullable Consumer<T> setter,
                                            boolean hidden) {
        mixinProperty(self, name, type, getter != null ? s -> getter.get() : null, setter != null ? (s, t) -> setter.accept(t) : null, hidden);
    }

    public static <T, S> void mixinProperty(Class<S> self,
                                            String name,
                                            Class<T> type,
                                            @Nullable Function<S, T> getter,
                                            @Nullable BiConsumer<S, T> setter,
                                            boolean hidden) {
        if (getter == null && setter == null) return;
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name for property must not be empty!");
        }
        String upperName = name;
        if (!Character.isDigit(name.charAt(0))) upperName = BeanUtils.capitalize(name);
        if (getter == null) {
            getter = so -> { throw new GroovyRuntimeException("Property '" + name + "' in " + self.getName() + " is writable, but not readable!"); };
        }
        if (setter == null) {
            setter = (so, t) -> { throw new GroovyRuntimeException("Property '" + name + "' in " + self.getName() + " is readable, but not writable!"); };
        }

        MetaMethod g = new Getter<>("get" + upperName, type, self, getter);
        MetaMethod s = new Setter<>("set" + upperName, type, self, setter);

        ExpandoMetaClass emc = getExpandoClass(self);
        emc.registerBeanProperty(name, new Property(name, type, g, s, hidden));
    }

    private static boolean isValid(CachedMethod method) {
        final int mod = method.getModifiers();
        return Modifier.isPublic(mod) && !Modifier.isAbstract(mod) && !method.isSynthetic() && method.getAnnotation(Internal.class) == null && method.getAnnotation(GroovyBlacklist.class) == null;
    }

    private static boolean isValid(CachedField cachedField) {
        if (cachedField == null) return true;
        final int mod = cachedField.getModifiers();
        Field field = cachedField.getCachedField();
        return Modifier.isPublic(mod) && !field.isSynthetic() && !field.isAnnotationPresent(Internal.class) && !field.isAnnotationPresent(GroovyBlacklist.class);
    }

    /**
     * Groovy's {@link org.codehaus.groovy.runtime.metaclass.NewStaticMetaMethod} does weird shit.
     */
    private static class NewStaticMetaMethod extends NewMetaMethod {

        private final CachedClass owner;

        public NewStaticMetaMethod(CachedClass owner, CachedMethod method) {
            super(method);
            this.owner = owner;
        }

        @Override
        public boolean isStatic() {
            return true;
        }

        @Override
        public int getModifiers() {
            return Modifier.PUBLIC;
        }

        @Override
        public CachedClass getDeclaringClass() {
            return owner;
        }

        @Override
        public CachedClass getOwnerClass() {
            return owner;
        }
    }

    private static class Property extends MetaBeanProperty implements Hidden {

        private final boolean hidden;

        public Property(String name, Class type, MetaMethod getter, MetaMethod setter, boolean hidden) {
            super(name, type, getter, setter);
            this.hidden = hidden;
        }

        @Override
        public boolean isHidden() {
            return hidden;
        }
    }
}
