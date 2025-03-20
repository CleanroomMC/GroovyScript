package com.cleanroommc.groovyscript;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class IncompatibleJavaException extends CustomModLoadingErrorDisplayException {

    private final String msg;

    public IncompatibleJavaException(String msg) {
        this.msg = msg;
    }

    @Override
    public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {}

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
        List<String> lines = fontRenderer.listFormattedStringToWidth(this.msg, errorScreen.width - 40);
        int buttonSpace = 20 + 2 * 18; // button is 18 pixel high + 18 pixel margin on both sides
        int y = (errorScreen.height - buttonSpace) / 2 - (fontRenderer.FONT_HEIGHT * 2 + 1) / 2; // font height * 2 /
        for (String line : lines) {
            int width = fontRenderer.getStringWidth(line);
            int x = errorScreen.width / 2 - width / 2;
            fontRenderer.drawStringWithShadow(line, x, y, 0xFFFFFFFF);
            y += fontRenderer.FONT_HEIGHT;
        }
    }
}
