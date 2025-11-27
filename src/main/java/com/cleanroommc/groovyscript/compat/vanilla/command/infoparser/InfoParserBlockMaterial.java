package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraft.block.material.Material;
import org.jetbrains.annotations.NotNull;

public class InfoParserBlockMaterial extends GenericInfoParser<Material> {

    public static final InfoParserBlockMaterial instance = new InfoParserBlockMaterial();

    @Override
    public String id() {
        return "blockmaterial";
    }

    @Override
    public String name() {
        return "Block Material";
    }

    @Override
    public String text(@NotNull Material entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getBlockState() == null) return;
        instance.add(info.getMessages(), info.getBlockState().getMaterial(), info.isPrettyNbt());
    }
}
