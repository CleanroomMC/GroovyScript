package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.helper.DescriptorHelper;
import com.cleanroommc.groovyscript.documentation.linkgenerator.LinkGeneratorHooks;
import com.google.common.collect.ComparisonChain;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.text.WordUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Registry {

    private static final Pattern PERIOD_END_PATTERN = Pattern.compile("\\.$");
    private final GroovyContainer<? extends GroovyPropertyContainer> mod;
    private final INamed registry;
    private final String baseTranslationKey;
    private final String reference;
    private final Class<?> registryClass;
    private final RegistryDescription description;
    private final Map<String, String> types;
    private final List<DescriptorHelper.MethodAnnotation<RecipeBuilderDescription>> recipeBuilderMethods;
    private final EnumMap<MethodDescription.Type, List<DescriptorHelper.MethodAnnotation<MethodDescription>>> methods = new EnumMap<>(MethodDescription.Type.class);
    private final List<String> imports = new ArrayList<>();

    public Registry(GroovyContainer<? extends GroovyPropertyContainer> mod, INamed registry) {
        this.mod = mod;
        this.registry = registry;
        this.baseTranslationKey = String.format("groovyscript.wiki.%s.%s", mod.getModId(), registry.getName());
        this.reference = String.format("mods.%s.%s", mod.getModId(), registry.getName());
        this.registryClass = registry.getClass();
        this.description = registryClass.getAnnotation(RegistryDescription.class);
        this.types = generateTypes(registryClass);
        var methodSignatures = generateOfClass(registryClass);

        List<DescriptorHelper.MethodAnnotation<RecipeBuilderDescription>> recipeBuilderMethods = new ArrayList<>();
        Map<MethodDescription.Type, List<DescriptorHelper.MethodAnnotation<MethodDescription>>> methods = new EnumMap<>(MethodDescription.Type.class);
        for (MethodDescription.Type value : MethodDescription.Type.values()) methods.put(value, new ArrayList<>());

        for (var entry : methodSignatures.getMethods(RecipeBuilderDescription.class)) {
            recipeBuilderMethods.add(entry);
            addImports(entry.getAnnotation().example());
        }
        for (var entry : methodSignatures.getMethods(MethodDescription.class)) {
            methods.get(entry.getAnnotation().type()).add(entry);
            addImports(entry.getAnnotation().example());
        }

        this.recipeBuilderMethods = sortGrSRecipeBuilderDescriptionMethods(recipeBuilderMethods);
        methods.forEach((k, v) -> this.methods.put(k, sortGrSMethodDescriptionMethods(v)));
    }

    /**
     * @return gathers the generic information and pairs the generic type symbol with the type being used as the generic.
     */
    private static Map<String, String> generateTypes(Class<?> registryClass) {
        var types = new HashMap<String, String>();
        if (registryClass.getGenericSuperclass() instanceof ParameterizedType parameterizedType) {
            if (parameterizedType.getRawType() instanceof Class<?>typeClass) {
                var parameters = typeClass.getTypeParameters();
                var args = parameterizedType.getActualTypeArguments();
                for (int i = 0; i < parameters.length && i < args.length; i++) {
                    types.put(parameters[i].toString(), args[i].getTypeName());
                }
            }
        }
        return types;
    }

    private static DescriptorHelper.OfClass generateOfClass(Class<?> clazz) {
        var methodSignatures = DescriptorHelper.generateOfClass(clazz);
        var description = clazz.getAnnotation(RegistryDescription.class);
        if (description != null) {
            var override = description.override();
            for (var annotation : override.method()) {
                methodSignatures.addAnnotation(methodSignatures.getMethod(annotation.method()), annotation);
            }
            for (var annotation : override.recipeBuilder()) {
                methodSignatures.addAnnotation(methodSignatures.getMethod(annotation.method()), annotation);
            }
        }
        return methodSignatures;
    }

    private static List<DescriptorHelper.MethodAnnotation<RecipeBuilderDescription>> sortGrSRecipeBuilderDescriptionMethods(List<DescriptorHelper.MethodAnnotation<RecipeBuilderDescription>> methods) {
        return methods.stream()
                .sorted(
                        (left, right) -> ComparisonChain.start()
                                .compare(left.getAnnotation().priority(), right.getAnnotation().priority())
                                .compare(left.getMethod().getName(), right.getMethod().getName(), String::compareToIgnoreCase)
                                .compare(Exporter.simpleSignature(left.getMethod()), Exporter.simpleSignature(right.getMethod()), String::compareToIgnoreCase)
                                .result())
                .collect(Collectors.toList());
    }

    private static List<DescriptorHelper.MethodAnnotation<MethodDescription>> sortGrSMethodDescriptionMethods(List<DescriptorHelper.MethodAnnotation<MethodDescription>> methods) {
        return methods.stream()
                .sorted(
                        (left, right) -> ComparisonChain.start()
                                .compare(left.getAnnotation().priority(), right.getAnnotation().priority())
                                .compare(left.getMethod().getName(), right.getMethod().getName(), String::compareToIgnoreCase)
                                .compare(Exporter.simpleSignature(left.getMethod()), Exporter.simpleSignature(right.getMethod()), String::compareToIgnoreCase)
                                .result())
                .collect(Collectors.toList());
    }

    private static List<Example> getExamples(DescriptorHelper.MethodAnnotation<MethodDescription> method) {
        return new ArrayList<>(Arrays.asList(method.getAnnotation().example()));
    }

    private static List<Example> sortExamples(List<Example> examples) {
        examples.sort(
                (left, right) -> ComparisonChain.start()
                        .compare(left.priority(), right.priority())
                        .compareFalseFirst(left.commented(), right.commented())
                        .compare(left.value().length(), right.value().length())
                        .compare(left.value(), right.value())
                        .result());
        return examples;
    }

    private void addImports(Example example) {
        Collections.addAll(imports, example.imports());
    }

    private void addImports(Example... examples) {
        for (var example : examples) addImports(example);
    }

    public List<String> getImports() {
        return this.imports;
    }

    public String getFileSourceCodeLink() {
        return LinkGeneratorHooks.convert(description.linkGenerator(), registryClass);
    }

    public String getTitle() {
        return Documentation.translate(description.title().isEmpty() ? String.format("%s.title", baseTranslationKey) : description.title());
    }

    public String getDescription() {
        return Documentation.ensurePeriod(
                Documentation.translate(description.description().isEmpty() ? String.format("%s.description", baseTranslationKey) : description.description())
                        .replace("\"", "\\\""));
    }

    public String exampleBlock() {
        StringBuilder out = new StringBuilder();
        out.append("// ").append(getTitle()).append(":").append("\n");
        out.append("// ").append(WordUtils.wrap(getDescription(), Documentation.MAX_LINE_LENGTH, "\n// ", false)).append("\n\n");
        out.append(documentMethodDescriptionType(MethodDescription.Type.REMOVAL));
        for (var method : recipeBuilderMethods) out.append(new Builder(method.getMethod(), method.getAnnotation(), reference, baseTranslationKey).builderExampleFile()).append("\n");
        if (!recipeBuilderMethods.isEmpty()) out.append("\n");
        out.append(documentMethodDescriptionType(MethodDescription.Type.ADDITION));
        out.append(documentMethodDescriptionType(MethodDescription.Type.VALUE));
        return out.toString();
    }

    private String documentMethodDescriptionType(MethodDescription.Type type) {
        StringBuilder out = new StringBuilder();
        var hasExamples = false;
        for (var entry : methods.get(type)) {
            var examples = examples(entry);
            out.append(examples);
            if (!hasExamples && !examples.isEmpty()) hasExamples = true;
        }
        if (hasExamples) out.append("\n");
        return out.toString();
    }

    private String generateHeader() {
        StringBuilder out = new StringBuilder();
        out.append("---\n").append("title: \"").append(getTitle()).append("\"\n");
        if (Documentation.DEFAULT_FORMAT.hasTitleTemplate()) out.append("titleTemplate: \"").append(mod).append(" | CleanroomMC").append("\"\n");
        out.append("description: \"").append(getDescription()).append("\"\n");
        String link = getFileSourceCodeLink();
        if (!link.isEmpty()) out.append("source_code_link: \"").append(link).append("\"\n");
        out.append("---\n\n");
        return out.toString();
    }

    private String generateTitle() {
        return String.format("# %s (%s)\n\n", getTitle(), mod);
    }

    private String generateDescription() {
        StringBuilder out = new StringBuilder();
        out.append("## ").append(I18n.format("groovyscript.wiki.description")).append("\n\n");
        out.append(getDescription()).append("\n\n");

        if (!description.isFullyDocumented()) {
            out.append(
                    new AdmonitionBuilder()
                            .type(Admonition.Type.WARNING)
                            .note(I18n.format("groovyscript.wiki.not_fully_documented"))
                            .generate());
            out.append("\n\n");
        }

        Admonition[] admonition = description.admonition();
        for (Admonition note : admonition) {
            out.append(
                    new AdmonitionBuilder()
                            .type(note.type())
                            .title(note.title())
                            .hasTitle(note.hasTitle())
                            .format(note.format())
                            .note(Documentation.ensurePeriod(Documentation.translate(note.value())))
                            .generate());
            out.append("\n\n");
        }
        return out.toString();
    }

    private String generateIdentifier() {
        StringBuilder out = new StringBuilder();
        out.append("## ").append(I18n.format("groovyscript.wiki.identifier")).append("\n\n").append(I18n.format("groovyscript.wiki.import_instructions")).append("\n\n");

        List<String> packages = mod.getAliases()
                .stream()
                .flatMap(modID -> registry.getAliases().stream().map(alias -> String.format("mods.%s.%s", modID, alias)))
                .collect(Collectors.toList());

        int target = packages.indexOf(reference);
        packages.set(target, reference + "/*()!*/");

        out.append(
                new CodeBlockBuilder()
                        .line(packages)
                        .annotation(I18n.format("groovyscript.wiki.defaultPackage"))
                        // Highlighting and focusing are based on the line count, and is 1-indexed
                        .highlight(String.valueOf(1 + target))
                        .focus(1 + target)
                        .toString());
        return out.toString();
    }

    private String recipeBuilder() {
        StringBuilder out = new StringBuilder();

        out.append("### ")
                .append(I18n.format("groovyscript.wiki.recipe_builder"))
                .append("\n\n")
                .append(I18n.format("groovyscript.wiki.uses_recipe_builder", getTitle()))
                .append("\n\n")
                .append(I18n.format("groovyscript.wiki.recipe_builder_note", Documentation.DEFAULT_FORMAT.linkToBuilder()))
                .append("\n\n");

        for (int i = 0; i < recipeBuilderMethods.size(); i++) {
            Builder builder = new Builder(recipeBuilderMethods.get(i).getMethod(), recipeBuilderMethods.get(i).getAnnotation(), reference, baseTranslationKey);
            out.append(
                    new AdmonitionBuilder()
                            .type(Admonition.Type.ABSTRACT)
                            .hasTitle(true)
                            .title(methodExample(recipeBuilderMethods.get(i).getMethod()))
                            .note(builder.documentMethods().split("\n"))
                            .note("\n")
                            .note(builder.builderAdmonition().split("\n"))
                            .note("\n")
                            .generate());
            if (i < recipeBuilderMethods.size() - 1) out.append("\n\n");
        }
        return out.toString();
    }

    public String documentationBlock() {
        StringBuilder out = new StringBuilder();

        out.append(generateHeader());
        out.append(generateTitle());
        out.append(generateDescription());
        out.append(generateIdentifier());

        if (!methods.get(MethodDescription.Type.VALUE).isEmpty()) {
            out.append("## ").append(I18n.format("groovyscript.wiki.editing_values")).append("\n\n").append(documentMethods(methods.get(MethodDescription.Type.VALUE))).append("\n");
        }
        if (!methods.get(MethodDescription.Type.ADDITION).isEmpty() || !recipeBuilderMethods.isEmpty()) {
            out.append("## ").append(Documentation.translate(description.category().adding())).append("\n\n");
            if (!methods.get(MethodDescription.Type.ADDITION).isEmpty()) {
                out.append(documentMethods(methods.get(MethodDescription.Type.ADDITION))).append("\n");
            }
            if (!recipeBuilderMethods.isEmpty()) {
                out.append(recipeBuilder()).append("\n\n");
            }
        }
        if (!methods.get(MethodDescription.Type.REMOVAL).isEmpty()) {
            out.append("## ").append(Documentation.translate(description.category().removing())).append("\n\n").append(documentMethods(methods.get(MethodDescription.Type.REMOVAL))).append("\n");
        }
        if (!methods.get(MethodDescription.Type.QUERY).isEmpty()) {
            out.append("## ").append(Documentation.translate(description.category().query())).append("\n\n").append(documentMethods(methods.get(MethodDescription.Type.QUERY), true)).append("\n");
        }
        out.append("\n");

        return out.toString();
    }

    public String documentMethods(List<DescriptorHelper.MethodAnnotation<MethodDescription>> methods) {
        return documentMethods(methods, false);
    }

    public String documentMethods(List<DescriptorHelper.MethodAnnotation<MethodDescription>> methods, boolean preventExamples) {
        StringBuilder out = new StringBuilder();
        List<String> exampleLines = new ArrayList<>();
        List<String> annotations = new ArrayList<>();

        for (DescriptorHelper.MethodAnnotation<MethodDescription> method : methods) {
            out.append(methodDescription(method));
            if (method.getAnnotation().example().length > 0 && Arrays.stream(method.getAnnotation().example()).anyMatch(x -> !x.value().isEmpty())) {
                exampleLines.addAll(Arrays.stream(method.getAnnotation().example()).flatMap(example -> Stream.of(methodExample(method.getMethod(), example.value()))).collect(Collectors.toList()));
            } else if (method.getMethod().getParameterTypes().length == 0) {
                exampleLines.add(methodExample(method.getMethod()));
            }
            Arrays.stream(method.getAnnotation().example()).map(Example::annotations).flatMap(Arrays::stream).forEach(annotations::add);
        }

        if (!exampleLines.isEmpty() && !preventExamples) {
            out.append(
                    new AdmonitionBuilder()
                            .type(Admonition.Type.EXAMPLE)
                            .note(
                                    new CodeBlockBuilder()
                                            .line(exampleLines)
                                            .annotation(annotations)
                                            .generate())
                            .generate());
            out.append("\n");
        }

        return out.toString();
    }

    public String methodDescription(DescriptorHelper.MethodAnnotation<MethodDescription> method) {
        String desc = method.getAnnotation().description();
        String registryDefault = String.format("%s.%s", baseTranslationKey, method.getMethod().getName());
        String globalDefault = String.format("groovyscript.wiki.%s", method.getMethod().getName());
        // If `desc` isn't defined, check the `registryDefault` key. If it exists, use it.
        // Then, check the `globalDefault` key. If it exists use it. Otherwise, we want to still use the `registryDefault` for logging a missing key.
        String lang = desc.isEmpty()
                ? I18n.hasKey(registryDefault) || !I18n.hasKey(globalDefault) ? registryDefault : globalDefault
                : desc;

        return String.format(
                "- %s:\n\n%s",
                PERIOD_END_PATTERN.matcher(Documentation.translate(lang)).replaceAll(""),
                new CodeBlockBuilder()
                        .line(methodExample(method.getMethod(), Exporter.simpleSignature(method.getMethod(), types)))
                        .indentation(1)
                        .toString());
    }

    private String methodExample(Method method, String example) {
        if (method.getParameterTypes().length == 0) return methodExample(method);
        return String.format("%s.%s(%s)", reference, method.getName(), example);
    }

    private String methodExample(Method method) {
        return String.format("%s.%s()", reference, method.getName());
    }

    private String examples(DescriptorHelper.MethodAnnotation<MethodDescription> method) {
        StringBuilder out = new StringBuilder();
        for (Example example : sortExamples(getExamples(method))) {
            if (example.commented()) out.append("// ");
            if (!example.def().isEmpty()) out.append("def ").append(example.def()).append(" = ");
            out.append(reference).append(".").append(method.getMethod().getName());
            if (example.value().isEmpty()) out.append("()");
            else out.append("(").append(example.value()).append(")");
            out.append("\n");
        }
        return out.toString();
    }
}
