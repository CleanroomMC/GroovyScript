package com.cleanroommc.groovyscript.keybind;

import com.cleanroommc.groovyscript.network.CReload;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.input.Keyboard;

public class ReloadKey extends GroovyScriptKeybinds.Key {

    private long timeSinceLastUse;

    public ReloadKey() {
        super("reload", KeyConflictContext.IN_GAME, KeyModifier.CONTROL, Keyboard.KEY_R);
    }

    public static void createKeybind() {
        GroovyScriptKeybinds.addKey(new ReloadKey());
    }

    @Override
    public boolean isValid() {
        return Minecraft.getMinecraft().currentScreen == null && Minecraft.getMinecraft().inGameHasFocus;
    }

    @Override
    public void handleKeybind() {
        long time = Minecraft.getSystemTime();
        if (Minecraft.getMinecraft().isIntegratedServerRunning() && time - timeSinceLastUse >= 1000 && Minecraft.getMinecraft().player.getPermissionLevel() >= 4) {
            NetworkHandler.sendToServer(new CReload());
            timeSinceLastUse = time;
        }
    }
}
