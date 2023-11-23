package com.cleanroommc.groovyscript.api.documentation.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used by {@link Property Properties} to determine what are valid values for the {@link Property}.
 * Frequently used in an array containing two entries, indicating minimum and maximum bounds.
 *
 * @see Property#valid()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ /* No targets allowed */})
public @interface Comp {

    /**
     * @return the method of comparison
     */
    Type type() default Type.EQ;

    /**
     * @return the value that {@link #type()} compares with
     */
    String value();

    /**
     * Used to determine the type of comparison. Contains a symbol representation and a localization key.
     *
     * <table>
     *  <tr>
     *   <th>name</th>
     *   <th>symbol</th>
     *   <th>key</th>
     *  </tr>
     *  <tr>
     *   <th>GT</th>
     *   <th>></th>
     *   <th>groovyscript.wiki.greater_than</th>
     *  </tr>
     *  <tr>
     *   <th>GTE</th>
     *   <th>>=</th>
     *   <th>groovyscript.wiki.greater_than_or_equal_to</th>
     *  </tr>
     *  <tr>
     *   <th>EQ</th>
     *   <th>==</th>
     *   <th>groovyscript.wiki.equal_to</th>
     *  </tr>
     *  <tr>
     *   <th>LTE</th>
     *   <th><=</th>
     *   <th>groovyscript.wiki.less_than_or_equal_to</th>
     *  </tr>
     *  <tr>
     *   <th>LT</th>
     *   <th><</th>
     *   <th>groovyscript.wiki.less_than</th>
     *  </tr>
     *  <tr>
     *   <th>NOT</th>
     *   <th>!=</th>
     *   <th>groovyscript.wiki.not</th>
     *  </tr>
     * </table>
     */
    enum Type {
        GT(">", "greater_than"),
        GTE(">=", "greater_than_or_equal_to"),
        EQ("==", "equal_to"),
        LTE("<=", "less_than_or_equal_to"),
        LT("<", "less_than"),
        NOT("!=", "not");

        private static final String baseLocalizationPath = "groovyscript.wiki.";

        private final String symbol;
        private final String key;

        Type(String symbol, String key) {
            this.symbol = symbol;
            this.key = baseLocalizationPath + key;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getKey() {
            return key;
        }

    }

}
