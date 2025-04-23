package com.cleanroommc.groovyscript.api.infocommand;

import com.cleanroommc.groovyscript.helper.StyleConstant;
import net.minecraft.util.text.Style;

public interface InfoParser {

    /**
     * The style for any parser header - bold and light purple.
     *
     * @deprecated use {@link com.cleanroommc.groovyscript.helper.StyleConstant#getTitleStyle()}
     */
    @Deprecated
    Style headerStyle = StyleConstant.getTitleStyle();

    /**
     * Priority of the parser for determining the order they are logged in chat,
     * with lowest being first and highest being last.
     * The default for {@link com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser GenericInfoParser} 100,
     * and {@link com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.InfoParserItem#priority() InfoParserItem.priority()} is set to 1.
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
     * Checks if the parser is enabled and parses it if it is.
     * Regardless of if {@code enabled} is {@code true}, the parser should first check
     * to see if {@link #id()} is disabled via a {@code -} prefix and should run if it was disabled.
     * Then, it should check if {@link #id()} was enabled, and run if it was or if the method is enabled by default.
     * Typically, {@code enabled} will be {@code true} if there are no arguments, the arguments included "{@code all}",
     * or all arguments started with {@code -} to negate a specific parser being enabled.
     *
     * @param info    the info package, containing all the information of the command
     * @param enabled if this should always be parsed
     * @see InfoParserPackage
     */
    void parse(InfoParserPackage info, boolean enabled);
}
