package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InfoParserOreDict extends GenericInfoParser<String> {

    public static final InfoParserOreDict instance = new InfoParserOreDict();

    @Override
    public String id() {
        return "oredict";
    }

    @Override
    public String name() {
        return "Ore Dictionary";
    }

    @Override
    public String plural() {
        return "Ore Dictionaries";
    }

    @Override
    public String text(@NotNull String entry, boolean colored, boolean prettyNbt) {
        return colored ? GroovyScriptCodeConverter.asGroovyCode(entry, true) : entry;
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;
        List<String> ores = Arrays.stream(OreDictionary.getOreIDs(info.getStack())).mapToObj(OreDictionary::getOreName).collect(Collectors.toList());
        instance.add(info.getMessages(), ores, info.isPrettyNbt());
    }
}
