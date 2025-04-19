package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.documentation.format.IFormat;
import com.cleanroommc.groovyscript.documentation.helper.ComparisonHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.resources.I18n;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Exporter {

    private static final String INDEX_FILE_NAME = "index.md";
    private static final String NAV_FILE_NAME = "!navigation.md";
    private static final String PRINT_MOD_DETECTED = "log.info 'mod \\'%s\\' detected, running script'";

    private static final Map<String, Class<?>> SKIPPED_CLASSES = new Object2ObjectOpenHashMap<>();

    public static void generateWiki(IFormat format, File folder, GroovyContainer<? extends GroovyPropertyContainer> mod) {
        List<String> fileLinks = new ArrayList<>();

        Set<INamed> registries = new HashSet<>();
        for (INamed named : mod.get().getRegistries()) {
            var annotation = named.getClass().getAnnotation(RegistryDescription.class);
            if (annotation == null) {
                SKIPPED_CLASSES.put(mod.getModId() + "." + named.getName(), named.getClass());
            } else {
                registries.add(named);
            }
        }

        if (registries.isEmpty()) return;

        registries.stream()
                .sorted(ComparisonHelper::iNamed)
                .forEach(registry -> {
                    Registry example = new Registry(mod, registry);
                    String location = String.format("%s.md", registry.getName());
                    fileLinks.add(String.format("* [%s](./%s)", example.getTitle(), location));
                    try {
                        File file = new File(folder, location);
                        Files.write(file.toPath(), example.documentationBlock().trim().concat("\n").getBytes());
                    } catch (IOException e) {
                        GroovyScript.LOGGER.throwing(e);
                    }
                });

        // TODO add bracket handlers
        //  maybe also add commands?

        StringBuilder index = new StringBuilder()
                .append("---")
                .append("\n")
                .append(format.removeTableOfContentsText())
                .append("\n") // Removes the table of contents from the sidebar of indexes.
                .append("---")
                .append("\n\n\n")
                .append("# ")
                .append(mod)
                .append("\n\n")
                .append("## ")
                .append(I18n.format("groovyscript.wiki.categories"))
                .append("\n\n")
                .append(I18n.format("groovyscript.wiki.subcategories_count", registries.size()))
                .append("\n\n");

        StringBuilder navigation = new StringBuilder()
                .append("---")
                .append("\n")
                .append("search:")
                .append("\n")
                .append("  exclude: true")
                .append("\n") // Removes navigation files from the search index
                .append("---")
                .append("\n\n\n")
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

        if (format.requiresNavFile()) {
            try {
                File file = new File(folder, NAV_FILE_NAME);
                Files.write(file.toPath(), navigation.toString().getBytes());
            } catch (IOException e) {
                GroovyScript.LOGGER.throwing(e);
            }
        }
    }

    public static void generateExamples(String target, GroovyContainer<? extends GroovyPropertyContainer> mod) {
        StringBuilder header = new StringBuilder();
        StringBuilder body = new StringBuilder();

        List<String> imports = new ArrayList<>();

        // Preprocessor to only run script if the mod is loaded
        header.append("\n")
                .append("// Auto generated groovyscript example file")
                .append("\n")
                .append("// MODS_LOADED: ")
                .append(mod.getModId())
                .append("\n");

        // Iterate through every registry of the mod once, in alphabetical order.
        Set<INamed> registries = new HashSet<>();
        for (INamed named : mod.get().getRegistries()) {
            var annotation = named.getClass().getAnnotation(RegistryDescription.class);
            if (annotation == null) {
                SKIPPED_CLASSES.put(mod.getModId() + "." + named.getName(), named.getClass());
            } else {
                if (annotation.location().equals(target)) {
                    registries.add(named);
                }
            }
        }

        if (registries.isEmpty()) return;

        registries.stream()
                .sorted(ComparisonHelper::iNamed)
                .forEach(registry -> {
                    GroovyLog.msg("Generating examples for the mod {} and registry '{}'.", mod.toString(), registry.getName()).debug().post();
                    Registry example = new Registry(mod, registry);
                    imports.addAll(example.getImports());
                    body.append(example.exampleBlock());
                });

        if (!imports.isEmpty()) header.append("\n");
        imports.stream().distinct().sorted().forEach(i -> header.append("import ").append(i).append("\n"));

        // Print that the script was loaded at the end of the header, after any imports have been added.
        header.append("\n")
                .append(String.format(PRINT_MOD_DETECTED, mod.getModId()))
                .append("\n\n")
                .append(body);

        try {
            File file = new File(new File(Documentation.EXAMPLES, target), mod.getModId() + ".groovy");
            Files.write(file.toPath(), header.toString().getBytes());
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
        }
    }

    public static void logSkippedClasses() {
        if (SKIPPED_CLASSES.isEmpty()) return;
        GroovyLog.Msg log = GroovyLog.msg("Skipped documenting the following potentially valid locations (this may be the correct behavior!)");
        SKIPPED_CLASSES.forEach((key, value) -> log.add(key + ": " + value.getName()));
        log.debug().post();
        SKIPPED_CLASSES.clear();
    }
}
