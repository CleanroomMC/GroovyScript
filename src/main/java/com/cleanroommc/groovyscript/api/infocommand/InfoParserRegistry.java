package com.cleanroommc.groovyscript.api.infocommand;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * To help gather information about items, blocks, the world, etc.
 * GroovyScript adds {@link InfoParser}s, which will provide information to the player
 * using data from an {@link InfoParserPackage}.
 * <p>
 * This is currently only used for commands related to {@link com.cleanroommc.groovyscript.command.BaseInfoCommand BaseInfoCommand}.
 *
 * @see com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.StandardInfoParserRegistry StandardInfoParserRegistry
 */
public class InfoParserRegistry {

    private static final List<InfoParser> INFO_PARSERS = new ArrayList<>();
    private static final List<String> IDS = new ArrayList<>();

    /**
     * Register the given info parser.
     */
    public static void addInfoParser(InfoParser parser) {
        INFO_PARSERS.add(parser);
        INFO_PARSERS.sort(Comparator.comparing(InfoParser::priority));
    }

    /**
     * @return all registered info parsers
     */
    public static List<InfoParser> getInfoParsers() {
        return ImmutableList.copyOf(INFO_PARSERS);
    }

    /**
     * @return the ids of all registered info parsers
     */
    public static List<String> getIds() {
        // generate the list
        if (IDS.size() != INFO_PARSERS.size()) {
            IDS.clear();
            for (var parser : INFO_PARSERS) {
                IDS.add(parser.id());
            }
        }
        return ImmutableList.copyOf(IDS);
    }
}
