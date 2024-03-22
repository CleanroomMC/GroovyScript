package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.google.common.collect.ComparisonChain;
import net.minecraft.client.resources.I18n;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Exporter {

    private static final String INDEX_FILE_NAME = "index.md";
    private static final String NAV_FILE_NAME = "!navigation.md";
    private static final String PRINT_MOD_DETECTED = "println 'mod \\'%s\\' detected, running script'";
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("(?>\\b)(?>[a-zA-Z0-9]+\\.)+([a-zA-Z0-9$]+)");

    public static String simpleSignature(Method method) {
        String signature = Arrays.stream(method.getAnnotatedParameterTypes()).map(x -> simpleSignature(x.getType().getTypeName())).collect(Collectors.joining(", "));
        return method.isVarArgs() ? convertVarArgs(signature) : signature;
    }

    public static String simpleSignature(String name) {
        return CLASS_NAME_PATTERN.matcher(name).replaceAll("$1").replaceAll("\\$", ".");
    }

    private static String convertVarArgs(String name) {
        return name.substring(0, name.lastIndexOf("[]")) + "..." + name.substring(name.lastIndexOf("[]") + 2);
    }


    public static void generateWiki(File folder, GroovyContainer<? extends ModPropertyContainer> mod) {
        List<String> fileLinks = new ArrayList<>();

        List<INamed> registries = mod.get().getRegistries().stream()
                .filter(x -> x.getClass().isAnnotationPresent(RegistryDescription.class))
                .distinct()
                .sorted((left, right) -> ComparisonChain.start()
                        .compare(left.getClass().getAnnotation(RegistryDescription.class).priority(), right.getClass().getAnnotation(RegistryDescription.class).priority())
                        .compare(left.getName(), right.getName())
                        .result())
                .collect(Collectors.toList());

        if (registries.isEmpty()) return;

        for (INamed registry : registries) {
            Registry example = new Registry(mod, registry);

            String location = String.format("%s.md", registry.getName());
            fileLinks.add(String.format("* [%s](./%s)", example.getTitle(), location));
            try {
                File file = new File(folder, location);
                Files.write(file.toPath(), example.documentationBlock().trim().concat("\n").getBytes());
            } catch (IOException e) {
                GroovyScript.LOGGER.throwing(e);
            }
        }

        // TODO add bracket handlers
        //  maybe also add commands?

        StringBuilder index = new StringBuilder()
                .append("---").append("\n")
                .append(Documentation.DEFAULT_FORMAT.removeTableOfContentsText()).append("\n") // Removes the table of contents from the sidebar of indexes.
                .append("---").append("\n\n\n")
                .append("# ").append(mod).append("\n\n")
                .append("## ").append(I18n.format("groovyscript.wiki.categories")).append("\n\n")
                .append(I18n.format("groovyscript.wiki.subcategories_count", registries.size())).append("\n\n");

        StringBuilder navigation = new StringBuilder()
                .append("---").append("\n")
                .append("search:").append("\n")
                .append("  exclude: true").append("\n") // Removes navigation files from the search index
                .append("---").append("\n\n\n")
                .append(String.format("* [%s](./%s)\n", mod, INDEX_FILE_NAME));

        fileLinks.forEach(line -> {
            index.append(line).append("\n\n");
            navigation.append(line).append("\n");
        });

        try {
            File file = new File(folder, INDEX_FILE_NAME);
            Files.write(file.toPath(), index.toString().getBytes());
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
        }

        if (Documentation.DEFAULT_FORMAT.requiresNavFile()) {
            try {
                File file = new File(folder, NAV_FILE_NAME);
                Files.write(file.toPath(), navigation.toString().getBytes());
            } catch (IOException e) {
                GroovyScript.LOGGER.throwing(e);
            }
        }
    }

    public static void generateExamples(String target, GroovyContainer<? extends ModPropertyContainer> mod) {
        StringBuilder header = new StringBuilder();
        StringBuilder body = new StringBuilder();

        List<String> imports = new ArrayList<>();

        // Preprocessor to only run script if the mod is loaded
        header.append("\n").append("// Auto generated groovyscript example file").append("\n")
                .append("// MODS_LOADED: ").append(mod.getModId()).append("\n");

        // Iterate through every registry of the mod once, in alphabetical order.
        List<INamed> registries = mod.get().getRegistries().stream()
                .distinct()
                .filter(x -> x.getClass().isAnnotationPresent(RegistryDescription.class))
                .filter(x -> x.getClass().getAnnotation(RegistryDescription.class).location().equals(target))
                .sorted((left, right) -> ComparisonChain.start()
                        .compare(left.getClass().getAnnotation(RegistryDescription.class).priority(), right.getClass().getAnnotation(RegistryDescription.class).priority())
                        .compare(left.getName(), right.getName())
                        .result())
                .collect(Collectors.toList());

        if (registries.isEmpty()) return;

        for (INamed registry : registries) {
            GroovyLog.msg("Generating examples for the mod {} and registry '{}'.", mod.toString(), registry.getName()).debug().post();
            Registry example = new Registry(mod, registry);
            imports.addAll(example.getImports());
            body.append(example.exampleBlock());
        }

        if (!imports.isEmpty()) header.append("\n");
        imports.stream().distinct().sorted().forEach(i -> header.append("import ").append(i).append("\n"));

        // Print that the script was loaded at the end of the header, after any imports have been added.
        header.append("\n").append(String.format(PRINT_MOD_DETECTED, mod.getModId())).append("\n\n")
                .append(body);

        try {
            File file = new File(new File(Documentation.EXAMPLES, target), mod.getModId() + ".groovy");
            Files.write(file.toPath(), header.toString().getBytes());
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
        }
    }
}
