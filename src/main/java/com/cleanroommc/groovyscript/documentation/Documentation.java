package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.documentation.format.IFormat;
import com.cleanroommc.groovyscript.documentation.format.OutputFormat;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.regex.Pattern;

public class Documentation {

    // Matches /*()*/ or /*()!*/ and captures if theres an !
    public static final Pattern ANNOTATION_COMMENT_LOCATION = Pattern.compile("/\\*\\(\\)(!?)\\*/");
    public static final int MAX_LINE_LENGTH = 120;
    public static final boolean USE_DEFAULT_BRANCH = FMLLaunchHandler.isDeobfuscatedEnvironment();

    public static final File EXAMPLES = new File(GroovyScript.getScriptPath());
    public static final File WIKI = new File(new File(GroovyScript.getScriptFile().getParentFile(), "build"), "wiki");

    public static final IFormat DEFAULT_FORMAT = OutputFormat.VITEPRESS;

    private static final boolean GENERATE_EXAMPLES = false;
    private static final boolean GENERATE_WIKI = false;
    // Kills the game as soon as the wiki or examples are generated.
    private static final boolean TEST_AND_CRASH = false;

    private static final boolean LOG_MISSING_KEYS = true;

    private static final Set<String> missingLangKeys = new ObjectLinkedOpenHashSet<>();

    public static void generate() {
        if (GENERATE_EXAMPLES) generateExamples();
        if (GENERATE_WIKI) generateWiki();
        if (TEST_AND_CRASH) FMLCommonHandler.instance().exitJava(0, false);
    }

    public static void generateExamples() {
        try {
            Files.createDirectories(EXAMPLES.toPath());
            for (LoadStage stage : LoadStage.getLoadStages()) {
                File target = new File(EXAMPLES, stage.getName());
                Files.createDirectories(target.toPath());

                for (GroovyContainer<? extends ModPropertyContainer> mod : ModSupport.getAllContainers()) {
                    if (!mod.isLoaded()) continue;
                    Exporter.generateExamples(stage.getName(), mod);
                }
            }
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
        }
        logAnyMissingKeys();
    }

    public static void generateWiki() {
        try {
            Files.createDirectories(WIKI.toPath());
            for (GroovyContainer<? extends ModPropertyContainer> mod : ModSupport.getAllContainers()) {
                if (!mod.isLoaded()) continue;
                File target = new File(WIKI, mod.getModId());
                if (target.exists() || Files.createDirectories(target.toPath()) != null) {
                    Exporter.generateWiki(target, mod);
                } else {
                    GroovyLog.get().error("Error creating file at {} to generate wiki files in", target);
                }
            }
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
        }
        logAnyMissingKeys();
    }

    public static void logAnyMissingKeys() {
        if (!missingLangKeys.isEmpty()) {
            GroovyLog.Msg log = GroovyLog.msg("Missing the following localization keys:");
            missingLangKeys.stream().sorted().forEach(log::add);
            log.debug().post();
            missingLangKeys.clear();
        }
    }

    public static String translate(String translateKey, Object... parameters) {
        if (LOG_MISSING_KEYS && !I18n.hasKey(translateKey)) {
            missingLangKeys.add(translateKey);
        }
        return I18n.format(translateKey, parameters);
    }

}

