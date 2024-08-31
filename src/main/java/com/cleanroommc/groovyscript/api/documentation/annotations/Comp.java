package com.cleanroommc.groovyscript.api.documentation.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.EnumSet;

/**
 * Used by {@link Property Properties} to determine what are valid values for the {@link Property}.
 *
 * @see Property#comp()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ /* No targets allowed */})
public @interface Comp {

    /**
     * @return the method of comparison
     * @deprecated use {@link #types()} instead
     */
    @Deprecated
    Type type() default Type.EQ;

    /**
     * @return the value that {@link #type()} compares with
     * @deprecated use {@link #lt()}, {@link #lte()}, {@link #gte()}, {@link #gt()}, {@link #eq()}, {@link #not()}, or {@link #unique()} instead
     */
    @Deprecated
    String value() default "";

    /**
     * @return an array of types to compare with
     */
    Type[] types() default {};

    /**
     * @return if {@link #types()} contains {@link Comp.Type#LT}, the value used to represent less than
     */
    int lt() default 0;

    /**
     * @return if {@link #types()} contains {@link Comp.Type#LTE}, the value used to represent less than or equal to
     */
    int lte() default 0;

    /**
     * @return if {@link #types()} contains {@link Comp.Type#GTE}, the value used to represent greater than or equal to
     */
    int gte() default 0;

    /**
     * @return if {@link #types()} contains {@link Comp.Type#GT}, the value used to represent greater than
     */
    int gt() default 0;

    /**
     * @return if {@link #types()} contains {@link Comp.Type#EQ}, the value used to represent equal to
     */
    int eq() default 0;

    /**
     * @return if {@link #types()} contains {@link Comp.Type#NOT}, the value used to represent not equal to
     */
    String not() default "";

    /**
     * @return if {@link #types()} contains {@link Comp.Type#UNI}, the lang key used to represent the unique description
     */
    String unique() default "";

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
     *  <tr>
     *   <th>UNI</th>
     *   <th>!?</th>
     *   <th>groovyscript.wiki.unique</th>
     *  </tr>
     * </table>
     */
    enum Type {
        GT(">", "groovyscript.wiki.greater_than"),
        GTE(">=", "groovyscript.wiki.greater_than_or_equal_to"),
        EQ("==", "groovyscript.wiki.equal_to"),
        LTE("<=", "groovyscript.wiki.less_than_or_equal_to"),
        LT("<", "groovyscript.wiki.less_than"),
        NOT("!=", "groovyscript.wiki.not"),
        UNI("!?", "groovyscript.wiki.unique");

        private final String symbol;
        private final String key;

        Type(String symbol, String key) {
            this.symbol = symbol;
            this.key = key;
        }

        /**
         * Creates an EnumSet based on the given {@link Comp}.
         * If {@link Comp#types()} has any elements, the types contained will be used.
         * Otherwise, any non-default values for the individual elements will be used.
         *
         * @param comp the {@link Comp} instance to be parsed
         * @return a set containing the types that are used in the Comp
         */
        public static EnumSet<Type> getUsedTypes(Comp comp) {
            if (comp.types().length > 0) return EnumSet.of(comp.types()[0], comp.types());
            var usedTypes = EnumSet.noneOf(Comp.Type.class);
            if (comp.gt() != 0) usedTypes.add(Comp.Type.GT);
            if (comp.gte() != 0) usedTypes.add(Comp.Type.GTE);
            if (comp.eq() != 0) usedTypes.add(Comp.Type.EQ);
            if (comp.lte() != 0) usedTypes.add(Comp.Type.LTE);
            if (comp.lt() != 0) usedTypes.add(Comp.Type.LT);
            if (!comp.not().isEmpty()) usedTypes.add(Comp.Type.NOT);
            if (!comp.unique().isEmpty()) usedTypes.add(Comp.Type.UNI);
            return usedTypes;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getKey() {
            return key;
        }

    }

}
