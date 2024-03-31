package com.cleanroommc.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.command.CustomClickAction;
import com.cleanroommc.groovyscript.command.GSCommand;
import com.cleanroommc.groovyscript.compat.content.GroovyResourcePack;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.TinkersConstruct;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.core.mixin.DefaultResourcePackAccessor;
import com.cleanroommc.groovyscript.documentation.Documentation;
import com.cleanroommc.groovyscript.documentation.linkgenerator.LinkGeneratorHooks;
import com.cleanroommc.groovyscript.event.EventHandler;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlerManager;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.cleanroommc.groovyscript.network.CReload;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.network.NetworkUtils;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.*;
import com.cleanroommc.groovyscript.sandbox.mapper.GroovyDeobfMapper;
import com.cleanroommc.groovyscript.sandbox.security.GrSMetaClassCreationHandle;
import com.cleanroommc.groovyscript.server.GroovyScriptLanguageServer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import groovy.lang.GroovySystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;

@GroovyBlacklist
@Mod(modid = GroovyScript.ID,
     name = GroovyScript.NAME,
     version = GroovyScript.VERSION,
     dependencies = "after:mixinbooter@[8.0,);",
     guiFactory = "com.cleanroommc.groovyscript.DisabledConfigGui")
@Mod.EventBusSubscriber(modid = GroovyScript.ID)
public class GroovyScript {

    public static final String ID = Tags.MODID;
    public static final String NAME = "GroovyScript";
    public static final String VERSION = Tags.VERSION;

    public static final String MC_VERSION = "1.12.2";
    public static final String GROOVY_VERSION = Tags.GROOVY_VERSION;

    public static final Logger LOGGER = LogManager.getLogger(ID);

    private static File minecraftHome;
    private static File scriptPath;
    private static File runConfigFile;
    private static File resourcesFile;
    private static RunConfig runConfig;
    private static GroovyScriptSandbox sandbox;
    private static ModContainer scriptMod;
    private static Thread languageServerThread;

    private static KeyBinding reloadKey;
    private static long timeSinceLastUse = 0;

    public static final Random RND = new Random();

    @Mod.EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        NetworkHandler.init();
        GroovySystem.getMetaClassRegistry().setMetaClassCreationHandle(GrSMetaClassCreationHandle.INSTANCE);
        GroovyDeobfMapper.init();
        LinkGeneratorHooks.init();
        ReloadableRegistryManager.init();
        try {
            sandbox = new GroovyScriptSandbox(scriptPath, FileUtil.makeFile(FileUtil.getMinecraftHome(), "cache", "groovy"));
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Error initializing sandbox!");
        }
        ModSupport.INSTANCE.setup(event.getASMHarvestedData());

        if (NetworkUtils.isDedicatedClient()) {
            // this resource pack must be added in construction
            ((DefaultResourcePackAccessor) Minecraft.getMinecraft()).get().add(new GroovyResourcePack());
            reloadKey = new KeyBinding("key.groovyscript.reload", KeyConflictContext.IN_GAME, KeyModifier.CONTROL, Keyboard.KEY_R, "key.categories.groovyscript");
            ClientRegistry.registerKeyBinding(reloadKey);
        }

        FluidRegistry.enableUniversalBucket();
        getRunConfig().initPackmode();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        if (ModSupport.TINKERS_CONSTRUCT.isLoaded()) TinkersConstruct.init();
        if (Boolean.parseBoolean(System.getProperty("groovyscript.run_ls"))) {
            runLanguageServer();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRegisterItem(RegistryEvent.Register<Item> event) {
        if (ModSupport.TINKERS_CONSTRUCT.isLoaded()) TinkersConstruct.preInit();
    }

    @ApiStatus.Internal
    public static void initializeRunConfig(File minecraftHome) {
        GroovyScript.minecraftHome = minecraftHome;
        // If we are launching with the environment variable set to use the examples folder, use the examples folder for easy and consistent testing.
        if (Boolean.parseBoolean(System.getProperty("groovyscript.use_examples_folder"))) {
            scriptPath = new File(minecraftHome.getParentFile(), "examples");
        } else {
            scriptPath = new File(minecraftHome, "groovy");
        }
        runConfigFile = new File(scriptPath, "runConfig.json");
        resourcesFile = new File(scriptPath, "assets");
        reloadRunConfig(true);
    }

    @ApiStatus.Internal
    public static void initializeGroovyPreInit() {
        // called via mixin in between construction and fml pre init
        GameObjectHandlerManager.init();
        VanillaModule.initializeBinding();
        ModSupport.init();
        if (FMLLaunchHandler.isDeobfuscatedEnvironment()) Documentation.generate();
        runGroovyScriptsInLoader(LoadStage.PRE_INIT);
    }

    @ApiStatus.Internal
    public static long runGroovyScriptsInLoader(LoadStage loadStage) {
        // called via mixin between fml post init and load complete
        if (!getRunConfig().isLoaderConfigured(loadStage.getName())) {
            GroovyLog.get().infoMC("Skipping load stage {}, since no scripts are configured!", loadStage.getName());
            return -1;
        }
        if (scriptMod == null) scriptMod = Loader.instance().getIndexedModList().get(getRunConfig().getPackId());
        ModContainer current = Loader.instance().activeModContainer();
        Loader.instance().setActiveModContainer(scriptMod);
        long time = System.currentTimeMillis();
        getSandbox().run(loadStage);
        time = System.currentTimeMillis() - time;
        LOGGER.info("Running Groovy scripts during {} took {} ms", loadStage.getName(), time);
        Loader.instance().setActiveModContainer(current);
        return time;
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        CustomClickAction.registerAction("copy", value -> {
            GuiScreen.setClipboardString(value);
            Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("groovyscript.command.copy.copied_start")
                                                                .appendSibling(new TextComponentString(value).setStyle(new Style().setColor(TextFormatting.GOLD)))
                                                                .appendSibling(new TextComponentTranslation("groovyscript.command.copy.copied_end")));
        });
    }

    @Mod.EventHandler
    public void onServerLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new GSCommand());
    }

    @SubscribeEvent
    public static void onInput(InputEvent.KeyInputEvent event) {
        long time = Minecraft.getSystemTime();
        if (Minecraft.getMinecraft().isIntegratedServerRunning() && reloadKey.isPressed() && time - timeSinceLastUse >= 1000 && Minecraft.getMinecraft().player.getPermissionLevel() >= 4) {
            NetworkHandler.sendToServer(new CReload());
            timeSinceLastUse = time;
        }
    }

    @NotNull
    public static String getScriptPath() {
        return getScriptFile().getPath();
    }

    @NotNull
    public static File getMinecraftHome() {
        if (minecraftHome == null) {
            throw new IllegalStateException("GroovyScript is not yet loaded!");
        }
        return minecraftHome;
    }

    @NotNull
    public static File getScriptFile() {
        if (scriptPath == null) {
            throw new IllegalStateException("GroovyScript is not yet loaded!");
        }
        return scriptPath;
    }

    @NotNull
    public static File getResourcesFile() {
        if (resourcesFile == null) {
            throw new IllegalStateException("GroovyScript is not yet loaded!");
        }
        return resourcesFile;
    }

    @NotNull
    public static File getRunConfigFile() {
        if (runConfigFile == null) {
            throw new IllegalStateException("GroovyScript is not yet loaded!");
        }
        return runConfigFile;
    }

    @NotNull
    public static GroovyScriptSandbox getSandbox() {
        if (sandbox == null) {
            throw new IllegalStateException("GroovyScript is not yet loaded!");
        }
        return sandbox;
    }

    public static boolean isSandboxLoaded() {
        return sandbox != null;
    }

    public static RunConfig getRunConfig() {
        return runConfig;
    }

    @ApiStatus.Internal
    public static void reloadRunConfig(boolean init) {
        JsonElement element = JsonHelper.loadJson(runConfigFile);
        if (element == null || !element.isJsonObject()) element = new JsonObject();
        JsonObject json = element.getAsJsonObject();
        if (runConfig == null) {
            if (!Files.exists(runConfigFile.toPath())) {
                json = RunConfig.createDefaultJson();
                runConfig = createRunConfig(json);
            } else {
                runConfig = new RunConfig(json);
            }
        }
        runConfig.reload(json, init);
    }

    private static RunConfig createRunConfig(JsonObject json) {
        JsonHelper.saveJson(runConfigFile, json);
        File main = new File(scriptPath.getPath() + File.separator + "postInit" + File.separator + "main.groovy");
        if (!Files.exists(main.toPath())) {
            try {
                main.getParentFile().mkdirs();
                Files.write(main.toPath(), "\nprintln('Hello World!')\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new RunConfig(json);
    }

    public static void postScriptRunResult(ICommandSender sender, boolean onlyLogFails, boolean running, boolean packmode, long time) {
        List<String> errors = GroovyLogImpl.LOG.collectErrors();
        if (errors.isEmpty()) {
            if (!onlyLogFails) {
                if (running) {
                    String s = packmode ? "changes packmode" : "reloaded scripts";
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully " + s + TextFormatting.WHITE + " in " + time + "ms"));
                } else {
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "No syntax errors found :)"));
                }
            }
        } else {
            String executing = running ? "running" : "checking";
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Found " + errors.size() + " errors while " + executing + " scripts"));
            int n = errors.size();
            if (errors.size() >= 10) {
                sender.sendMessage(new TextComponentString("Displaying the first 7 errors:"));
                n = 7;
            }
            for (int i = 0; i < n; i++) {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + errors.get(i)));
            }
            GSCommand.postLogFiles(sender);
        }
    }

    public static boolean runLanguageServer() {
        if (languageServerThread != null) return false;
        languageServerThread = new Thread(() -> GroovyScriptLanguageServer.listen(getSandbox().getScriptRoot()));
        languageServerThread.start();
        return true;
    }
}
