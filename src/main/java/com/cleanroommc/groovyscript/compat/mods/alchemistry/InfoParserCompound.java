package com.cleanroommc.groovyscript.compat.mods.alchemistry;

import al132.alchemistry.chemistry.ChemicalCompound;
import al132.alchemistry.chemistry.CompoundRegistry;
import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InfoParserCompound extends GenericInfoParser<ChemicalCompound> {

    public static final InfoParserCompound instance = new InfoParserCompound();

    @Override
    public String id() {
        return "compound";
    }

    @Override
    public String name() {
        return "Compound";
    }

    @Override
    public String text(@NotNull ChemicalCompound entry, boolean colored, boolean prettyNbt) {
        return Alchemistry.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;
        for (ChemicalCompound x : CompoundRegistry.INSTANCE.compounds()) {
            if (ItemStack.areItemsEqual(x.toItemStack(1), info.getStack())) {
                instance.add(info.getMessages(), x, info.isPrettyNbt());
            }
        }
    }
}
