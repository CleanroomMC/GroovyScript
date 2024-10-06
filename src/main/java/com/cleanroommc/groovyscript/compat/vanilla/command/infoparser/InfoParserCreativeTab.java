package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraft.creativetab.CreativeTabs;
import org.jetbrains.annotations.NotNull;

public class InfoParserCreativeTab extends GenericInfoParser<CreativeTabs> {

    public static final InfoParserCreativeTab instance = new InfoParserCreativeTab();

    @Override
    public String id() {
        return "creativetab";
    }

    @Override
    public String name() {
        return "Creative Tab";
    }

    @Override
    public String text(@NotNull CreativeTabs entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;
        CreativeTabs tab = info.getStack().getItem().getCreativeTab();
        if (tab != null) {
            instance.add(info.getMessages(), tab, info.isPrettyNbt());
        }
    }
}
