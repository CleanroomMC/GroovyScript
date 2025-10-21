package com.cleanroommc.groovyscript.keybinds;

import com.cleanroommc.groovyscript.network.CReload;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import net.minecraft.client.Minecraft;

public class QuickReloadKey extends GroovyScriptKeybinds.Key {

    private static final long INTERVAL = 1000L;
    private static final int PERMISSION_LEVEL = 4;

    private static final Minecraft mc = Minecraft.getMinecraft();

    private long timeSinceLastUse;

    public QuickReloadKey() {
        super("quick_reload");
    }

    public static QuickReloadKey createKeybind() {
        return new QuickReloadKey();
    }

    @Override
    public boolean isValid() {
        return mc.currentScreen == null && mc.inGameHasFocus;
    }

    @Override
    public void runOperation() {
        long time = Minecraft.getSystemTime();
        if (mc.isIntegratedServerRunning() && time - timeSinceLastUse >= INTERVAL && mc.player.getPermissionLevel() >= PERMISSION_LEVEL) {
            NetworkHandler.sendToServer(new CReload(false));
            timeSinceLastUse = time;
        }
    }
}
