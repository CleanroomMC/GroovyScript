package com.cleanroommc.groovyscript.network;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.helper.StyleConstant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentString;

public class StartLanguageServerPacket implements IPacket {

    @Override
    public void encode(PacketBuffer buf) {}

    @Override
    public void decode(PacketBuffer buf) {}

    @Override
    public IPacket executeClient(NetHandlerPlayClient handler) {
        if (GroovyScript.runLanguageServer()) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Starting language server").setStyle(StyleConstant.SUCCESS_STYLE));
        } else {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Language server is already running").setStyle(StyleConstant.WARNING_STYLE));
        }
        return null;
    }
}
