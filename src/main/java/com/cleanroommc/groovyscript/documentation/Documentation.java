package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.documentation.IContainerDocumentation;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.documentation.format.IFormat;
import com.cleanroommc.groovyscript.documentation.format.OutputFormat;
import com.cleanroommc.groovyscript.documentation.helper.ContainerHolder;
import com.cleanroommc.groovyscript.documentation.helper.GenerationFlags;
import com.cleanroommc.groovyscript.documentation.helper.LangHelper;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class Documentation {

    // Matches /*()*/ or /*()!*/ and captures if there's an !
    public static final Pattern ANNOTATION_COMMENT_LOCATION = Pattern.compile("/\\*\\(\\)(!?)\\*/");
    public static final int MAX_LINE_LENGTH = 120;
    public static final boolean USE_DEFAULT_BRANCH = FMLLaunchHandler.isDeobfuscatedEnvironment();

    public static final String GROOVY_FILE_EXTENSION = ".groovy";

    public static final File EXAMPLES = new File(GroovyScript.getScriptPath());
    public static final File WIKI = new File(new File(GroovyScript.getScriptFile().getParentFile(), "build"), "wiki");
    public static final File WIKI_MODS = new File(WIKI, "mods");
    public static final File WIKI_MINECRAFT = new File(WIKI, "minecraft");

    public static final IFormat DEFAULT_FORMAT = OutputFormat.VITEPRESS;

    public static void generate() {
        if (GenerationFlags.GENERATE_EXAMPLES) generateExamples(false);
        if (GenerationFlags.GENERATE_WIKI) generateWiki(false);
        logMissing();
        if (GenerationFlags.GENERATE_AND_CRASH) FMLCommonHandler.instance().exitJava(0, false);
    }

    public static void ensureDirectoryExists(File folder) {
        try {
            Files.createDirectories(folder.toPath());
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
        }
    }

    public static File generatedExampleFolder(LoadStage stage) {
        return new File(new File(EXAMPLES, stage.getName()), "generated");
    }

    public static File generatedExampleFile(File parent, String location) {
        return new File(parent, location + "_generated" + GROOVY_FILE_EXTENSION);
    }

    public static File generatedExampleFile(LoadStage stage, String location) {
        return generatedExampleFile(generatedExampleFolder(stage), location);
    }

    public static void generateExamples() {
        generateExamples(true);
    }

    private static void generateExamples(boolean log) {
        for (LoadStage stage : LoadStage.getLoadStages()) {
            File generatedFolder = generatedExampleFolder(stage);
            ensureDirectoryExists(generatedFolder);
            for (var container : ModSupport.getAllContainers()) {
                if (!container.isLoaded()) continue;
                File file = generatedExampleFile(generatedFolder, container.getModId());
                if (!(container instanceof IContainerDocumentation doc) || doc.generateExamples(file, stage)) {
                    Exporter.generateExamples(file, stage, ContainerHolder.of(container));
                }
            }
        }
        if (log) logMissing();
    }

    public static void generateWiki() {
        generateWiki(true);
    }

    private static void generateWiki(boolean log) {
        for (var container : ModSupport.getAllContainers()) {
            if (!container.isLoaded()) continue;
            File target = new File(WIKI_MODS, container.getModId());
            if (!(container instanceof IContainerDocumentation doc) || doc.generateWiki(target)) {
                Exporter.generateWiki(target, ContainerHolder.of(container));
            }
        }
        if (log) logMissing();
    }

    private static void logMissing() {
        LangHelper.logAnyMissingKeys();
        Exporter.logSkippedClasses();
    }
}
