package com.cleanroommc.groovyscript.network;

import com.cleanroommc.groovyscript.command.GSCommand;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;

public class CReload implements IPacket {

    @Override
    public void encode(PacketBuffer buf) {}

    @Override
    public void decode(PacketBuffer buf) {}

    @Override
    public IPacket executeServer(NetHandlerPlayServer handler) {
        GSCommand.runReload(handler.player, handler.player.getServer());
        return null;
    }
}
