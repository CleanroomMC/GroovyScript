package com.cleanroommc.groovyscript.network;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import org.apache.commons.lang3.StringUtils;

public class SCopy implements IPacket {

    private String[] text;

    public SCopy(String[] text) {
        this.text = text;
    }

    public SCopy() {}

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeVarInt(text.length);
        for (String t : text) {
            NetworkUtils.writeStringSafe(buf, t);
        }
    }

    @Override
    public void decode(PacketBuffer buf) {
        text = new String[buf.readVarInt()];
        for (int i = 0; i < text.length; i++) {
            text[i] = buf.readString(Short.MAX_VALUE);
        }
    }

    @Override
    public IPacket executeClient(NetHandlerPlayClient handler) {
        GuiScreen.setClipboardString(StringUtils.join(text, ' '));
        return null;
    }
}
