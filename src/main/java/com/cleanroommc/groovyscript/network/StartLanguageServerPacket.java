package com.cleanroommc.groovyscript.network;

import com.cleanroommc.groovyscript.GroovyScript;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class StartLanguageServerPacket implements IPacket {

    @Override
    public void encode(PacketBuffer buf) {}

    @Override
    public void decode(PacketBuffer buf) {}

    @Override
    public IPacket executeClient(NetHandlerPlayClient handler) {
        if (GroovyScript.runLanguageServer()) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Starting language server"));
        } else {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Language server is already running"));
        }
        return null;
    }

}
