package com.cleanroommc.groovyscript.network;

import com.cleanroommc.groovyscript.command.GSCommand;
import com.google.common.collect.ImmutableList;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;

public class CReload implements IPacket {

    private boolean reloadJei;

    public CReload() {
        this(true);
    }

    public CReload(boolean reloadJei) {
        this.reloadJei = reloadJei;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeBoolean(this.reloadJei);
    }

    @Override
    public void decode(PacketBuffer buf) {
        this.reloadJei = buf.readBoolean();
    }

    @Override
    public IPacket executeServer(NetHandlerPlayServer handler) {
        GSCommand.runReload(handler.player, handler.player.getServer(), reloadJei ? ImmutableList.of() : ImmutableList.of("--skip-jei"));
        return null;
    }
}
