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
     * <b>0xFF00FF</b>, <b>5.0</b>f, <b>347</b>
     */
    public static final TextFormatting NUMBER = TextFormatting.YELLOW;

    /**
     * Used for text within strings:
     * '<b>hello</b>', "<b>world</b>"
     */
    public static final TextFormatting STRING = TextFormatting.GREEN;

    /**
     * Used for method names, most commonly the object mapper name:
     * <b>call</b>(), <b>item</b>('minecraft:clay'), <b>fluid</b>('water')
     */
    public static final TextFormatting METHOD = TextFormatting.AQUA;

    /**
     * Used for creating a new object:
     * <b>new</b> Object(), <b>key</b>=value
     */
    public static final TextFormatting NEW = TextFormatting.LIGHT_PURPLE;

    /**
     * Used for class types, casts, and declarations:
     * <b>(byte)</b>, new <b>Object</b>(), 1.5<b>F</b>
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
    public static Style getTitleStyle() {
        return new Style().setColor(TextFormatting.WHITE).setBold(true);
    }

    /**
     * Used to distinguish text from surrounding text.
     * Often used for borders or for critical information.
     */
    public static Style getEmphasisStyle() {
        return new Style().setColor(TextFormatting.GOLD);
    }

    /**
     * Used for the text of a state where the other outcome could be a warning or an error,
     * but this evaluation was not, and works as expected.
     */
    public static Style getSuccessStyle() {
        return new Style().setColor(SUCCESS);
    }

    /**
     * Used for the text of warnings
     */
    public static Style getWarningStyle() {
        return new Style().setColor(WARNING);
    }

    /**
     * Used for the text of errors
     */
    public static Style getErrorStyle() {
        return new Style().setColor(ERROR);
    }
}
