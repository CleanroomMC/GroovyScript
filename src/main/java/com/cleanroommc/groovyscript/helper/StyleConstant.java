package com.cleanroommc.groovyscript.helper;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

/**
 * Constant values for formatting colors for the Minecraft chat.
 * <p>
 * Primarily used to format the object mappers into valid code fragments via
 * {@link com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter GroovyScriptCodeConverter}
 */
public class StyleConstant {

    /**
     * Used for any text that should not be a different color:
     * the default text color
     */
    public static final TextFormatting BASE = TextFormatting.GRAY;

    /**
     * Used for the digits of numbers:
     * {@code 0xFF00FF}, {@code 5.0}f
     */
    public static final TextFormatting NUMBER = TextFormatting.YELLOW;

    /**
     * Used for text within strings:
     * '{@code hello}', "{@code world}"
     */
    public static final TextFormatting STRING = TextFormatting.AQUA;

    /**
     * Used for the object mapper name:
     * {@code item}('minecraft:clay'), {@code fluid}('water')
     */
    public static final TextFormatting MAPPER = TextFormatting.DARK_GREEN;

    /**
     * Used for creating a new object:
     * {@code new} Object()
     */
    public static final TextFormatting NEW = TextFormatting.LIGHT_PURPLE;

    /**
     * Used for class types, casts, and declarations:
     * {@code (byte)}, new {@code Object}(), 1.5{@code F}
     */
    public static final TextFormatting CLASS = TextFormatting.GOLD;

    /**
     * Used for the text of a state where a different outcome could be a warning or an error,
     * but this evaluation was not and has succeeded.
     */
    public static final TextFormatting SUCCESS = TextFormatting.GREEN;

    /**
     * Used for the text of warnings
     */
    public static final TextFormatting WARNING = TextFormatting.YELLOW;

    /**
     * Used for the text of errors
     */
    public static final TextFormatting ERROR = TextFormatting.RED;

    /**
     * Used for titles to make them emphasised and keep them distinct from the surrounding text
     */
    public static final Style TITLE_STYLE = new Style().setColor(TextFormatting.WHITE).setBold(true);

    /**
     * Used for the text of a state where the other outcome could be a warning or an error,
     * but this evaluation was not, and works as expected.
     */
    public static final Style SUCCESS_STYLE = new Style().setColor(SUCCESS);

    /**
     * Used for the text of warnings
     */
    public static final Style WARNING_STYLE = new Style().setColor(WARNING);

    /**
     * Used for the text of errors
     */
    public static final Style ERROR_STYLE = new Style().setColor(ERROR);

}
