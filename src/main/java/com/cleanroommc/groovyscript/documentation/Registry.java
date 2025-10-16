package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.documentation.IRegistryDocumentation;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.documentation.helper.*;
import com.cleanroommc.groovyscript.documentation.helper.descriptor.DescriptorHelper;
import com.cleanroommc.groovyscript.documentation.helper.descriptor.MethodAnnotation;
import com.cleanroommc.groovyscript.documentation.linkgenerator.LinkGeneratorHooks;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Registry implements IRegistryDocumentation {

    public static final String BASE_LANG_LOCATION = "groovyscript.wiki";
    private static final Pattern PERIOD_END_PATTERN = Pattern.compile("\\.$");

    private final ContainerHolder container;
    private final INamed registry;
    private final RegistryDescription description;
    private final Map<String, String> types;
    private final List<Builder> recipeBuilders;
    private final EnumMap<MethodDescription.Type, List<MethodAnnotation<MethodDescription>>> methods;
    private final List<String> imports = new ArrayList<>();
    private final List<String> aliases;

    public Registry(ContainerHolder container, INamed registry) {
        this.container = container;
        this.registry = registry;
        this.description = registry.getClass().getAnnotation(RegistryDescription.class);
        this.types = generateTypes(registry.getClass());
        var methodSignatures = generateOfClass(registry.getClass());

        List<MethodAnnotation<RecipeBuilderDescription>> recipeBuilderMethods = new ArrayList<>();
        this.methods = new EnumMap<>(MethodDescription.Type.class);
        for (MethodDescription.Type value : MethodDescription.Type.values()) this.methods.put(value, new ArrayList<>());

        for (var entry : methodSignatures.getMethods(RecipeBuilderDescription.class)) {
            recipeBuilderMethods.add(entry);
            addImports(entry.annotation().example());
        }
        for (var entry : methodSignatures.getMethods(MethodDescription.class)) {
            methods.get(entry.annotation().type()).add(entry);
            addImports(entry.annotation().example());
        }
        this.recipeBuilders = recipeBuilderMethods
                .stream()
                .sorted(ComparisonHelper::recipeBuilder)
                .map(x -> new Builder(x.method(), x.annotation(), getReference(), getBaseLangKey()))
                .collect(Collectors.toList());
        methods.values().forEach(value -> value.sort(ComparisonHelper::method));
        aliases = generateAliases(container, registry);
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
                methodSignatures.addAnnotation(annotation, annotation.method());
            }
            for (var annotation : override.recipeBuilder()) {
                methodSignatures.addAnnotation(annotation, annotation.method());
            }
        }
        return methodSignatures;
    }

    private static List<String> generateAliases(ContainerHolder container, INamed registry) {
        List<String> list = new ArrayList<>();
        for (String modID : container.aliases()) {
            if (modID.isEmpty()) {
                list.addAll(registry.getAliases());
                continue;
            }
            for (String alias : registry.getAliases()) {
                String format = String.format("%s.%s", modID, alias);
                list.add(format);
            }
        }
        for (var x : GroovyScript.getSandbox().getBindings().entrySet()) {
            if (x.getValue() == registry) {
                list.add(x.getKey());
            }
        }
        list.sort(ComparisonHelper::packages);
        return list;
    }

    @Override
    public String getName() {
        return registry.getName();
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public @NotNull String generateExamples(ContainerHolder container, LoadStage loadStage, List<String> imports) {
        if (description.location() == loadStage) {
            imports.addAll(getImports());
            return exampleBlock();
        }
        return "";
    }

    @Override
    public void generateWiki(ContainerHolder container, File suggestedFolder, LinkIndex linkIndex) {
        Exporter.writeNormalWikiFile(suggestedFolder, linkIndex, getName(), getTitle(), documentationBlock());
    }

    @Override
    public int priority() {
        return description.priority();
    }

    private String getBaseLangKey() {
        return BASE_LANG_LOCATION + "." + container.id() + "." + registry.getName();
    }

    private String getReference() {
        return GroovyScript.getSandbox().getBindings().get(registry.getName()) == registry ? registry.getName() : container.access() + "." + registry.getName();
    }

    private void addImports(Example... examples) {
        for (var example : examples) Collections.addAll(imports, example.imports());
    }

    public List<String> getImports() {
        return this.imports;
    }

    public String getFileSourceCodeLink() {
        return LinkGeneratorHooks.convert(description.linkGenerator(), registry.getClass());
    }

    public String getTitle() {
        return LangHelper.translate(description.title().isEmpty() ? String.format("%s.title", getBaseLangKey()) : description.title());
    }

    public String getDescription() {
        return LangHelper.ensurePeriod(
                LangHelper.translate(description.description().isEmpty() ? String.format("%s.description", getBaseLangKey()) : description.description())
                        .replace("\"", "\\\""));
    }

    public String exampleBlock() {
        StringBuilder out = new StringBuilder();
        out.append("// ").append(getTitle()).append(":").append("\n");
        out.append("// ").append(WordUtils.wrap(getDescription(), Documentation.MAX_LINE_LENGTH, "\n// ", false)).append("\n\n");
        out.append(documentMethodDescriptionType(MethodDescription.Type.VALUE));
        out.append(documentMethodDescriptionType(MethodDescription.Type.REMOVAL));
        if (!recipeBuilders.isEmpty()) {
            for (var builder : recipeBuilders) out.append(builder.builderExampleFile()).append("\n");
            out.append("\n");
        }
        out.append(documentMethodDescriptionType(MethodDescription.Type.ADDITION));
        out.append(documentMethodDescriptionType(MethodDescription.Type.QUERY));
        return out.toString();
    }

    private String documentMethodDescriptionType(MethodDescription.Type type) {
        StringBuilder out = new StringBuilder();
        for (var entry : methods.get(type)) {
            out.append(examples(entry));
        }
        if (out.toString().isEmpty()) return "";
        return out + "\n";
    }

    private String generateHeader() {
        StringBuilder out = new StringBuilder();
        out.append("---\n").append("title: \"").append(getTitle()).append("\"\n");
        if (Documentation.DEFAULT_FORMAT.hasTitleTemplate()) out.append("titleTemplate: \"").append(container.name()).append(" | CleanroomMC").append("\"\n");
        out.append("description: \"").append(getDescription()).append("\"\n");
        String link = getFileSourceCodeLink();
        if (!link.isEmpty()) out.append("source_code_link: \"").append(link).append("\"\n");
        out.append("---\n\n");
        return out.toString();
    }

    private String generateTitle() {
        return String.format("# %s (%s)\n\n", getTitle(), container.name());
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
                            .note(LangHelper.ensurePeriod(LangHelper.translate(note.value())))
                            .generate());
            out.append("\n\n");
        }
        return out.toString();
    }

    private String generateIdentifier() {
        StringBuilder out = new StringBuilder();
        out.append("## ").append(I18n.format("groovyscript.wiki.identifier")).append("\n\n").append(I18n.format("groovyscript.wiki.import_instructions")).append("\n\n");

        List<String> packages = getAliases();

        int target = packages.indexOf(getReference());
        if (target == -1) {
            GroovyLog.get().warn("Couldn't find identifier %s in packagess %s", getReference(), String.join(", ", packages));
        } else {
            packages.set(target, getReference() + "/*()!*/");
            out.append(new CodeBlockBuilder()
                               .line(packages)
                               .annotation(I18n.format("groovyscript.wiki.defaultPackage"))
                               // Highlighting and focusing are based on the line count, and is 1-indexed
                               .highlight(String.valueOf(1 + target))
                               .focus(1 + target));
        }
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

        int size = recipeBuilders.size();
        for (int i = 0; i < size; i++) {
            out.append(recipeBuilders.get(i).generateAdmonition());
            if (i < size - 1) out.append("\n\n");
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
        if (!methods.get(MethodDescription.Type.ADDITION).isEmpty() || !recipeBuilders.isEmpty()) {
            out.append("## ").append(LangHelper.translate(description.category().adding())).append("\n\n");
            if (!methods.get(MethodDescription.Type.ADDITION).isEmpty()) {
                out.append(documentMethods(methods.get(MethodDescription.Type.ADDITION))).append("\n");
            }
            if (!recipeBuilders.isEmpty()) {
                out.append(recipeBuilder()).append("\n\n");
            }
        }
        if (!methods.get(MethodDescription.Type.REMOVAL).isEmpty()) {
            out.append("## ").append(LangHelper.translate(description.category().removing())).append("\n\n").append(documentMethods(methods.get(MethodDescription.Type.REMOVAL))).append("\n");
        }
        if (!methods.get(MethodDescription.Type.QUERY).isEmpty()) {
            out.append("## ").append(LangHelper.translate(description.category().query())).append("\n\n").append(documentMethods(methods.get(MethodDescription.Type.QUERY), true)).append("\n");
        }
        out.append("\n");

        return out.toString();
    }

    public String documentMethods(List<MethodAnnotation<MethodDescription>> methods) {
        return documentMethods(methods, false);
    }

    public String documentMethods(List<MethodAnnotation<MethodDescription>> methods, boolean preventExamples) {
        StringBuilder out = new StringBuilder();
        List<String> exampleLines = new ArrayList<>();
        List<String> annotations = new ArrayList<>();
        Set<Method> describedMethods = new ObjectOpenHashSet<>();

        for (MethodAnnotation<MethodDescription> method : methods) {
            // only add the method description if it is the first for the targeted method
            if (describedMethods.add(method.method())) {
                out.append(methodDescription(method));
            }
            if (method.annotation().example().length > 0 && Arrays.stream(method.annotation().example()).anyMatch(x -> !x.value().isEmpty())) {
                exampleLines.addAll(Arrays.stream(method.annotation().example()).flatMap(example -> Stream.of(methodExample(method.method(), example.value()))).collect(Collectors.toList()));
            } else if (method.method().getParameterTypes().length == 0) {
                exampleLines.add(methodExample(method.method()));
            }
            Arrays.stream(method.annotation().example()).map(Example::annotations).flatMap(Arrays::stream).forEach(annotations::add);
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

    public String methodDescription(MethodAnnotation<MethodDescription> method) {
        String lang = method.annotation().description();
        String registryDefault = String.format("%s.%s", getBaseLangKey(), method.method().getName());
        String globalDefault = String.format("%s.%s", Registry.BASE_LANG_LOCATION, method.method().getName());
        if (lang.isEmpty()) {
            // If the `globalDefault` is not defined, we always want to use `registryDefault` for logging the missing key.
            if (I18n.hasKey(registryDefault) || !I18n.hasKey(globalDefault)) lang = registryDefault;
            else lang = globalDefault;
        }

        return String.format(
                "- %s:\n\n%s",
                PERIOD_END_PATTERN.matcher(LangHelper.translate(lang)).replaceAll(""),
                new CodeBlockBuilder()
                        .line(methodExample(method.method(), DescriptorHelper.simpleParameters(method.method(), types)))
                        .indentation(1)
                        .toString());
    }

    private String methodExample(Method method, String example) {
        if (method.getParameterTypes().length == 0) return methodExample(method);
        return String.format("%s.%s(%s)", getReference(), method.getName(), example);
    }

    private String methodExample(Method method) {
        return String.format("%s.%s()", getReference(), method.getName());
    }

    private String examples(MethodAnnotation<MethodDescription> method) {
        StringBuilder out = new StringBuilder();
        Arrays.stream(method.annotation().example())
                .sorted(ComparisonHelper::example)
                .forEach(example -> {
                    if (example.commented()) out.append("// ");
                    if (!example.def().isEmpty()) out.append("def ").append(example.def()).append(" = ");
                    out.append(getReference()).append(".").append(method.method().getName());
                    if (example.value().isEmpty()) out.append("()");
                    else out.append("(").append(example.value()).append(")");
                    out.append("\n");
                });
        return out.toString();
    }
}
