package com.cleanroommc.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.command.CustomClickAction;
import com.cleanroommc.groovyscript.command.GSCommand;
import com.cleanroommc.groovyscript.compat.content.GroovyResourcePack;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.TinkersConstruct;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.StandardInfoParserRegistry;
import com.cleanroommc.groovyscript.core.mixin.DefaultResourcePackAccessor;
import com.cleanroommc.groovyscript.documentation.Documentation;
import com.cleanroommc.groovyscript.documentation.linkgenerator.LinkGeneratorHooks;
import com.cleanroommc.groovyscript.event.EventHandler;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.cleanroommc.groovyscript.helper.StyleConstant;
import com.cleanroommc.groovyscript.mapper.AbstractObjectMapper;
import com.cleanroommc.groovyscript.mapper.ObjectMapperManager;
import com.cleanroommc.groovyscript.network.CReload;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.network.NetworkUtils;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.*;
import com.cleanroommc.groovyscript.sandbox.mapper.GroovyDeobfMapper;
import com.cleanroommc.groovyscript.sandbox.meta.GrSMetaClassCreationHandle;
import com.cleanroommc.groovyscript.server.GroovyScriptLanguageServerImpl;
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
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;

@GroovyBlacklist
@Mod(
        modid = GroovyScript.ID,
        name = GroovyScript.NAME,
        version = GroovyScript.VERSION,
        dependencies = "after:mixinbooter@[8.0,);after:extendedcrafting@[1.6.0,);",
        guiFactory = "com.cleanroommc.groovyscript.DisabledConfigGui")
@Mod.EventBusSubscriber(modid = GroovyScript.ID)
public class GroovyScript {

    public static final String ID = Tags.MODID;
    public static final String NAME = "GroovyScript";
    public static final String VERSION = Tags.VERSION;

    public static final String MC_VERSION = "1.12.2";
    public static final String GROOVY_VERSION = Tags.GROOVY_VERSION;

    public static final Logger LOGGER = LogManager.getLogger(ID);

    private static ModContainer grsContainer;
    private static GroovyScriptSandbox sandbox;
    private static RunConfig runConfig;
    private static ModContainer scriptMod;
    private static Thread languageServerThread;

    private static KeyBinding reloadKey;
    private static long timeSinceLastUse;

    public static final Random RND = new Random();

    @Mod.EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        if (!SandboxData.isInitialised()) {
            LOGGER.throwing(new IllegalStateException("Sandbox data should have been initialised by now, but isn't! Trying to initialize again."));
            SandboxData.initialize((File) FMLInjectionData.data()[6], LOGGER);
        }
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        NetworkHandler.init();
        GroovySystem.getMetaClassRegistry().setMetaClassCreationHandle(GrSMetaClassCreationHandle.INSTANCE);
        GroovySystem.getMetaClassRegistry().getMetaClassCreationHandler().setDisableCustomMetaClassLookup(true);
        GroovyDeobfMapper.init();
        LinkGeneratorHooks.init();
        ReloadableRegistryManager.init();
        GroovyScript.sandbox = new GroovyScriptSandbox();
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
        if (event.getSide().isClient() && Boolean.parseBoolean(System.getProperty("groovyscript.run_ls"))) {
            runLanguageServer();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRegisterItem(RegistryEvent.Register<Item> event) {
        if (ModSupport.TINKERS_CONSTRUCT.isLoaded()) TinkersConstruct.preInit();
    }

    @ApiStatus.Internal
    public static void initializeRunConfig(File minecraftHome) {
        SandboxData.initialize(minecraftHome, LOGGER);
        reloadRunConfig(true);
    }

    @ApiStatus.Internal
    public static void initializeGroovyPreInit() {
        // called via mixin in between construction and fml pre init
        ObjectMapperManager.init();
        StandardInfoParserRegistry.init();
        ModSupport.init();
        for (AbstractObjectMapper<?> goh : ObjectMapperManager.getObjectMappers()) {
            getSandbox().registerBinding(goh);
        }
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
            var message = new TextComponentTranslation("groovyscript.command.copy.copied_start").setStyle(StyleConstant.getEmphasisStyle())
                    .appendSibling(new TextComponentString(value).setStyle(new Style().setColor(TextFormatting.RESET)))
                    .appendSibling(new TextComponentTranslation("groovyscript.command.copy.copied_end").setStyle(StyleConstant.getEmphasisStyle()));
            Minecraft.getMinecraft().player.sendMessage(message);
        });
    }

    @Mod.EventHandler
    public void onServerLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new GSCommand());
        VanillaModule.command.onStartServer(event.getServer());
    }

    @SubscribeEvent
    public static void onInput(InputEvent.KeyInputEvent event) {
        long time = Minecraft.getSystemTime();
        if (Minecraft.getMinecraft().isIntegratedServerRunning() && reloadKey.isPressed() && time - timeSinceLastUse >= 1000 && Minecraft.getMinecraft().player.getPermissionLevel() >= 4) {
            NetworkHandler.sendToServer(new CReload());
            timeSinceLastUse = time;
        }
    }

    public static @NotNull String getScriptPath() {
        return getScriptFile().getPath();
    }

    public static @NotNull File getMinecraftHome() {
        return SandboxData.getMinecraftHome();
    }

    public static @NotNull File getScriptFile() {
        return SandboxData.getScriptFile();
    }

    public static @NotNull File getResourcesFile() {
        return SandboxData.getResourcesFile();
    }

    public static @NotNull File getRunConfigFile() {
        return SandboxData.getRunConfigFile();
    }

    public static @NotNull GroovyScriptSandbox getSandbox() {
        if (sandbox == null) {
            throw new IllegalStateException("GroovyScript is not yet loaded or failed to load!");
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
        JsonElement element = JsonHelper.loadJson(getRunConfigFile());
        if (element == null || !element.isJsonObject()) element = new JsonObject();
        JsonObject json = element.getAsJsonObject();
        if (runConfig == null) {
            if (!Files.exists(getRunConfigFile().toPath())) {
                json = RunConfig.createDefaultJson();
                runConfig = createRunConfig(json);
            } else {
                runConfig = new RunConfig(json);
            }
        }
        runConfig.reload(json, init);
    }

    private static RunConfig createRunConfig(JsonObject json) {
        JsonHelper.saveJson(getRunConfigFile(), json);
        File main = new File(getScriptFile().getPath() + File.separator + "postInit" + File.separator + "main.groovy");
        if (!Files.exists(main.toPath())) {
            try {
                main.getParentFile().mkdirs();
                Files.write(main.toPath(), "\nlog.info('Hello World!')\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
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
                    sender.sendMessage(new TextComponentString("Successfully " + s).setStyle(StyleConstant.getSuccessStyle()).appendSibling(new TextComponentString(" in " + time + "ms")));
                } else {
                    sender.sendMessage(new TextComponentString("No syntax errors found :)").setStyle(StyleConstant.getSuccessStyle()));
                }
            }
        } else {
            String executing = running ? "running" : "checking";
            sender.sendMessage(new TextComponentString("Found " + errors.size() + " errors while " + executing + " scripts").setStyle(StyleConstant.getErrorStyle()));
            int n = errors.size();
            if (errors.size() >= 10) {
                sender.sendMessage(new TextComponentString("Displaying the first 7 errors:").setStyle(StyleConstant.getTitleStyle()));
                n = 7;
            }
            for (int i = 0; i < n; i++) {
                sender.sendMessage(new TextComponentString(errors.get(i)).setStyle(StyleConstant.getErrorStyle()));
            }
            GSCommand.postLogFiles(sender);
        }
    }

    @ApiStatus.Internal
    public static boolean runLanguageServer() {
        if (languageServerThread != null) return false;
        languageServerThread = new Thread(() -> GroovyScriptLanguageServerImpl.listen(getSandbox().getScriptRoot()));
        languageServerThread.start();
        return true;
    }

    public static void doForGroovyScript(Runnable runnable) {
        ModContainer current = Loader.instance().activeModContainer();
        if (grsContainer == null) {
            grsContainer = Loader.instance().getIndexedModList().get(ID);
        }
        Loader.instance().setActiveModContainer(grsContainer);
        runnable.run();
        Loader.instance().setActiveModContainer(current);
    }
}
