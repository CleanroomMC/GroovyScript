package com.cleanroommc.groovyscript.keybinds;

import com.cleanroommc.groovyscript.network.CReload;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.input.Keyboard;

public class ReloadKey extends GroovyScriptKeybinds.Key {

    private static final long INTERVAL = 1000L;
    private static final int PERMISSION_LEVEL = 4;

    private static final Minecraft mc = Minecraft.getMinecraft();

    private long timeSinceLastUse;

    public ReloadKey() {
        super("reload", KeyConflictContext.IN_GAME, KeyModifier.CONTROL, Keyboard.KEY_R);
    }

    public static ReloadKey createKeybind() {
        return new ReloadKey();
    }

    @Override
    public boolean isValid() {
        return mc.currentScreen == null && mc.inGameHasFocus;
    }

    @Override
    public void runOperation() {
        long time = Minecraft.getSystemTime();
        if (mc.isIntegratedServerRunning() && time - timeSinceLastUse >= INTERVAL && mc.player.getPermissionLevel() >= PERMISSION_LEVEL) {
            NetworkHandler.sendToServer(new CReload());
            timeSinceLastUse = time;
        }
    }
}
