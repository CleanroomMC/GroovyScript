package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class InfoParserItem extends GenericInfoParser<ItemStack> {

    public static final InfoParserItem instance = new InfoParserItem();

    private static void copyToClipboard(String text) {
        GuiScreen.setClipboardString(text);
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public String id() {
        return "item";
    }

    @Override
    public String name() {
        return "Item";
    }

    @Override
    public String text(@NotNull ItemStack entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored, prettyNbt);
    }

    @Override
    public void iterate(List<ITextComponent> messages, @NotNull Iterator<ItemStack> entries, boolean prettyNbt) {
        if (entries.hasNext()) {
            ItemStack entry = entries.next();
            messages.add(information(entry, prettyNbt));
            // can only copy to clipboard if a client is running this
            if (FMLCommonHandler.instance().getSide().isClient()) copyToClipboard(copyText(entry, false));
        }
        while (entries.hasNext()) {
            messages.add(information(entries.next(), prettyNbt));
        }
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;
        instance.add(info.getMessages(), info.getStack(), info.isPrettyNbt());
        InfoParserTranslationKey.instance.add(info.getMessages(), info.getStack().getTranslationKey(), info.isPrettyNbt());
    }
}
