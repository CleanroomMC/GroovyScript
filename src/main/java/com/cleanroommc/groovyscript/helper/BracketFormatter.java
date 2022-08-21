package com.cleanroommc.groovyscript.helper;

import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

/**
 * Used to nicely format GS code for display to the player
 */
public class BracketFormatter {

    private static final String BRACKET_START = "<";
    private static final String BRACKET_END = ">";
    private static final String SEPARATOR = ":";
    private static final String QUOTE = "'";

    @NotNull
    public static String formatGSCode(@NotNull String s) {
        int loc = s.indexOf(QUOTE, 1);
        String left = s.substring(0, loc + 1);
        String right = s.substring(loc + 1);
        return formatGSCode(left, right);
    }

    @NotNull
    public static String formatGSCode(@NotNull String s, @NotNull String extra) {
        String display = s.replace(BRACKET_START, TextFormatting.GOLD + BRACKET_START + TextFormatting.DARK_AQUA)
                .replace(BRACKET_END, TextFormatting.GOLD + BRACKET_END + TextFormatting.WHITE)
                .replaceAll(SEPARATOR, TextFormatting.GOLD + SEPARATOR + TextFormatting.DARK_AQUA)
                .replaceAll(QUOTE, TextFormatting.WHITE + QUOTE);

        if (!extra.isEmpty()) {
            display += TextFormatting.GRAY + extra;
        }

        return display;
    }
}
