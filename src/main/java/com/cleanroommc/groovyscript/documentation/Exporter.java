package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.ComparisonChain;
import net.minecraft.client.resources.I18n;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Exporter {

    private static final String INDEX_FILE_NAME = "index.md";
    private static final String NAV_FILE_NAME = "!navigation.md";
    private static final String PRINT_MOD_DETECTED = "println 'mod \\'%s\\' detected, running script'";


    public static String simpleName(Method method) {
        RecipeBuilderMethodDescription recipeBuilderMethodDescription = method.getAnnotation(RecipeBuilderMethodDescription.class);
        if (recipeBuilderMethodDescription != null && !recipeBuilderMethodDescription.signature().isEmpty()) return recipeBuilderMethodDescription.signature();
        MethodDescription methodDescription = method.getAnnotation(MethodDescription.class);
        if (methodDescription != null && !methodDescription.signature().isEmpty()) return methodDescription.signature();
        return simpleName(method.getParameterTypes());
    }

    public static String simpleName(Class<?>... classes) {
        return Arrays.stream(classes).map(Exporter::simpleName).collect(Collectors.joining(", "));
    }

    public static String simpleName(Class<?> clazz) {
        return simpleName(clazz.getCanonicalName());
    }

    public static String simpleName(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static void generateWiki(File folder, ModSupport.Container<? extends ModPropertyContainer> mod) {
        List<String> fileLinks = new ArrayList<>();

        List<VirtualizedRegistry<?>> registries = mod.get().getRegistries().stream()
                .filter(x -> x.getClass().isAnnotationPresent(RegistryDescription.class))
                .distinct()
                .collect(Collectors.toList());

        if (registries.isEmpty()) return;

        for (VirtualizedRegistry<?> registry : registries) {
            Registry example = new Registry(mod, registry);

            String location = String.format("%s.md", registry.getName());
            fileLinks.add(String.format("* [%s](./%s)", example.getTitle(), location));
            try {
                File file = new File(folder, location);
                Files.write(file.toPath(), example.documentationBlock().trim().concat("\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // TODO add bracket handlers
        //  maybe also add commands?

        StringBuilder index = new StringBuilder();
        StringBuilder navigation = new StringBuilder().append(String.format("* [%s](./%s)\n", mod, INDEX_FILE_NAME));

        index.append("---").append("\n")
                .append("hide: toc").append("\n") // Removes the table of contents from the sidebar of indexes.
                .append("---").append("\n\n\n")
                .append("# ").append(mod).append("\n\n")
                .append("## ").append(I18n.format("groovyscript.wiki.categories")).append("\n\n")
                .append(I18n.format("groovyscript.wiki.subcategories_count", registries.size())).append("\n\n");

        fileLinks.stream().sorted().forEach(line -> {
            index.append(line).append("\n\n");
            navigation.append(line).append("\n");
        });

        try {
            File file = new File(folder, INDEX_FILE_NAME);
            Files.write(file.toPath(), index.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File file = new File(folder, NAV_FILE_NAME);
            Files.write(file.toPath(), navigation.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateExamples(String target, ModSupport.Container<? extends ModPropertyContainer> mod) {
        StringBuilder header = new StringBuilder();
        StringBuilder body = new StringBuilder();

        List<String> imports = new ArrayList<>();

        // Preprocessor to only run script if the mod is loaded
        header.append("\n").append("// Auto generated groovyscript example file").append("\n")
                .append("// MODS_LOADED: ").append(mod.getId()).append("\n");

        // Iterate through every registry of the mod once, in alphabetical order.
        List<VirtualizedRegistry<?>> registries = mod.get().getRegistries().stream()
                .distinct()
                .filter(x -> x.getClass().isAnnotationPresent(RegistryDescription.class))
                .filter(x -> x.getClass().getAnnotation(RegistryDescription.class).location().equals(target))
                .sorted((left, right) -> ComparisonChain.start()
                        .compare(left.getClass().getAnnotation(RegistryDescription.class).priority(), right.getClass().getAnnotation(RegistryDescription.class).priority())
                        .compare(left.getName(), right.getName())
                        .result())
                .collect(Collectors.toList());

        if (registries.isEmpty()) return;

        for (VirtualizedRegistry<?> registry : registries) {
            GroovyLog.msg("Generating examples for the mod {} and registry '{}'.", mod.toString(), registry.getName()).debug().post();
            Registry example = new Registry(mod, registry);
            imports.addAll(example.getImports());
            body.append(example.exampleBlock());
        }

        if (imports.size() >= 1) header.append("\n");
        imports.stream().distinct().sorted().forEach(i -> header.append("import ").append(i).append("\n"));

        // Print that the script was loaded at the end of the header, after any imports have been added.
        header.append("\n").append(String.format(PRINT_MOD_DETECTED, mod.getId())).append("\n\n")
                .append(body);

        try {
            File file = new File(new File(Documentation.EXAMPLES, target), mod.getId() + ".groovy");
            Files.write(file.toPath(), header.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
