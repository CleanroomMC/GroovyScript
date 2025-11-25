package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.documentation.helper.AdmonitionBuilder;
import com.cleanroommc.groovyscript.documentation.helper.CodeBlockBuilder;
import com.cleanroommc.groovyscript.documentation.helper.ComparisonHelper;
import com.cleanroommc.groovyscript.documentation.helper.LangHelper;
import com.cleanroommc.groovyscript.documentation.helper.descriptor.DescriptorHelper;
import com.cleanroommc.groovyscript.documentation.helper.descriptor.MethodAnnotation;
import it.unimi.dsi.fastutil.chars.Char2CharArrayMap;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Builder {

    private static final Char2CharMap commaSeparatedParts = new Char2CharArrayMap() {

        {
            put(']', '[');
            put('\'', '\'');
            defaultReturnValue(Character.MIN_VALUE);
        }
    };

    private final String location;
    private final String langLocation;
    private final Method builderMethod;
    private final RecipeBuilderDescription annotation;
    private final Map<String, FieldDocumentation> fields;
    private final Map<String, List<MethodAnnotation<RecipeBuilderMethodDescription>>> methods;
    private final List<MethodAnnotation<RecipeBuilderRegistrationMethod>> registrationMethods;

    public Builder(Method builderMethod, RecipeBuilderDescription annotation, String location, String langLocation) {
        this.builderMethod = builderMethod;
        this.location = location;
        this.langLocation = langLocation;
        this.annotation = annotation;
        Class<?> builderClass = annotation.clazz() == void.class ? builderMethod.getReturnType() : annotation.clazz();
        var methodSignatures = generateOfClass(builderClass, annotation);
        this.fields = gatherFields(builderClass, annotation, langLocation);
        this.methods = gatherMethods(methodSignatures, fields);
        this.registrationMethods = gatherRegistrationMethods(methodSignatures);
    }

    private static DescriptorHelper.OfClass generateOfClass(Class<?> clazz, RecipeBuilderDescription annotation) {
        var methodSignatures = DescriptorHelper.generateOfClass(clazz);
        Consumer<RecipeBuilderOverride> consumer = override -> {
            for (var entry : override.method()) {
                methodSignatures.addAnnotation(entry, entry.method());
            }
            for (var entry : override.register()) {
                methodSignatures.addAnnotation(entry, entry.method());
            }
        };
        if (annotation != null) consumer.accept(annotation.override());
        getAnnotationsFromClassRecursive(RecipeBuilderOverride.class, clazz).forEach(consumer);
        return methodSignatures;
    }

    private static <T extends Annotation> List<T> getAnnotationsFromClassRecursive(Class<T> annotation, Class<?> clazz) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, clazz.getAnnotationsByType(annotation));
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) list.addAll(getAnnotationsFromClassRecursive(annotation, superclass));
        return list;
    }

    private static Map<String, FieldDocumentation> gatherFields(Class<?> builderClass, RecipeBuilderDescription annotation, String langLocation) {
        Map<String, FieldDocumentation> fields = new HashMap<>();
        List<Field> allFields = getAllFields(builderClass);
        // Attached to the class or any parent classes, to create/override requirements set in the parent
        List<Property> classProperties = new ArrayList<>(getAnnotationsFromClassRecursive(Property.class, builderClass));
        // Part of an override attached to the class or any parent classes, to create/override requirements set in the parent
        for (var rbo : getAnnotationsFromClassRecursive(RecipeBuilderOverride.class, builderClass)) {
            Collections.addAll(classProperties, rbo.requirement());
        }
        for (Field field : allFields) {
            List<Property> annotations = Stream.of(
                    // (deprecated) Attached to the builder method's requirements field, an uncommon location for specific overrides
                    Arrays.stream(annotation.requirement()).filter(r -> r.property().equals(field.getName())),
                    // Attached to the builder method's override of the requirements field, an uncommon location for specific overrides
                    Arrays.stream(annotation.override().requirement()).filter(r -> r.property().equals(field.getName())),
                    // Attached to the class or any parent classes either directly or via RecipeBuilderOverride, to create/override requirements set in the parent
                    classProperties.stream().filter(r -> r.property().equals(field.getName())),
                    // Attached to the field, the typical place for property information to be created
                    Arrays.stream(field.getAnnotationsByType(Property.class)).filter(r -> {
                        if (r.property().isEmpty() || r.property().equals(field.getName())) return true;
                        GroovyLog.get().warn("Property Annotation had element property '{}' set to a value that wasn't empty or equal to field '{}' in class '{}'.", r.property(), field, builderClass);
                        return false;
                    }))
                    .flatMap(x -> x)
                    .sorted(ComparisonHelper::property)
                    .collect(Collectors.toList());

            if (!annotations.isEmpty()) {
                fields.putIfAbsent(field.getName(), new FieldDocumentation(field, annotations, langLocation));
            }
        }

        return fields;
    }

    private static Map<String, List<MethodAnnotation<RecipeBuilderMethodDescription>>> gatherMethods(DescriptorHelper.OfClass methodSignatures, Map<String, FieldDocumentation> fields) {
        Map<String, List<MethodAnnotation<RecipeBuilderMethodDescription>>> fieldToModifyingMethods = new HashMap<>();

        BiPredicate<Method, String> isDocumented = (method, fieldName) -> {
            if (method.getDeclaringClass() == methodSignatures.getClazz()) return true;
            var field = fields.get(fieldName);
            return field == null || !field.isIgnoringInheritedMethods();
        };

        for (var method : methodSignatures.getMethods(RecipeBuilderMethodDescription.class)) {
            if (method.annotation().field().length == 0) {
                var field = method.method().getName();
                if (isDocumented.test(method.method(), field)) {
                    fieldToModifyingMethods.computeIfAbsent(field, k -> new ArrayList<>()).add(method);
                }
            } else {
                for (var field : method.annotation().field()) {
                    if (isDocumented.test(method.method(), field)) {
                        fieldToModifyingMethods.computeIfAbsent(field, k -> new ArrayList<>()).add(method);
                    }
                }
            }
        }
        fieldToModifyingMethods.values().forEach(value -> value.sort(ComparisonHelper::recipeBuilderMethod));
        return fieldToModifyingMethods;
    }

    private static List<MethodAnnotation<RecipeBuilderRegistrationMethod>> gatherRegistrationMethods(DescriptorHelper.OfClass methodSignatures) {
        return methodSignatures.getMethods(RecipeBuilderRegistrationMethod.class)
                .stream()
                .sorted(ComparisonHelper::recipeBuilderRegistrationHierarchy)
                // Ensure only the first method with a given name is used
                .filter(distinctByKey(x -> x.method().getName()))
                .sorted(ComparisonHelper::recipeBuilderRegistration)
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
     * Converts a single string into an array of strings.
     * Groups of code surrounded by square braces (`[]`) or single quotes (`''`) are split onto separate lines.
     * Otherwise, each line starts with a period (`.`) provided that it is not contained within any special zones (comment, string, brackets, etc.)
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

    private static @NotNull List<String> getOutputs(List<String> parts) {
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

    public String builderExampleAdmonition() {
        if (annotation.example().length == 0) return "";
        return new AdmonitionBuilder()
                .note(new CodeBlockBuilder().line(builder(false).split("\n")).annotation(annotations()).generate())
                .type(Admonition.Type.EXAMPLE)
                .indentation(1)
                .generate();
    }

    @SuppressWarnings("StringBufferMayBeStringBuilder")
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
        return Arrays.stream(annotation.example()).flatMap(example -> Arrays.stream(example.annotations())).map(LangHelper::translate).collect(Collectors.toList());
    }

    public String documentMethods() {
        StringBuilder out = new StringBuilder();

        methods.keySet().forEach(target -> {
            if (fields.containsKey(target)) return;
            GroovyLog.get().warn("Couldn't find field '{}' referenced in a method used for recipe builder '{}'", target, location);
        });

        if (fields.values().stream().anyMatch(FieldDocumentation::isUsed)) {
            out.append(documentFields());
        } else {
            GroovyLog.get().warn("Couldn't find any fields being used for recipe builder '{}'", location);
        }

        return out.toString();
    }

    public String documentRegistration() {
        if (registrationMethods.isEmpty()) {
            GroovyLog.get().warn("Couldn't find any registration methods for recipe builder '{}'", location);
            return "";
        }
        StringBuilder out = new StringBuilder();
        for (var registerMethod : registrationMethods) {
            out.append("- ");
            var desc = registerMethod.annotation().description();
            if (desc.isEmpty()) {
                String returnType = registerMethod.method().getAnnotatedReturnType().getType().getTypeName();
                boolean hasNoReturnValue = "void".equals(returnType) || "null".equals(returnType);
                out.append(
                        hasNoReturnValue
                                ? I18n.format("groovyscript.wiki.recipe_builder.register")
                                : I18n.format("groovyscript.wiki.recipe_builder.register_return", returnType));
            } else out.append(LangHelper.translate(desc));
            out.append("\n\n");
            out.append(new CodeBlockBuilder().line(String.format("%s()", registerMethod.method().getName())).indentation(1).toString());
        }
        return out.toString();
    }

    public StringBuilder documentFields() {
        StringBuilder out = new StringBuilder();
        var list = new ArrayList<>(fields.values());
        list.sort(ComparisonHelper::field);
        for (FieldDocumentation fieldDocumentation : list) {
            if (!fieldDocumentation.isUsed()) continue;

            out.append(fieldDocumentation.getDescription());

            out.append(FieldDocumentation.documentValue(fieldDocumentation::hasComparison, fieldDocumentation::getComparison, "groovyscript.wiki.requires"));
            out.append(FieldDocumentation.documentValue(fieldDocumentation::hasRequirement, fieldDocumentation::getRequirement, "groovyscript.wiki.requires"));
            out.append(FieldDocumentation.documentValue(fieldDocumentation::hasDefaultValue, fieldDocumentation::getDefaultValue, "groovyscript.wiki.default"));

            out.append("\n\n");

            var recipeBuilderMethods = methods.get(fieldDocumentation.getField().getName());

            if (recipeBuilderMethods == null || recipeBuilderMethods.isEmpty()) {
                GroovyLog.get().warn("Couldn't find any methods targeting field '{}' in recipe builder '{}'", fieldDocumentation.getField().getName(), location + "." + builderMethod.getName());
                continue;
            }
            var lines = recipeBuilderMethods.stream()
                    .sorted(ComparisonHelper::recipeBuilderMethod)
                    .map(x -> {
                        var desc = x.annotation().description();
                        var method = DescriptorHelper.shortSignature(x.method());
                        if (desc.isEmpty()) return method;
                        return method + " // " + LangHelper.translate(desc);
                    })
                    .distinct()
                    .collect(Collectors.toList());
            out.append(new CodeBlockBuilder().line(lines).indentation(1).toString());
        }
        return out;
    }

    public String generateAdmonition() {
        var admonition = new AdmonitionBuilder();
        admonition.type(Admonition.Type.ABSTRACT).hasTitle(true).title(title());

        // the --- creates a page break indicator, which is used to separate blocks - but it should only do so for blocks that exist.
        Consumer<String> addBlock = block -> {
            if (!block.isEmpty()) admonition.note("\n").note("---").note("\n").note(block.split("\n"));
        };

        addBlock.accept(creationMethod());
        addBlock.accept(documentMethods());
        addBlock.accept(documentRegistration());
        addBlock.accept(builderExampleAdmonition());
        admonition.note("\n");
        return admonition.generate();
    }

    public boolean hasComplexMethod() {
        return builderMethod.getParameterTypes().length != 0;
    }

    private String title() {
        String lang = annotation.title();

        if (lang.isEmpty()) {
            lang = LangHelper.fallback(
                    String.format("%s.%s.title", langLocation, builderMethod.getName()),
                    String.format("%s.recipe_builder.title", Registry.BASE_LANG_LOCATION));
        }

        return LangHelper.translate(lang);
    }

    public String creationMethod() {
        StringBuilder out = new StringBuilder();
        String lang = annotation.description();

        if (lang.isEmpty()) {
            // If the method is complex, we want to require defining the key via the annotation or as the registryDefault.
            String registryDefault = String.format("%s.%s.description", langLocation, builderMethod.getName());
            if (hasComplexMethod()) lang = registryDefault;
            else lang = LangHelper.fallback(registryDefault, String.format("%s.recipe_builder.description", Registry.BASE_LANG_LOCATION));
        }

        var example = location + "." + DescriptorHelper.shortSignature(builderMethod);

        out.append("- ").append(LangHelper.ensurePeriod(LangHelper.translate(lang))).append("\n\n");
        out.append(new CodeBlockBuilder().line(example).indentation(1).toString());
        return out.toString();
    }

    private String createBuilder(Example example, boolean canBeCommented) {
        StringBuilder out = new StringBuilder();
        var methodLocation = location + "." + builderMethod.getName();
        var error = GroovyLog.msg("Error creating example for " + methodLocation).error();

        var prependComment = canBeCommented && example.commented();

        if (prependComment) out.append("//");

        if (!example.def().isEmpty()) out.append("def ").append(example.def()).append(" = ");


        out.append(methodLocation);

        var exampleText = String.join("", getOutputs(generateParts(example.value())));

        if (exampleText.isEmpty()) {
            out.append("()").append("\n");
        } else {
            if (hasComplexMethod()) {
                if (!example.value().startsWith("(")) {
                    error.add("provided example for a recipe builder with parameters did not begin with a '('").post();
                    return "";
                }
                // trim to remove the starting indent so the builder method doesn't have 4 spaces between start
                out.append(exampleText.trim()).append("\n");
            } else {
                if (!example.value().startsWith(".")) {
                    error.add("provided example for a recipe builder without parameters did not begin with a '.'").post();
                    return "";
                }
                out.append("()").append("\n").append(exampleText);
            }
        }


        if (!registrationMethods.isEmpty()) out.append("    .").append(String.format("%s()", registrationMethods.get(0).method().getName()));

        var exampleMethod = prependComment ? out.toString().replace("\n", "\n//") : out.toString();

        return exampleMethod + "\n";
    }

    public static class FieldDocumentation {

        private static final Function<List<String>, String> SERIAL_COMMA_LIST = list -> {
            int last = list.size() - 1;
            if (last < 1) return String.join("", list);
            var and = " " + I18n.format("groovyscript.wiki.and") + " ";
            if (last == 1) return String.join(and, list);
            return String.join("," + and, String.join(", ", list.subList(0, last)), list.get(last));
        };

        private final Field field;
        private final String langLocation;
        private final List<Property> annotations;
        private final Property firstAnnotation;

        public FieldDocumentation(Field field, List<Property> annotations, String langLocation) {
            this.field = field;
            this.langLocation = langLocation;
            this.annotations = annotations;
            this.firstAnnotation = annotations.get(0);
        }

        private static String documentValue(BooleanSupplier check, Supplier<String> string, String langKey) {
            if (check.getAsBoolean()) {
                var value = string.get();
                if (!value.isEmpty()) return " " + I18n.format(langKey, value);
            }
            return "";
        }

        private static String parseComparisonRequirements(Comp comp, EnumSet<Comp.Type> usedTypes) {
            return usedTypes.stream().sorted().map(type -> LangHelper.translate(type.getKey(), switch (type) {
                case GT -> comp.gt();
                case GTE -> comp.gte();
                case EQ -> comp.eq();
                case LTE -> comp.lte();
                case LT -> comp.lt();
                case NOT -> comp.not();
                case UNI -> LangHelper.translate(comp.unique());
            })).collect(Collectors.collectingAndThen(Collectors.toList(), SERIAL_COMMA_LIST));
        }

        public Field getField() {
            return field;
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
                    .orElse(String.format("%s.%s.value", langLocation, field.getName()));
        }

        public boolean hasDefaultValue() {
            return !"null".equals(getDefaultValue()) && !getDefaultValue().isEmpty();
        }

        public String getDefaultValue() {
            return annotations.stream()
                    .filter(x -> !x.defaultValue().isEmpty())
                    .findFirst()
                    .map(Property::defaultValue)
                    .orElse(DescriptorHelper.defaultValueConverter(getField().getType()));
        }

        public String getValue() {
            return annotations.stream().filter(x -> !x.value().isEmpty()).findFirst().map(Property::value).orElse("");
        }

        public boolean hasComparison() {
            return true; //annotations.stream().anyMatch(x -> x.comp().types().length != 0 || x.valid().length != 0);
        }

        @SuppressWarnings({
                "deprecation", "SimplifyOptionalCallChains"
        })
        public String getComparison() {
            Optional<Comp[]> comparison = annotations.stream().map(Property::valid).filter(valid -> valid.length != 0).findFirst();
            if (!comparison.isPresent()) {
                for (var property : annotations) {
                    var usedTypes = Comp.Type.getUsedTypes(property.comp());
                    if (!usedTypes.isEmpty()) {
                        return FieldDocumentation.parseComparisonRequirements(property.comp(), usedTypes);
                    }
                }
                return "";
            }
            return Arrays.stream(comparison.get())
                    .sorted(ComparisonHelper::comp)
                    .map(x -> LangHelper.translate(x.type().getKey(), x.value()))
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
                    .map(LangHelper::translate)
                    .orElse("");
        }

        public boolean isIgnoringInheritedMethods() {
            return getAnnotation().ignoresInheritedMethods();
        }

        public boolean isUsed() {
            return !getAnnotation().needsOverride();
        }

        private String getFieldTypeInlineCode() {
            //return "`#!groovy " + DescriptorHelper.simpleTypeName(getField().getAnnotatedType().getType().getTypeName()) + "`. ";
            return "`" + DescriptorHelper.simpleTypeName(getField().getAnnotatedType().getType().getTypeName()) + "`. ";
        }

        public String getDescription() {
            return "- " + getFieldTypeInlineCode() + LangHelper.ensurePeriod(LangHelper.translate(getLangKey()));
        }
    }
}
