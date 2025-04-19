package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.documentation.format.IFormat;
import com.cleanroommc.groovyscript.documentation.format.OutputFormat;
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

    public static final File EXAMPLES = new File(GroovyScript.getScriptPath());
    public static final File WIKI = new File(new File(GroovyScript.getScriptFile().getParentFile(), "build"), "wiki");

    public static final IFormat DEFAULT_FORMAT = OutputFormat.VITEPRESS;

    public static void generate() {
        if (GenerationFlags.GENERATE_EXAMPLES) generateExamples();
        if (GenerationFlags.GENERATE_WIKI) generateWiki();
        if (GenerationFlags.GENERATE_AND_CRASH) FMLCommonHandler.instance().exitJava(0, false);
    }

    public static void generateExamples() {
        try {
            Files.createDirectories(EXAMPLES.toPath());
            for (LoadStage stage : LoadStage.getLoadStages()) {
                File target = new File(EXAMPLES, stage.getName());
                Files.createDirectories(target.toPath());

                for (GroovyContainer<? extends GroovyPropertyContainer> mod : ModSupport.getAllContainers()) {
                    if (!mod.isLoaded()) continue;
                    Exporter.generateExamples(stage.getName(), mod);
                }
            }
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
        }
        logMissing();
    }

    public static void generateWiki() {
        try {
            Files.createDirectories(WIKI.toPath());
            for (GroovyContainer<? extends GroovyPropertyContainer> mod : ModSupport.getAllContainers()) {
                if (!mod.isLoaded()) continue;
                File target = new File(WIKI, mod.getModId());
                if (target.exists() || Files.createDirectories(target.toPath()) != null) {
                    Exporter.generateWiki(DEFAULT_FORMAT, target, mod);
                } else {
                    GroovyLog.get().error("Error creating file at {} to generate wiki files in", target);
                }
            }
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
        }
        logMissing();
    }

    private static void logMissing() {
        LangHelper.logAnyMissingKeys();
        Exporter.logSkippedClasses();
    }
}
