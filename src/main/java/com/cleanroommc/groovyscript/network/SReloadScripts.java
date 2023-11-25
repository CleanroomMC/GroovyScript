package com.cleanroommc.groovyscript.network;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.packmode.Packmode;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentString;

public class SReloadScripts implements IPacket {

    private String packmode;
    private boolean changePackmode;
    private boolean reloadJei;

    public SReloadScripts() {
    }

    public SReloadScripts(String packmode, boolean changePackmode, boolean reloadJei) {
        this.packmode = packmode;
        this.changePackmode = changePackmode;
        this.reloadJei = reloadJei;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeBoolean(this.changePackmode);
        if (this.changePackmode) {
            buf.writeBoolean(this.packmode == null);
            if (this.packmode != null) {
                NetworkUtils.writeStringSafe(buf, packmode);
            }
        }
        buf.writeBoolean(this.reloadJei);
    }

    @Override
    public void decode(PacketBuffer buf) {
        this.changePackmode = buf.readBoolean();
        if (this.changePackmode) {
            this.packmode = buf.readBoolean() ? null : buf.readString(128);
        }
        this.reloadJei = buf.readBoolean();
    }

    @Override
    public IPacket executeClient(NetHandlerPlayClient handler) {
        if (this.changePackmode && this.packmode != null) {
            updatePackmode(Minecraft.getMinecraft().player, this.packmode);
        }
        if (this.reloadJei) {
            ReloadableRegistryManager.reloadJei(!this.changePackmode);
            if (this.changePackmode) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Finished updating packmode and JEI. Enjoy :)"));
            }
        }
        return null;
    }

    public static void updatePackmode(EntityPlayer player, String packmode) {
        player.sendMessage(new TextComponentString("Updating packmode to '" + packmode + "'. This might take a few minutes."));
        Packmode.updatePackmode(packmode);
        GroovyScript.runGroovyScriptsInLoader(LoadStage.POST_INIT);
        player.sendMessage(new TextComponentString("Reloading JEI..."));
    }
}
