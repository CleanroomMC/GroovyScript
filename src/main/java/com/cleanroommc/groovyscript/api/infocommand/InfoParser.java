package com.cleanroommc.groovyscript.api.infocommand;

import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.InfoParserItem;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public interface InfoParser {

    /**
     * The style for any parser header - bold and light purple.
     */
    Style headerStyle = new Style().setColor(TextFormatting.LIGHT_PURPLE).setBold(true);

    /**
     * Priority of the parser for determining the order they are logged in chat,
     * with lowest being first and highest being last.
     * The is 100, and {@link InfoParserItem#priority()} is set to 1.
     *
     * @return the priority of the Parser
     */
    int priority();

    /**
     * The id used to determine what modes are active when running the command.
     * Displayed in-game as part of the allowed argument list for the command.
     *
     * @return id of the parser
     */
    String id();

    /**
     * Checks if the parser is enabled or is forced to be enabled, and parse if enabled.
     * If {@code force} is enabled, it should attempt to parse regardless of other considerations,
     * such as the valid modes stored in {@link InfoParserPackage#getArgs()}.
     * Typically, {@code force} will be {@code true} if there are no arguments, the arguments included "{@code all}",
     * or the arguments had a length of 1 and their only argument was "{@code pretty}".
     *
     * @param info  the info package, containing all the information of the command
     * @param force if this should always be parsed
     * @see InfoParserPackage
     */
    void parse(InfoParserPackage info, boolean force);

}
