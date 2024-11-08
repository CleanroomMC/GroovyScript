package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.StyleConstant;
import org.jetbrains.annotations.NotNull;

public class InfoParserTranslationKey extends GenericInfoParser<String> {

    public static final InfoParserTranslationKey instance = new InfoParserTranslationKey();

    @Override
    public String id() {
        return "translation";
    }

    @Override
    public String name() {
        return "Translation key";
    }

    @Override
    public String text(@NotNull String entry, boolean colored, boolean prettyNbt) {
        return colored ? StyleConstant.STRING + entry : entry;
    }

    @Override
    public void parse(InfoParserPackage info) {
        // translation isn't used via the registry (what parse does) but is instead directly called by individual InfoParsers
    }
}
