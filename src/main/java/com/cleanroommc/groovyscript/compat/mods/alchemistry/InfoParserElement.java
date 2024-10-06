package com.cleanroommc.groovyscript.compat.mods.alchemistry;

import al132.alchemistry.chemistry.ChemicalElement;
import al132.alchemistry.chemistry.ElementRegistry;
import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InfoParserElement extends GenericInfoParser<ChemicalElement> {

    public static final InfoParserElement instance = new InfoParserElement();

    @Override
    public String id() {
        return "element";
    }

    @Override
    public String name() {
        return "Element";
    }

    @Override
    public String text(@NotNull ChemicalElement entry, boolean colored, boolean prettyNbt) {
        return Alchemistry.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;
        for (ChemicalElement x : ElementRegistry.INSTANCE.getAllElements()) {
            if (ItemStack.areItemsEqual(x.toItemStack(1), info.getStack())) {
                instance.add(info.getMessages(), x, info.isPrettyNbt());
            }
        }
    }
}
