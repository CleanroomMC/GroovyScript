package com.cleanroommc.groovyscript.api;

/**
 * A bracket handler returns a object based on its input arguments.
 * A bracket handler can be called from groovy lik this:
 * <p>
 *    {@code bracket_handler_name(args)}
 * </p>
 * In the first way there is always only one argument which is a String.
 * In the second method the argument size is at least, but not limited to one.
 * The first argument is always a string. The other can be anything.
 */
public interface IBracketHandler<T> {

    /**
     * Parses a object based on input arguments
     *
     * @param args arguments. length >= 1 && args[0] instanceof String
     * @return a parsed Object
     */
    default T parse(String mainArg, Object[] args) {
        return parse(mainArg);
    }

    /**
     * Parses a object based on input arguments
     *
     * @param arg argument
     * @return a parsed Object
     */
    T parse(String arg);
}
