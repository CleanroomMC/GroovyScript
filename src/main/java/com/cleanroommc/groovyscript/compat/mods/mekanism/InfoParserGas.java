package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.InfoParserTranslationKey;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import org.jetbrains.annotations.NotNull;

public class InfoParserGas extends GenericInfoParser<GasStack> {

    public static final InfoParserGas instance = new InfoParserGas();

    @Override
    public String id() {
        return "gas";
    }

    @Override
    public String name() {
        return "Gas";
    }

    @Override
    public String plural() {
        return "Gases";
    }

    @Override
    public String text(@NotNull GasStack entry, boolean colored, boolean prettyNbt) {
        return Mekanism.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().getItem() instanceof IGasItem item) {
            GasStack stack = item.getGas(info.getStack());
            instance.add(info.getMessages(), stack, info.isPrettyNbt());
            InfoParserTranslationKey.instance.add(info.getMessages(), stack.getGas().getTranslationKey(), info.isPrettyNbt());
        }
    }
}
