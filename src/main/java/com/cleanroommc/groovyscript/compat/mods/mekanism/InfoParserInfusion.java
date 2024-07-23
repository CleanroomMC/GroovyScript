package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.InfoParserTranslationKey;
import mekanism.api.infuse.InfuseObject;
import mekanism.api.infuse.InfuseRegistry;
import org.jetbrains.annotations.NotNull;

public class InfoParserInfusion extends GenericInfoParser<InfuseObject> {

    public static final InfoParserInfusion instance = new InfoParserInfusion();

    @Override
    public String id() {
        return "infusion";
    }

    @Override
    public String name() {
        return "Infusion Type";
    }

    @Override
    public String text(@NotNull InfuseObject entry, boolean colored, boolean prettyNbt) {
        return Mekanism.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;
        InfuseObject obj = InfuseRegistry.getObject(info.getStack());
        if (obj == null) return;
        instance.add(info.getMessages(), obj, info.isPrettyNbt());
        InfoParserTranslationKey.instance.add(info.getMessages(), obj.type.unlocalizedName, info.isPrettyNbt());
    }

}
