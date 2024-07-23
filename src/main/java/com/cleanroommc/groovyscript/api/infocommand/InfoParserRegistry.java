package com.cleanroommc.groovyscript.api.infocommand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InfoParserRegistry {

    private static final List<InfoParser> INFO_PARSERS = new ArrayList<>();

    public static void addInfoParser(InfoParser command) {
        INFO_PARSERS.add(command);
    }

    public static List<InfoParser> getInfoParsers() {
        return INFO_PARSERS.stream().sorted(Comparator.comparing(InfoParser::priority)).collect(Collectors.toList());
    }

    public static List<String> getIds() {
        return INFO_PARSERS.stream().sorted(Comparator.comparing(InfoParser::priority)).map(InfoParser::id).collect(Collectors.toList());
    }

}
