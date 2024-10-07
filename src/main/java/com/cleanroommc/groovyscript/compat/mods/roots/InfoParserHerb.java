package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import epicsquid.roots.api.Herb;
import epicsquid.roots.init.HerbRegistry;
import org.jetbrains.annotations.NotNull;

public class InfoParserHerb extends GenericInfoParser<Herb> {

    public static final InfoParserHerb instance = new InfoParserHerb();

    @Override
    public String id() {
        return "herb";
    }

    @Override
    public String name() {
        return "Herb";
    }

    @Override
    public String text(@NotNull Herb entry, boolean colored, boolean prettyNbt) {
        return Roots.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;
        if (HerbRegistry.isHerb(info.getStack())) {
            Herb herb = HerbRegistry.getHerbByItem(info.getStack().getItem());
            if (herb == null) return;
            instance.add(info.getMessages(), herb, info.isPrettyNbt());
        }
    }
}
