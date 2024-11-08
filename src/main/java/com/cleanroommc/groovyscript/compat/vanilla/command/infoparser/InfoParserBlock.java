package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraft.block.Block;
import org.jetbrains.annotations.NotNull;

public class InfoParserBlock extends GenericInfoParser<Block> {

    public static final InfoParserBlock instance = new InfoParserBlock();

    @Override
    public String id() {
        return "block";
    }

    @Override
    public String name() {
        return "Block";
    }

    @Override
    public String text(@NotNull Block entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getBlock() == null) return;
        instance.add(info.getMessages(), info.getBlock(), info.isPrettyNbt());
        InfoParserTranslationKey.instance.add(info.getMessages(), info.getBlock().getTranslationKey(), info.isPrettyNbt());
    }
}
