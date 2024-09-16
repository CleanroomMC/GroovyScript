package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.google.common.collect.ComparisonChain;
import it.unimi.dsi.fastutil.chars.Char2CharArrayMap;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Builder {

    private static final Char2CharMap commaSeparatedParts = new Char2CharArrayMap() {{
        put(']', '[');
        put('\'', '\'');
        defaultReturnValue(Character.MIN_VALUE);
    }};

    private final String reference;
    private final Method builderMethod;
    private final RecipeBuilderDescription annotation;
    private final Map<String, FieldDocumentation> fields;
    private final Map<String, List<RecipeBuilderMethod>> methods;
    private final List<Method> registrationMethods;

    public Builder(Method builderMethod, String reference, String baseTranslationKey) {
        this.builderMethod = builderMethod;
        this.reference = reference;
        this.annotation = builderMethod.getAnnotation(RecipeBuilderDescription.class);
        Class<?> builderClass = builderMethod.getReturnType();
        this.fields = gatherFields(builderClass, annotation, baseTranslationKey);
        this.methods = gatherMethods(builderClass, fields);
        this.registrationMethods = gatherRegistrationMethods(builderClass);
    }

    private static List<Property> getPropertyAnnotationsFromClassRecursive(Class<?> clazz) {
        List<Property> list = new ArrayList<>();
        Collections.addAll(list, clazz.getAnnotationsByType(Property.class));
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) list.addAll(getPropertyAnnotationsFromClassRecursive(superclass));
        return list;
    }

    private static Map<String, FieldDocumentation> gatherFields(Class<?> builderClass, RecipeBuilderDescription annotation, String langLocation) {
        Map<String, FieldDocumentation> fields = new HashMap<>();
        List<Field> allFields = getAllFields(builderClass);
        for (Field field : allFields) {
            List<Property> annotations = Stream.of(
                            // Attached to the builder method's requirements field, an uncommon location for specific overrides
                            Arrays.stream(annotation.requirement()).filter(r -> r.property().equals(field.getName())),
                            // Attached to the class or any parent classes, to create/override requirements set in the parent
                            getPropertyAnnotationsFromClassRecursive(builderClass).stream().filter(r -> r.property().equals(field.getName())),
                            // Attached to the field, the typical place for property information to be created
                            Arrays.stream(field.getAnnotationsByType(Property.class)).filter(r -> {
                                if (r.property().isEmpty() || r.property().equals(field.getName())) return true;
                                GroovyLog.get().warn("Property Annotation had element property '{}' set to a value that wasn't empty or equal to field '{}' in class '{}'.", r.property(), field, builderClass);
                                return false;
                            }))
                    .flatMap(x -> x)
                    .sorted((left, right) -> ComparisonChain.start().compare(left.hierarchy(), right.hierarchy()).result())
                    .collect(Collectors.toList());

            if (!annotations.isEmpty()) {
                fields.putIfAbsent(field.getName(), new FieldDocumentation(field, annotations, langLocation, ""));
            }
        }

        // Also find properties not attached to a field, with rawProperty = true. These have to be attached to the class
        // since there isn't, well, a field to attach them to.
        List<Property> virtualProperties = getPropertyAnnotationsFromClassRecursive(builderClass).stream()
                .filter(Property::virtual)
                .sorted((left, right) -> ComparisonChain.start().compare(left.hierarchy(), right.hierarchy()).result())
                .collect(Collectors.toList());
        Set<String> virtualFieldNames = new HashSet<>();
        for (Property p : virtualProperties) if (p.virtual()) virtualFieldNames.add(p.property());
        for (String s : virtualFieldNames) {
            List<Property> annotations = virtualProperties.stream().filter(r -> r.property().equals(s)).collect(Collectors.toList());
            fields.putIfAbsent(s, new FieldDocumentation(null, annotations, langLocation, s));
        }

        return fields;
    }

    private static Map<String, List<RecipeBuilderMethod>> gatherMethods(Class<?> builderClass, Map<String, FieldDocumentation> fields) {
        Map<String, List<RecipeBuilderMethod>> fieldToModifyingMethods = new HashMap<>();
        for (Method method : builderClass.getMethods()) {
            if (!method.isAnnotationPresent(RecipeBuilderMethodDescription.class)) continue;
            RecipeBuilderMethod pair = new RecipeBuilderMethod(method);
            for (String target : pair.targetFields()) {
                if (fields.get(target) != null && fields.get(target).isIgnoringInheritedMethods() && !Arrays.asList(builderClass.getDeclaredMethods()).contains(method)) {
                    continue;
                }
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

    private static List<Method> gatherRegistrationMethods(Class<?> builderClass) {
        return Arrays.stream(builderClass.getMethods())
                .filter(x -> x.isAnnotationPresent(RecipeBuilderRegistrationMethod.class))
                .sorted((left, right) -> ComparisonChain.start()
                        .compare(left.getAnnotation(RecipeBuilderRegistrationMethod.class).hierarchy(), right.getAnnotation(RecipeBuilderRegistrationMethod.class).hierarchy())
                        // Specifically de-prioritize Object classes
                        .compareFalseFirst(left.getReturnType() == Object.class, right.getReturnType() == Object.class)
                        .result())
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
        Set<Object> seen = ConcurrentHashMap.newKeySet();
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
     * Converts a single string into an array of strings.
     * Groups of code surrounded by square braces (`[]`) or single quotes (`''`) are split onto separate lines.
     * Otherwise, each line starts with a period (`.`) provided that it is not contained within any special zones (comment, string, brackets, etc)
     */
    private static List<String> generateParts(String content) {
        List<String> parts = new ArrayList<>();

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
            } else if (current == ',' && index > 0) {
                char nextChar = commaSeparatedParts.get(content.charAt(index - 1));
                if (nextChar != Character.MIN_VALUE && (content.charAt(index + 1) == nextChar || content.charAt(index + 2) == nextChar)) {
                    int point = content.indexOf(nextChar, index + 1);
                    parts.add(content.substring(start, point));
                    start = point;
                }
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

    @NotNull
    private static List<String> getOutputs(List<String> parts) {
        List<String> output = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i);
            if (!part.isEmpty()) {
                int indent = 4;
                if (!output.isEmpty()) {
                    int lastIndex = output.get(i - 1).indexOf(part.charAt(0));
                    if (lastIndex != -1) indent = lastIndex;
                }
                output.add(StringUtils.repeat(" ", indent) + part.trim() + "\n");
            }
        }
        return output;
    }

    public String builderAdmonition() {
        if (annotation.example().length == 0) return "";
        return new AdmonitionBuilder()
                .note(new CodeBlockBuilder().line(builder(false).split("\n")).annotation(annotations()).generate())
                .type(Admonition.Type.EXAMPLE)
                .indentation(1)
                .generate();
    }

    public String builderExampleFile() {
        Matcher matcher = Documentation.ANNOTATION_COMMENT_LOCATION.matcher(builder(true));
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while (matcher.find()) {
            matcher.appendReplacement(sb, (i < annotations().size()) ? " // " + annotations().get(i) : "");
            i++;
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public String builder(boolean canBeCommented) {
        return Arrays.stream(annotation.example()).map(x -> createBuilder(x, canBeCommented)).collect(Collectors.joining("\n"));
    }

    public List<String> annotations() {
        return Arrays.stream(annotation.example()).flatMap(example -> Arrays.stream(example.annotations())).map(Documentation::translate).collect(Collectors.toList());
    }

    public String documentMethods() {
        StringBuilder out = new StringBuilder();

        methods.keySet().forEach(target -> {
            if (fields.containsKey(target)) return;
            GroovyLog.get().warn("Couldn't find field '{}' referenced in a method used for recipe builder '{}'", target, reference);
        });

        if (fields.values().stream().anyMatch(FieldDocumentation::isUsed)) {
            out.append(documentFields());
        } else {
            GroovyLog.get().warn("Couldn't find any fields being used for recipe builder '{}'", reference);
        }

        if (registrationMethods.isEmpty()) {
            GroovyLog.get().warn("Couldn't find any registration methods for recipe builder '{}'", reference);
        } else {
            for (Method registerMethod : registrationMethods) {
                out.append("- ");
                String returnType = registerMethod.getAnnotatedReturnType().getType().getTypeName();
                if ("void".equals(returnType) || "null".equals(returnType)) out.append(I18n.format("groovyscript.wiki.register"));
                else out.append(I18n.format("groovyscript.wiki.register_return", returnType));
                out.append("\n\n");
                out.append(new CodeBlockBuilder().line(String.format("%s()", registerMethod.getName())).indentation(1).toString());
            }
        }

        return out.toString();
    }

    public StringBuilder documentFields() {
        StringBuilder out = new StringBuilder();
        fields.values().stream()
                .sorted()
                .filter(FieldDocumentation::isUsed)
                .forEach(fieldDocumentation -> {

                    out.append(fieldDocumentation.getDescription());

                    if (fieldDocumentation.hasComparison()) {
                        out.append(" ").append(I18n.format("groovyscript.wiki.requires", fieldDocumentation.getComparison()));
                    }

                    if (fieldDocumentation.hasRequirement()) {
                        out.append(" ").append(I18n.format("groovyscript.wiki.requires", fieldDocumentation.getRequirement()));
                    }

                    if (fieldDocumentation.hasDefaultValue()) {
                        out.append(" ").append(I18n.format("groovyscript.wiki.default", fieldDocumentation.getDefaultValue()));
                    }

                    out.append("\n\n");

                    List<RecipeBuilderMethod> recipeBuilderMethods = methods.get(fieldDocumentation.getFieldName());

                    if (recipeBuilderMethods == null || recipeBuilderMethods.isEmpty()) {
                        GroovyLog.get().warn("Couldn't find any methods targeting field '{}' in recipe builder '{}'", fieldDocumentation.getFieldName(), reference);
                    } else {
                        out.append(new CodeBlockBuilder()
                                           .line(recipeBuilderMethods.stream()
                                                         .sorted()
                                                         .map(RecipeBuilderMethod::shortMethodSignature)
                                                         .distinct()
                                                         .collect(Collectors.toList()))
                                           .indentation(1)
                                           .toString());
                    }
                });
        return out;
    }

    private String createBuilder(Example example, boolean canBeCommented) {
        StringBuilder out = new StringBuilder();

        if (canBeCommented && example.commented()) out.append("/*");

        if (!example.def().isEmpty()) out.append("def ").append(example.def()).append(" = ");

        out.append(reference);

        if (!example.raw()) out.append(".").append(builderMethod.getName()).append("()");

        if (!example.value().isEmpty() || !registrationMethods.isEmpty()) out.append("\n");

        out.append(String.join("", getOutputs(generateParts(example.value()))));

        if (!registrationMethods.isEmpty()) out.append("    .").append(String.format("%s()", registrationMethods.get(0).getName()));

        if (canBeCommented && example.commented()) out.append("*/");

        out.append("\n");

        return out.toString();
    }

    private static class FieldDocumentation implements Comparable<FieldDocumentation> {

        private final @Nullable Field field;
        private final String nullFieldName;
        private final String langLocation;
        private final List<Property> annotations;
        private final Property firstAnnotation;

        public FieldDocumentation(@Nullable Field field, List<Property> annotations, String langLocation, String nullFieldName) {
            this.field = field;
            this.langLocation = langLocation;
            this.annotations = annotations;
            this.firstAnnotation = annotations.get(0);
            this.nullFieldName = nullFieldName;
        }

        public Property getAnnotation() {
            return firstAnnotation;
        }

        public int priority() {
            return annotations.stream().map(Property::priority).filter(x -> x != 1000).findFirst().orElse(1000);
        }

        public String getLangKey() {
            return annotations.stream()
                    .map(Property::value)
                    .filter(value -> !value.isEmpty())
                    .findFirst()
                    .orElse(String.format("%s.%s.value", langLocation, field != null ? field.getName() : nullFieldName));
        }

        public boolean hasDefaultValue() {
            return !"null".equals(getDefaultValue()) && !getDefaultValue().isEmpty();
        }

        public String getDefaultValue() {
            return annotations.stream()
                    .filter(x -> !x.defaultValue().isEmpty())
                    .findFirst()
                    .map(Property::defaultValue)
                    .orElse(field != null ? defaultValueConverter(field.getType()) : "unknown");
        }

        public String getValue() {
            return annotations.stream().filter(x -> !x.value().isEmpty()).findFirst().map(Property::value).orElse("");
        }

        public boolean hasComparison() {
            return annotations.stream().anyMatch(x -> x.valid().length != 0);
        }

        public String getComparison() {
            Optional<Comp[]> comparison = annotations.stream().map(Property::valid).filter(valid -> valid.length != 0).findFirst();
            if (!comparison.isPresent()) return "";
            return Arrays.stream(comparison.get())
                    .sorted((left, right) -> ComparisonChain.start().compare(left.type(), right.type()).result())
                    .map(x -> Documentation.translate(x.type().getKey(), x.value()))
                    .collect(Collectors.joining(String.format(" %s ", I18n.format("groovyscript.wiki.and"))));
        }

        public boolean hasRequirement() {
            return annotations.stream().map(Property::requirement).anyMatch(x -> !x.isEmpty());
        }

        public String getRequirement() {
            return annotations.stream()
                    .map(Property::requirement)
                    .filter(x -> !x.isEmpty())
                    .findFirst()
                    .map(Documentation::translate)
                    .orElse("");
        }

        public boolean isIgnoringInheritedMethods() {
            return getAnnotation().ignoresInheritedMethods();
        }

        public boolean isUsed() {
            return !getAnnotation().needsOverride();
        }

        private String getFieldTypeInlineCode() {
            //return "`#!groovy " + Exporter.simpleSignature(getField().getAnnotatedType().getType().getTypeName()) + "`. ";
            if (field == null) return "";
            return "`" + Exporter.simpleSignature(field.getAnnotatedType().getType().getTypeName()) + "`. ";
        }

        public String getDescription() {
            return "- " + getFieldTypeInlineCode() + Documentation.ensurePeriod(Documentation.translate(getLangKey()));
        }

        @Override
        public int compareTo(@NotNull FieldDocumentation comp) {
            return ComparisonChain.start()
                    .compare(this.priority(), comp.priority())
                    .compare(this.getFieldLength(), comp.getFieldLength())
                    .compare(this.getFieldName(), comp.getFieldName(), String::compareToIgnoreCase)
                    .result();
        }

        public int getFieldLength() {
            if (field == null) return 0;
            return field.getName().length();
        }

        public String getFieldName() {
            if (field == null) return nullFieldName;
            return field.getName();
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
            return String.format("%s(%s)", getMethod().getName(), Exporter.simpleSignature(getMethod()));
        }

        @Override
        public int compareTo(@NotNull RecipeBuilderMethod comp) {
            String thisSignature = this.shortMethodSignature();
            String compSignature = comp.shortMethodSignature();
            String thisPart = thisSignature.substring(0, thisSignature.indexOf("("));
            String compPart = compSignature.substring(0, compSignature.indexOf("("));
            return ComparisonChain.start()
                    .compare(this.getAnnotation().priority(), comp.getAnnotation().priority())
                    .compare(thisPart.length(), compPart.length())
                    .compare(thisPart, compPart, String::compareToIgnoreCase)
                    .compare(thisSignature.length(), compSignature.length())
                    .compare(thisSignature, compSignature, String::compareToIgnoreCase)
                    .result();
        }
    }

}
