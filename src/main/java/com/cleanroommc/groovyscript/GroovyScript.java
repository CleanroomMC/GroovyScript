package com.cleanroommc.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.command.CustomClickAction;
import com.cleanroommc.groovyscript.command.GSCommand;
import com.cleanroommc.groovyscript.compat.content.GroovyResourcePack;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.core.mixin.DefaultResourcePackAccessor;
import com.cleanroommc.groovyscript.event.EventHandler;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.cleanroommc.groovyscript.network.CReload;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.network.NetworkUtils;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.GroovyLogImpl;
import com.cleanroommc.groovyscript.sandbox.GroovyScriptSandbox;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import com.cleanroommc.groovyscript.sandbox.RunConfig;
import com.cleanroommc.groovyscript.sandbox.mapper.GroovyDeobfMapper;
import com.cleanroommc.groovyscript.sandbox.security.GrSMetaClassCreationHandle;
import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import groovy.lang.GroovySystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
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

@GroovyBlacklist
@Mod(modid = GroovyScript.ID, name = GroovyScript.NAME, version = GroovyScript.VERSION)
@Mod.EventBusSubscriber(modid = GroovyScript.ID)
public class GroovyScript {

    public static final String ID = "groovyscript";
    public static final String NAME = "GroovyScript";
    public static final String VERSION = "0.4.2";

    public static final String MC_VERSION = "1.12.2";
    public static final String GROOVY_VERSION = "4.0.8";

    public static final Logger LOGGER = LogManager.getLogger(ID);

    private static File scriptPath;
    private static File runConfigFile;
    private static File resourcesFile;
    private static RunConfig runConfig;
    private static GroovyScriptSandbox sandbox;

    private static KeyBinding reloadKey;
    private static long timeSinceLastUse = 0;

    private static final Joiner fileJoiner = Joiner.on(File.separator);

    @Mod.EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        NetworkHandler.init();
        GroovySystem.getMetaClassRegistry().setMetaClassCreationHandle(GrSMetaClassCreationHandle.INSTANCE);
        GroovyDeobfMapper.init();
        ReloadableRegistryManager.init();
        scriptPath = new File(Loader.instance().getConfigDir().toPath().getParent().toString() + File.separator + "groovy");
        try {
            sandbox = new GroovyScriptSandbox(getScriptFile().toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Error initializing sandbox!");
        }
        runConfigFile = new File(scriptPath, "runConfig.json");
        resourcesFile = new File(scriptPath, "assets");
        reloadRunConfig();

        if (NetworkUtils.isDedicatedClient()) {
            // this resource pack must be added in construction
            ((DefaultResourcePackAccessor) Minecraft.getMinecraft()).get().add(new GroovyResourcePack());
            reloadKey = new KeyBinding("key.groovyscript.reload", KeyConflictContext.IN_GAME, KeyModifier.CONTROL, Keyboard.KEY_R, "key.categories.groovyscript");
            ClientRegistry.registerKeyBinding(reloadKey);
        }
    }

    @ApiStatus.Internal
    public static void initializeGroovyPreInit() {
        // called via mixin in between construction and fml pre init
        BracketHandlerManager.init();
        VanillaModule.initializeBinding();
        ModSupport.init();

        boolean wasNull = Loader.instance().activeModContainer() == null;
        if (wasNull) {
            Loader.instance().setActiveModContainer(Loader.instance().getIndexedModList().get(ID));
        }

        getSandbox().run(LoadStage.PRE_INIT);

        if (wasNull) {
            Loader.instance().setActiveModContainer(null);
        }
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        getSandbox().run(LoadStage.POST_INIT);

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
        if (reloadKey.isPressed() && time - timeSinceLastUse >= 1000 && Minecraft.getMinecraft().player.getPermissionLevel() >= 4) {
            NetworkHandler.sendToServer(new CReload());
            timeSinceLastUse = time;
        }
    }

    @NotNull
    public static String getScriptPath() {
        return getScriptFile().getPath();
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
    public static GroovyScriptSandbox getSandbox() {
        if (sandbox == null) {
            throw new IllegalStateException("GroovyScript is not yet loaded!");
        }
        return sandbox;
    }

    public static RunConfig getRunConfig() {
        return runConfig;
    }

    @ApiStatus.Internal
    public static void reloadRunConfig() {
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
        runConfig.reload(json);
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

    public static File makeFile(String... pieces) {
        return new File(fileJoiner.join(pieces));
    }

    public static File makeFile(File parent, String... pieces) {
        return new File(parent, fileJoiner.join(pieces));
    }

    public static void postScriptRunResult(EntityPlayerMP player, boolean startup) {
        List<String> errors = GroovyLogImpl.LOG.collectErrors();
        if (errors.isEmpty()) {
            if (!startup) {
                player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully ran scripts"));
            }
        } else {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "Found " + errors.size() + " errors while executing scripts"));
            int n = errors.size();
            if (errors.size() >= 10) {
                player.sendMessage(new TextComponentString("Displaying the first 7 errors:"));
                n = 7;
            }
            for (int i = 0; i < n; i++) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + errors.get(i)));
            }
            player.server.commandManager.executeCommand(player, "/gs log");
        }
    }
}
