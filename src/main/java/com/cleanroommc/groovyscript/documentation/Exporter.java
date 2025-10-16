package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.documentation.IRegistryDocumentation;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.documentation.helper.ComparisonHelper;
import com.cleanroommc.groovyscript.documentation.helper.ContainerHolder;
import com.cleanroommc.groovyscript.documentation.helper.LinkIndex;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Exporter {

    private static final String INDEX_FILE_NAME = "index.md";
    private static final String NAV_FILE_NAME = "!navigation.md";
    private static final String EXAMPLE_HEADER = "\n// Auto generated groovyscript example file\n// MODS_LOADED: %1$s\n%2$s\nlog 'mod \\'%1$s\\' detected, running script'\n\n";

    private static final Map<String, Class<?>> SKIPPED_CLASSES = new Object2ObjectOpenHashMap<>();

    public static void generateWiki(File targetFolder, ContainerHolder container) {
        var linkIndex = new LinkIndex();

        var registries = getRegistries(container, x -> x.skipDefaultWiki(container));

        if (registries.isEmpty()) return;

        Documentation.ensureDirectoryExists(targetFolder);

        for (var registry : registries) {
            GroovyLog.msg("Generating wiki for the container {} and registry '{}'.", container.name(), registry.getName()).debug().post();
            registry.generateWiki(container, targetFolder, linkIndex);
        }

        String indexText = String.format("---\n%s\n---\n\n\n# %s\n\n%s", Documentation.DEFAULT_FORMAT.removeTableOfContentsText(), container.name(), linkIndex.get());
        write(new File(targetFolder, INDEX_FILE_NAME), indexText);

        if (Documentation.DEFAULT_FORMAT.requiresNavFile()) {
            String navigation = String.format("---\nsearch:\n  exclude: true\n---\n\n\n* [%s](./%s)\n%s", container.name(), INDEX_FILE_NAME, linkIndex.getLinks());
            write(new File(targetFolder, NAV_FILE_NAME), navigation);
        }
    }

    public static void writeNormalWikiFile(File targetFolder, LinkIndex linkIndex, String id, String title, String doc) {
        String location = id + ".md";
        linkIndex.add(String.format("* [%s](./%s)", title, location));
        write(new File(targetFolder, location), doc.trim().concat("\n"));
    }

    public static void generateExamples(File targetFile, LoadStage loadStage, ContainerHolder container) {
        StringBuilder body = new StringBuilder();

        List<String> imports = new ArrayList<>();

        var registries = getRegistries(container, x -> x.skipDefaultExamples(container));

        if (registries.isEmpty()) return;

        for (var registry : registries) {
            GroovyLog.msg("Generating examples for the container {} and registry '{}'.", container.name(), registry.getName()).debug().post();
            body.append(registry.generateExamples(container, loadStage, imports));
        }

        if (body.length() == 0) return;

        String header = String.format(EXAMPLE_HEADER, container.id(), getImportBlock(imports));

        write(targetFile, header + body);
    }

    public static void write(File file, String text) {
        try {
            Files.write(file.toPath(), text.getBytes());
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
        }
    }

    public static void exportFile(File file, String resource) throws IOException {
        try (InputStream inputStream = Exporter.class.getClassLoader().getResourceAsStream(resource);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            int i;
            while ((i = inputStream.read()) != -1) {
                outputStream.write(i);
            }
        }
    }

    private static List<IRegistryDocumentation> getRegistries(ContainerHolder container, Predicate<IRegistryDocumentation> check) {
        List<IRegistryDocumentation> registries = new ArrayList<>();
        for (INamed named : container.registries()) {
            if (named instanceof IRegistryDocumentation doc) {
                registries.add(doc);
                if (check.test(doc)) continue;
            }
            if (named.getClass().isAnnotationPresent(RegistryDescription.class)) {
                registries.add(new Registry(container, named));
                continue;
            }
            SKIPPED_CLASSES.put(container.id() + "." + named.getName(), named.getClass());
        }
        registries.sort(ComparisonHelper::iRegistryDocumentation);
        return registries;
    }

    private static String getImportBlock(List<String> imports) {
        if (imports.isEmpty()) return "";
        var list = new ArrayList<>(new ObjectOpenHashSet<>(imports));
        list.sort(ComparisonHelper::splitString);
        var sb = new StringBuilder();
        for (var x : list) {
            sb.append("\nimport ").append(x);
        }
        return sb + "\n";
    }

    public static void logSkippedClasses() {
        if (SKIPPED_CLASSES.isEmpty()) return;
        GroovyLog.Msg log = GroovyLog.msg("Skipped documenting the following potentially valid locations (this may be the correct behavior!)");
        SKIPPED_CLASSES.forEach((key, value) -> log.add(key + ": " + value.getName()));
        log.debug().post();
        SKIPPED_CLASSES.clear();
    }
}
