package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraft.world.DimensionType;
import org.jetbrains.annotations.NotNull;

public class InfoParserDimension extends GenericInfoParser<DimensionType> {

    public static final InfoParserDimension instance = new InfoParserDimension();

    @Override
    public String id() {
        return "dimension";
    }

    @Override
    public String name() {
        return "Dimension";
    }

    @Override
    public String text(@NotNull DimensionType entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getEntity() == null) return;
        instance.add(info.getMessages(), info.getEntity().world.provider.getDimensionType(), info.isPrettyNbt());
    }

}
