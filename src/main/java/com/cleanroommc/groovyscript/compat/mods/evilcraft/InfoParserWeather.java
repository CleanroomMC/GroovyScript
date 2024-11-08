package com.cleanroommc.groovyscript.compat.mods.evilcraft;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class InfoParserWeather extends GenericInfoParser<String> {

    public static final InfoParserWeather instance = new InfoParserWeather();

    @Override
    public String id() {
        return "weather";
    }

    @Override
    public String name() {
        return "Weather";
    }

    @Override
    public String text(@NotNull String entry, boolean colored, boolean prettyNbt) {
        return EvilCraft.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getEntity() == null) return;

        World world = info.getEntity().getEntityWorld();
        if (world.isThundering()) {
            instance.add(info.getMessages(), "lightning", info.isPrettyNbt());
            return;
        }
        if (world.isRaining()) {
            instance.add(info.getMessages(), "rain", info.isPrettyNbt());
            return;
        }
        instance.add(info.getMessages(), "clear", info.isPrettyNbt());
    }
}
