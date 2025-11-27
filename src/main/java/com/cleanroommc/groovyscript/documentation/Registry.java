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
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Registry implements IRegistryDocumentation {

    public static final String BASE_LANG_LOCATION = "groovyscript.wiki";
    private static final Pattern PERIOD_END_PATTERN = Pattern.compile("\\.$");
    private static final int TOO_MANY_PACKAGES = 12;

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
        Consumer<MethodOverride> consumer = override -> {
            for (var entry : override.method()) {
                methodSignatures.addAnnotation(entry, entry.method());
            }
            for (var annotation : override.recipeBuilder()) {
                methodSignatures.addAnnotation(annotation, annotation.method());
            }
        };
        var description = clazz.getAnnotation(RegistryDescription.class);
        if (description != null) consumer.accept(description.override());
        var mo = clazz.getAnnotation(MethodOverride.class);
        if (mo != null) consumer.accept(mo);
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
        var location = Exporter.writeNormalWikiFile(suggestedFolder, getName(), getTitle(), documentationBlock());
        linkIndex.add(location);
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
        if (priority() != 1000) out.append("order: ").append(priority()).append("\n");
        out.append("---\n\n");
        return out.toString();
    }

    private String generateTitle() {
        return new Heading(getTitle() + " (" + container.name() + ")", 1).get();
    }

    private String generateDescription() {
        var md = new Heading(I18n.format("groovyscript.wiki.description"), getDescription());

        if (!description.isFullyDocumented()) {
            md.addEntry(
                    new AdmonitionBuilder()
                            .type(Admonition.Type.WARNING)
                            .note(I18n.format("groovyscript.wiki.not_fully_documented"))
                            .generate());
        }

        Admonition[] admonition = description.admonition();
        for (Admonition note : admonition) {
            md.addEntry(
                    new AdmonitionBuilder()
                            .type(note.type())
                            .title(note.title())
                            .hasTitle(note.hasTitle())
                            .format(note.format())
                            .note(LangHelper.ensurePeriod(LangHelper.translate(note.value())))
                            .generate());
        }
        return md.get();
    }

    private String generateIdentifier() {
        var md = new Heading(I18n.format("groovyscript.wiki.identifier"), I18n.format("groovyscript.wiki.import_default", getReference()));

        List<String> packages = getAliases();

        int target = packages.indexOf(getReference());
        if (target == -1) {
            GroovyLog.get().warn("Couldn't find identifier %s in packages %s", getReference(), String.join(", ", packages));
            return "";
        }
        packages.set(target, getReference() + "/*()!*/");
        var codeBlock = new CodeBlockBuilder()
                .line(packages)
                .annotation(I18n.format("groovyscript.wiki.defaultPackage"))
                // Highlighting and focusing are based on the line count, and is 1-indexed
                .highlight(String.valueOf(1 + target))
                .focus(1 + target)
                .toString();
        var admonition = new AdmonitionBuilder()
                .hasTitle(true)
                .title(I18n.format("groovyscript.wiki.all_packages_title"))
                .note("\n")
                .note(I18n.format("groovyscript.wiki.import_instructions"))
                .note("\n")
                .note(codeBlock.trim())
                .note("\n")
                .type(Admonition.Type.QUOTE);
        if (packages.size() >= TOO_MANY_PACKAGES) admonition.format(Admonition.Format.COLLAPSED);
        md.addEntry(admonition.generate());
        return md.get();
    }

    private String recipeBuilder() {
        var md = new Heading(I18n.format("groovyscript.wiki.recipe_builder"), 3);

        md.addEntry(I18n.format("groovyscript.wiki.uses_recipe_builder", getTitle()));
        md.addEntry(I18n.format("groovyscript.wiki.recipe_builder_note", Documentation.DEFAULT_FORMAT.linkToBuilder()));

        for (Builder recipeBuilder : recipeBuilders) {
            md.addEntry(recipeBuilder.generateAdmonition());
        }
        return md.get();
    }

    public String documentationBlock() {
        StringBuilder out = new StringBuilder();

        out.append(generateHeader());
        out.append(generateTitle());
        out.append(generateDescription());
        out.append(generateIdentifier());

        if (!methods.get(MethodDescription.Type.VALUE).isEmpty()) {
            out.append(documentMethods(LangHelper.translate(LangHelper.fallback(description.category().values, getBaseLangKey() + ".operation.values")), MethodDescription.Type.VALUE));
            out.append(methodExamples(MethodDescription.Type.VALUE));
        }
        if (!methods.get(MethodDescription.Type.ADDITION).isEmpty() || !recipeBuilders.isEmpty()) {
            out.append(documentMethods(LangHelper.translate(LangHelper.fallback(description.category().adding, getBaseLangKey() + ".operation.adding")), MethodDescription.Type.ADDITION));
            if (!methods.get(MethodDescription.Type.ADDITION).isEmpty()) {
                out.append(methodExamples(MethodDescription.Type.ADDITION));
            }
            if (!recipeBuilders.isEmpty()) {
                out.append(recipeBuilder());
            }
        }
        if (!methods.get(MethodDescription.Type.REMOVAL).isEmpty()) {
            out.append(documentMethods(LangHelper.translate(LangHelper.fallback(description.category().removing, getBaseLangKey() + ".operation.removing")), MethodDescription.Type.REMOVAL));
            out.append(methodExamples(MethodDescription.Type.REMOVAL));
        }
        if (!methods.get(MethodDescription.Type.QUERY).isEmpty()) {
            out.append(documentMethods(LangHelper.translate(LangHelper.fallback(description.category().query, getBaseLangKey() + ".operation.query")), MethodDescription.Type.QUERY));
//            out.append(methodExamples(MethodDescription.Type.QUERY));
        }
        out.append("\n");

        return out.toString();
    }

    public String documentMethods(String header, MethodDescription.Type type) {
        Set<Method> describedMethods = new ObjectOpenHashSet<>();
        var md = new Heading(header);
        for (var method : methods.get(type)) {
            // only add the method description if it is the first for the targeted method
            if (describedMethods.add(method.method())) {
                md.addEntry(methodDescription(method).trim());
            }
        }
        return md.get();
    }

    public String methodExamples(MethodDescription.Type type) {
        List<String> exampleLines = new ArrayList<>();
        List<String> annotations = new ArrayList<>();

        for (var method : methods.get(type)) {
            var example = method.annotation().example();
            if (example.length > 0 && Arrays.stream(example).anyMatch(x -> !x.value().isEmpty())) {
                for (var x : example) {
                    exampleLines.add(methodExample(method.method(), x.value()));
                }
            } else if (method.method().getParameterTypes().length == 0) {
                exampleLines.add(methodExample(method.method()));
            }
            for (var x : example) {
                Collections.addAll(annotations, x.annotations());
            }
        }

        if (exampleLines.isEmpty()) return "";
        return new AdmonitionBuilder()
                .type(Admonition.Type.EXAMPLE)
                .note(new CodeBlockBuilder().line(exampleLines).annotation(annotations).generate())
                .generate() + "\n\n";
    }

    public String methodDescription(MethodAnnotation<MethodDescription> method) {
        String lang = method.annotation().description();
        if (lang.isEmpty()) {
            lang = LangHelper.fallback(
                    0,
                    String.format("%s.%s", getBaseLangKey(), method.method().getName()),
                    String.format("%s.%s", Registry.BASE_LANG_LOCATION, method.method().getName()));
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
