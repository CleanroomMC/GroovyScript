package com.cleanroommc.groovyscript.compat;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class WarningScreen extends GuiScreen {

    public static boolean wasOpened = false;

    public final String title = TextFormatting.BOLD + "! Warning from GroovyScript !";
    public final List<String> messages;
    private List<String> lines;
    private int lastWidth = 0;

    public WarningScreen(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawString(this.fontRenderer, this.title, this.width / 2 - this.fontRenderer.getStringWidth(this.title) / 2, 14, 0xFFFFFFFF);
        int titleHeight = 14 + this.fontRenderer.FONT_HEIGHT;

        int x, y, w, h;
        if (this.width <= 110) {
            x = 10;
            w = this.width - 10;
        } else {
            w = MathHelper.clamp(this.width - 40, 110, 300);
            x = this.width / 2 - w / 2;
        }

        if (w != this.lastWidth) {
            this.lastWidth = w;
            this.lines = new ArrayList<>();
            for (String s : this.messages) {
                if (this.messages.size() > 1) s = "- " + s;
                this.lines.addAll(this.fontRenderer.listFormattedStringToWidth(s, w));
            }
        }
        h = this.lines.size() * (this.fontRenderer.FONT_HEIGHT + 5) - 6;
        y = Math.max(titleHeight + 2, this.height / 2 - h / 2);

        boolean first = true;
        for (String line : this.lines) {
            boolean msg = line.startsWith("- ");
            int x0 = x;
            if (msg) {
                x0 -= 10;
                if (!first) y += 3;
            }
            drawString(this.fontRenderer, line, x0, y, 0xFFFFFFFF);
            y += this.fontRenderer.FONT_HEIGHT;
            first = false;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    @Override
    public void initGui() {
        int buttonX = this.width / 2 - 100;
        int buttonY = this.height - 30;
        this.buttonList.add(new GuiButton(0, buttonX, buttonY, I18n.format("gui.toTitle")));
    }
}
