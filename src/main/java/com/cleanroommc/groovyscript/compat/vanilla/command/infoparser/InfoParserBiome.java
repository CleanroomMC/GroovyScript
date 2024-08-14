package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class InfoParserBiome extends GenericInfoParser<Biome> {

    public static final InfoParserBiome instance = new InfoParserBiome();

    @Override
    public String id() {
        return "biome";
    }

    @Override
    public String name() {
        return "Biome";
    }

    @Override
    public String text(@NotNull Biome entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getPos() == null || info.getEntity() == null) return;
        instance.add(info.getMessages(), info.getEntity().getEntityWorld().getBiome(info.getPos()), info.isPrettyNbt());
    }

}
