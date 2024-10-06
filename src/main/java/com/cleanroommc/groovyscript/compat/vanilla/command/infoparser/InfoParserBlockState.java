package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.NotNull;

public class InfoParserBlockState extends GenericInfoParser<IBlockState> {

    public static final InfoParserBlockState instance = new InfoParserBlockState();

    @Override
    public String id() {
        return "blockstate";
    }

    @Override
    public String name() {
        return "Block state";
    }

    @Override
    public String text(@NotNull IBlockState entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getBlockState() == null) return;
        instance.add(info.getMessages(), info.getBlockState(), info.isPrettyNbt());
    }
}
