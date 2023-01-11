package com.cleanroommc.groovyscript;

import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.command.CustomClickAction;
import com.cleanroommc.groovyscript.command.GSCommand;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.crystal.CrystalItemStackExpansion;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectItemStackExpansion;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.warp.WarpItemStackExpansion;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.ExpansionHelper;
import com.cleanroommc.groovyscript.sandbox.GroovyDeobfuscationMapper;
import com.cleanroommc.groovyscript.sandbox.GroovyScriptSandbox;
import com.cleanroommc.groovyscript.sandbox.RunConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
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

@Mod(modid = GroovyScript.ID, name = GroovyScript.NAME, version = GroovyScript.VERSION)
@Mod.EventBusSubscriber(modid = GroovyScript.ID)
public class GroovyScript {

    public static final String ID = "groovyscript";
    public static final String NAME = "GroovyScript";
    public static final String VERSION = "0.1.0";

    public static final Logger LOGGER = LogManager.getLogger(ID);

    private static File scriptPath;
    private static File runConfigFile;
    private static RunConfig runConfig;
    private static GroovyScriptSandbox sandbox;

    private static final KeyBinding reloadKey = new KeyBinding("key.groovyscript.reload", KeyConflictContext.IN_GAME, KeyModifier.CONTROL, Keyboard.KEY_R, "key.categories.groovyscript");
    private static long timeSinceLastUse = 0;

    @Mod.EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        NetworkHandler.init();
        GroovyDeobfuscationMapper.init();
        ReloadableRegistryManager.init();
        scriptPath = new File(Loader.instance().getConfigDir().toPath().getParent().toString() + File.separator + "groovy");
        try {
            sandbox = new GroovyScriptSandbox(getScriptFile().toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Error initializing sandbox!");
        }
        runConfigFile = new File(scriptPath.getPath() + File.separator + "runConfig.json");
        reloadRunConfig();
        BracketHandlerManager.init();
        VanillaModule.initializeBinding();
        registerExpansions();

        getSandbox().run(GroovyScriptSandbox.LOADER_PRE_INIT);
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        getSandbox().run(GroovyScriptSandbox.LOADER_POST_INIT);

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
        if (event.getServer() instanceof IntegratedServer) {
            ClientRegistry.registerKeyBinding(reloadKey);
        }
    }

    @SubscribeEvent
    public static void onInput(InputEvent.KeyInputEvent event) {
        long time = Minecraft.getSystemTime();
        if (reloadKey.isPressed() && time - timeSinceLastUse >= 1000 && Minecraft.getMinecraft().player.getPermissionLevel() >= 4) {
            GSCommand.runReload(Minecraft.getMinecraft().player, null);
            timeSinceLastUse = time;
        }
    }

    private void registerExpansions() {
        if (ModSupport.THAUMCRAFT.isLoaded()) {
            ExpansionHelper.mixinClass(ItemStack.class, AspectItemStackExpansion.class);
            ExpansionHelper.mixinClass(ItemStack.class, WarpItemStackExpansion.class);
        }
        if (ModSupport.ASTRAL_SORCERY.isLoaded()) {
            ExpansionHelper.mixinClass(ItemStack.class, CrystalItemStackExpansion.class);
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
}
