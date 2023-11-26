package com.cleanroommc.groovyscript.packmode;

import com.cleanroommc.groovyscript.GroovyScript;
import com.google.common.base.CaseFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PackmodeButton extends GuiButton {

    private int index = -1;
    private String packmode;
    private String desc;

    public PackmodeButton(int buttonId, int x, int y, int widthIn, int heightIn) {
        super(buttonId, x, y, widthIn, heightIn, "");
        this.enabled = Packmode.needsPackmode();
        updatePackmode();
    }

    @Override
    public void drawButton(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.enabled) {
            super.drawButton(mc, mouseX, mouseY, partialTicks);
        }
    }

    public void updatePackmode() {
        if (!Packmode.needsPackmode()) return;
        List<String> all = GroovyScript.getRunConfig().getPackmodeList();
        if (++index >= all.size()) index = 0;
        this.packmode = all.get(this.index);
        if (I18n.hasKey("groovyscript.packmode." + this.packmode + ".name")) {
            this.displayString = "Packmode: " + I18n.format("groovyscript.packmode." + this.packmode + ".name");
        } else {
            this.displayString = "Packmode: " + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, this.packmode);
        }
        if (I18n.hasKey("groovyscript.packmode." + this.packmode + ".description")) {
            this.desc = I18n.format("groovyscript.packmode." + this.packmode + ".description");
        } else {
            this.desc = null;
        }
    }

    public String getPackmode() {
        return packmode;
    }

    public String getDesc() {
        return desc;
    }
}
