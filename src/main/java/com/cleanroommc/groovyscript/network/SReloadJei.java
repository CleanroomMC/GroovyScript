package com.cleanroommc.groovyscript.network;

import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;

public class SReloadJei implements IPacket {

    @Override
    public void encode(PacketBuffer buf) {

    }

    @Override
    public void decode(PacketBuffer buf) {

    }

    @Override
    public IPacket executeClient(NetHandlerPlayClient handler) {
        ReloadableRegistryManager.reloadJei();
        return null;
    }
}
