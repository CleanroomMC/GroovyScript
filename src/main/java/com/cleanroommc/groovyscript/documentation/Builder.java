package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.documentation.annotations.*;
import com.google.common.collect.ComparisonChain;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.resources.I18n;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Builder {

    private final String reference;
    private final Method builderMethod;
    private final RecipeBuilderDescription annotation;
    private final Map<String, List<RecipeBuilderMethod>> methods;
    private final Map<String, FieldDocumentation> fields;
    private final List<Method> registrationMethods;

    public Builder(Method builderMethod, String reference, String baseTranslationKey) {
        this.builderMethod = builderMethod;
        this.reference = reference;
        this.annotation = builderMethod.getAnnotation(RecipeBuilderDescription.class);
        Class<?> builderClass = builderMethod.getReturnType();
        this.methods = gatherMethods(builderClass);
        this.fields = gatherFields(builderClass, annotation, baseTranslationKey);
        this.registrationMethods = gatherRegistrationMethods(builderClass);
    }

    private static Map<String, List<RecipeBuilderMethod>> gatherMethods(Class<?> builderClass) {
        Map<String, List<RecipeBuilderMethod>> fieldToModifyingMethods = new HashMap<>();
        for (Method method : builderClass.getMethods()) {
            if (!method.isAnnotationPresent(RecipeBuilderMethodDescription.class)) continue;
            RecipeBuilderMethod pair = new RecipeBuilderMethod(method);
            for (String target : pair.targetFields()) {
                fieldToModifyingMethods.computeIfAbsent(target, k -> new ArrayList<>()).add(pair);
            }
        }

        fieldToModifyingMethods.forEach((key, value) -> value.sort((left, right) -> ComparisonChain.start()
                .compare(left.getAnnotation().priority(), right.getAnnotation().priority())
                .compare(left.getMethod().getName().length(), right.getMethod().getName().length())
                .compare(left.getMethod().getName(), right.getMethod().getName(), String::compareToIgnoreCase)
                .result()));

        return fieldToModifyingMethods;
    }

    private static Map<String, FieldDocumentation> gatherFields(Class<?> builderClass, RecipeBuilderDescription annotation, String langLocation) {
        Map<String, FieldDocumentation> fields = new HashMap<>();
        List<Field> allFields = getAllFields(builderClass);
        for (Field field : allFields) {
            List<Property> annotations = Stream.of(
                            // Attached to the builder method's requirements field, an uncommon location for specific overrides
                            Arrays.stream(annotation.requirement()).filter(r -> r.property().equals(field.getName())),
                            // Attached to the class, to create/override requirements set in the parent
                            Arrays.stream(builderClass.getAnnotationsByType(Property.class)).filter(r -> r.property().equals(field.getName())),
                            // Attached to the field, the typical place for property information to be created
                            Arrays.stream(field.getAnnotationsByType(Property.class)).filter(r -> {
                                if (r.property().isEmpty() || r.property().equals(field.getName())) return true;
                                GroovyLog.get().warn("Property Annotation had element property '{}' set to a value that wasn't empty or equal to field '{}' in class '{}'.", r.property(), field, builderClass);
                                return false;
                            }))
                    .flatMap(x -> x)
                    .sorted((left, right) -> ComparisonChain.start().compare(left.priority(), right.priority()).result())
                    .collect(Collectors.toList());

            // Get the first field description
            String descriptionLangKey = annotations.stream()
                    .filter(x -> !x.value().isEmpty())
                    .findFirst()
                    .map(Property::value)
                    .orElse(String.format("%s.%s.value", langLocation, field.getName()));

            if (!annotations.isEmpty()) {
                fields.putIfAbsent(field.getName(), new FieldDocumentation(field, annotations, descriptionLangKey));
            }
        }

        return fields;
    }

    private static List<Method> gatherRegistrationMethods(Class<?> builderClass) {
        return Arrays.stream(builderClass.getMethods())
                .filter(x -> x.isAnnotationPresent(RecipeBuilderRegistrationMethod.class))
                // Ensure only the first method with a given name is used
                .filter(distinctByKey(Method::getName))
                .sorted((left, right) -> ComparisonChain.start()
                        .compare(left.getAnnotation(RecipeBuilderRegistrationMethod.class).priority(), right.getAnnotation(RecipeBuilderRegistrationMethod.class).priority())
                        .compare(left.getName().length(), right.getName().length())
                        .compare(left.getName(), right.getName(), String::compareToIgnoreCase)
                        .result())
                .collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Set<Object> seen = new ConcurrentSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

    // Gets all fields of a nested class.
    private static List<Field> getAllFields(Class<?> clazz) {
        Class<?> innerclass = clazz;
        List<Field> fields = new ArrayList<>();
        while (innerclass != Object.class) {
            fields.addAll(Arrays.asList(innerclass.getDeclaredFields()));
            innerclass = innerclass.getSuperclass();
        }
        return fields;
    }

    /**
     * Obtains the normal default value of a class, and allows for properties to avoid having to state the default value unless it is explicitly changed.
     */
    private static String defaultValueConverter(Class<?> clazz) {
        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) return "false";
        if (clazz.equals(Byte.class) || clazz.equals(byte.class)) return "0";
        if (clazz.equals(Character.class) || clazz.equals(char.class)) return "\u0000";
        if (clazz.equals(Double.class) || clazz.equals(double.class)) return "0.0d";
        if (clazz.equals(Float.class) || clazz.equals(float.class)) return "0.0f";
        if (clazz.equals(Integer.class) || clazz.equals(int.class)) return "0";
        if (clazz.equals(Long.class) || clazz.equals(long.class)) return "0";
        if (clazz.equals(Short.class) || clazz.equals(short.class)) return "0";
        if (clazz.equals(String.class)) return "";
        return "null";
    }

    /**
     * Converts a single string into an array of strings, each starting with a '.' that is not contained within any special zones (comment, string, brackets, etc)
     */
    private static List<String> generateParts(String content) {
        ArrayList<String> parts = new ArrayList<>();

        ArrayList<Character> req = new ArrayList<>();
        String next = "";
        int start = 0;
        int index = 0;

        while (index < content.length()) {
            char current = content.charAt(index);

            if (!next.isEmpty()) next = "";
            else if (current == '"') next = "\"";
            else if (current == '\'') next = "'";
            else if (current == '/' && content.charAt(index + 1) == '*') next = "*/";
            else if (current == '(') req.add(')');
            else if (current == '[') req.add(']');
            else if (current == '{') req.add('}');
            else if (!req.isEmpty() && current == req.get(req.size() - 1)) {
                req.remove(req.size() - 1);
            } else if (req.isEmpty() && current == '.' && start != index) {
                parts.add(content.substring(start, index));
                start = index;
            }

            if (next.isEmpty()) index++;
            else index = content.indexOf(next, index + 1);

            if (content.length() == index) parts.add(content.substring(start));
        }

        return parts;
    }

    public String builderAdmonition() {
        if (annotation.example().length == 0) return "";
        return new AdmonitionBuilder()
                .note(new CodeBlockBuilder().line(builder().split("\n")).annotation(annotations()).generate())
                .type(Admonition.Type.EXAMPLE)
                .generate();
    }

    public String builderExampleFile() {
        Matcher matcher = Documentation.ANNOTATION_COMMENT_LOCATION.matcher(builder());
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while (matcher.find()) {
            matcher.appendReplacement(sb, (i < annotations().size()) ? " // " + annotations().get(i) : "");
            i++;
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public String builder() {
        return Arrays.stream(annotation.example()).map(this::createBuilder).collect(Collectors.joining("\n"));
    }

    public List<String> annotations() {
        return Arrays.stream(annotation.example()).flatMap(example -> Arrays.stream(example.annotations())).map(I18n::format).collect(Collectors.toList());
    }

    public String documentMethods() {
        StringBuilder out = new StringBuilder();

        for (Map.Entry<String, FieldDocumentation> fieldDocumentation : fields.entrySet()) {
            if (!fieldDocumentation.getValue().isUsed()) continue;
            Property annotation = fieldDocumentation.getValue().getAnnotation();

            out.append("- ").append(I18n.format(fieldDocumentation.getValue().getDescription())).append(".");

            if (annotation.valid().length != 0) {
                String req = Arrays.stream(annotation.valid())
                        .sorted((left, right) -> ComparisonChain.start().compare(left.type(), right.type()).result())
                        .map(x -> I18n.format(x.type().getKey(), x.value()))
                        .collect(Collectors.joining(String.format(" %s ", I18n.format(annotation.isOr() ? "groovyscript.wiki.or" : "groovyscript.wiki.and"))));
                out.append(" ").append(I18n.format("groovyscript.wiki.requires", req));
            }

            if (annotation.required()) {
                if (!annotation.requirement().isEmpty()) {
                    out.append(" ").append(I18n.format("groovyscript.wiki.requires", I18n.format(annotation.requirement())));
                }
            } else {
                String defaultValue = annotation.defaultValue().isEmpty()
                                      ? defaultValueConverter(fieldDocumentation.getValue().getField().getType())
                                      : annotation.defaultValue();

                out.append(" ").append(I18n.format("groovyscript.wiki.default", defaultValue));
            }

            out.append("\n\n");
            out.append(new CodeBlockBuilder()
                               .line(methods.getOrDefault(fieldDocumentation.getKey(), new ArrayList<>()).stream()
                                             .sorted()
                                             .map(RecipeBuilderMethod::shortMethodSignature)
                                             .distinct()
                                             .collect(Collectors.toList()))
                               .indentation(1)
                               .toString());
        }

        for (Method registerMethod : registrationMethods) {
            out.append("- ").append(I18n.format("groovyscript.wiki.register", registerMethod.getAnnotatedReturnType().getType().getTypeName())).append("\n\n");
            out.append(new CodeBlockBuilder().line(String.format("%s()", registerMethod.getName())).indentation(1).toString());
        }

        return out.toString();
    }

    private String createBuilder(Example example) {
        String content = example.value();
        StringBuilder out = new StringBuilder();

        if (!example.def().isEmpty()) out.append("def ").append(example.def()).append(" = ");

        out.append(reference).append(".").append(builderMethod.getName()).append("()").append("\n");

        for (String part : generateParts(content)) {
            if (!part.isEmpty()) out.append(String.format("    %s\n", part));
        }
        if (!registrationMethods.isEmpty()) out.append("    .").append(String.format("%s()", registrationMethods.get(0).getName())).append("\n");

        return out.toString();
    }


    private static class FieldDocumentation {

        private final Field field;
        private final String descriptionLangKey;
        private final List<Property> annotations;
        private final Property firstAnnotation;

        public FieldDocumentation(Field field, List<Property> annotations, String descriptionLangKey) {
            this.field = field;
            this.descriptionLangKey = descriptionLangKey;
            this.annotations = annotations;
            this.firstAnnotation = annotations.get(0);
        }

        public Field getField() {
            return field;
        }

        public Property getAnnotation() {
            return firstAnnotation;
        }

        public boolean isUsed() {
            return annotations.stream().anyMatch(x -> !x.needsOverride());
        }

        public String getDescription() {
            return descriptionLangKey;
        }

    }


    private static class RecipeBuilderMethod implements Comparable<RecipeBuilderMethod> {

        private final Method method;
        private final RecipeBuilderMethodDescription annotation;

        public RecipeBuilderMethod(Method method) {
            this.method = method;
            this.annotation = method.getAnnotation(RecipeBuilderMethodDescription.class);
        }

        public Method getMethod() {
            return method;
        }

        public RecipeBuilderMethodDescription getAnnotation() {
            return annotation;
        }

        public List<String> targetFields() {
            return getAnnotation().field().length == 0 ? Collections.singletonList(getMethod().getName()) : Arrays.asList(getAnnotation().field());
        }

        public String shortMethodSignature() {
            return String.format("%s(%s)", getMethod().getName(), annotation.signature().isEmpty() ? Exporter.simpleName(getMethod()) : annotation.signature());
        }

        @Override
        public int compareTo(@NotNull Builder.RecipeBuilderMethod right) {
            RecipeBuilderMethod left = this;
            String leftSignature = left.shortMethodSignature();
            String rightSignature = right.shortMethodSignature();
            String leftPart = leftSignature.substring(0, leftSignature.indexOf("("));
            String rightPart = rightSignature.substring(0, rightSignature.indexOf("("));
            return ComparisonChain.start()
                    .compare(left.getAnnotation().priority(), right.getAnnotation().priority())
                    .compare(leftPart.length(), rightPart.length())
                    .compare(leftPart, rightPart, String::compareToIgnoreCase)
                    .compare(leftSignature.length(), rightSignature.length())
                    .compare(leftSignature, rightSignature, String::compareToIgnoreCase)
                    .result();
        }
    }

}
