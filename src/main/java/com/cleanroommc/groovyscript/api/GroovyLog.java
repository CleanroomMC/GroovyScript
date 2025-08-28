package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.sandbox.GroovyLogImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An interface for the GroovyScript logger. The log is separate to Minecraft's normal and debug log.
 * The generated log file can be found at "[Minecraft instance]/groovy.log".
 * All logging methods format its content similarly to how C does it.
 * Curly braces in the msg parameter get replaced with the given arguments.
 * <p>
 * Examples: <br>
 * <code>info("Hello {} World", "big")</code>
 * would result in
 * <code>"Hello big World"</code>
 * </p><p>
 * <code>info("The current count is {}", 42)</code>
 * would result in
 * <code>"The current count is 42"</code>
 * </p><p>
 * You can also access arguments by index: <br>
 * <code>info("Th{0}s {0}s a p{0}{1}c{1} of cak{1}", 'i', 'e')</code> <br>
 * would result in
 * <code>"This is a piece of cake"</code>
 * </p>
 */
public interface GroovyLog {

    /**
     * @return an instance of {@link GroovyLog}
     */
    static @NotNull GroovyLog get() {
        return GroovyLogImpl.LOG;
    }

    /**
     * Creates a {@link Msg} builder object for logging.
     *
     * @param msg  the main message of the message
     * @param args arguments for the main message
     * @return a message builder object
     */
    @Contract("_,_ -> new")
    static Msg msg(String msg, Object... args) {
        return GroovyLogImpl.msg(msg, args);
    }

    /**
     * Allows Groovy to use {@code log('text')} to print an info message by using Groovy's call operator overloading.
     * <p>
     * Should be avoided in Java code.
     *
     * @see #info(String, Object...)
     */
    default void call(String msg, Object... args) {
        info(msg, args);
    }

    /**
     * Allows Groovy to use {@code log('text')} to print an info message by using Groovy's call operator overloading.
     * <p>
     * Should be avoided in Java code.
     *
     * @see #info(Object)
     */
    default void call(Object obj) {
        info(obj);
    }

    /**
     * Determines whether debug messages should be ignored.
     *
     * @return true if messages on debug level should be logged
     */
    boolean isDebug();

    /**
     * Returns the writer object that handles writing to file.
     *
     * @return output file writer for this log
     */
    PrintWriter getWriter();

    /**
     * Returns the locations of this log file.
     *
     * @return the locations of this log file
     */
    Path getLogFilePath();

    /**
     * Formats and logs a {@link Msg} object to this log.
     *
     * @param msg message to log
     */
    void log(Msg msg);

    /**
     * Formats and logs to this log on INFO level
     *
     * @param msg  message to log
     * @param args message arguments
     * @see GroovyLog formatting
     */
    void info(String msg, Object... args);

    /**
     * Formats and logs a single object to this log on INFO level
     *
     * @param obj object to log
     */
    default void info(Object obj) {
        info(String.valueOf(obj), 0);
    }

    /**
     * Formats and logs to this log AND Minecraft's log on INFO level
     *
     * @param msg  message to log
     * @param args message arguments
     * @see GroovyLog formatting
     */
    void infoMC(String msg, Object... args);

    default void infoMC(Object o) {
        infoMC(String.valueOf(o), 0);
    }

    /**
     * Formats and logs to this log on DEBUG level. <br>
     * <b>Note!</b> {@link #isDebug()} must be true for this actually log something.
     *
     * @param msg  message to log
     * @param args message arguments
     * @see GroovyLog formatting
     */
    void debug(String msg, Object... args);

    default void debug(Object o) {
        debug(String.valueOf(o), 0);
    }

    /**
     * Formats and logs to this log AND Minecraft's log on DEBUG level. <br>
     * <b>Note!</b> {@link #isDebug()} must be true for this actually log something.
     *
     * @param msg  message to log
     * @param args message arguments
     * @see GroovyLog formatting
     */
    void debugMC(String msg, Object... args);

    default void debugMC(Object o) {
        debugMC(String.valueOf(o), 0);
    }

    /**
     * Formats and logs to this log on WARN level
     *
     * @param msg  message to log
     * @param args message arguments
     * @see GroovyLog formatting
     */
    void warn(String msg, Object... args);

    default void warn(Object o) {
        warn(String.valueOf(o), 0);
    }

    /**
     * Formats and logs to this log AND Minecraft's log on WARN level
     *
     * @param msg  message to log
     * @param args message arguments
     * @see GroovyLog formatting
     */
    void warnMC(String msg, Object... args);

    default void warnMC(Object o) {
        warnMC(String.valueOf(o), 0);
    }

    /**
     * Formats and logs to this log on FATAL level
     *
     * @param msg  message to log
     * @param args message arguments
     * @see GroovyLog formatting
     */
    void fatal(String msg, Object... args);

    default void fatal(Object o) {
        fatal(String.valueOf(o), 0);
    }

    /**
     * Formats and logs to this log AND Minecraft's log on FATAL level
     *
     * @param msg  message to log
     * @param args message arguments
     * @see GroovyLog formatting
     */
    void fatalMC(String msg, Object... args);

    default void fatalMC(Object o) {
        fatalMC(String.valueOf(o), 0);
    }

    /**
     * Formats and logs to this log on ERROR level
     *
     * @param msg  message to log
     * @param args message arguments
     * @see GroovyLog formatting
     */
    void error(String msg, Object... args);

    default void error(Object o) {
        error(String.valueOf(o), 0);
    }

    /**
     * Formats and logs to this log AND Minecraft's log on ERROR level
     *
     * @param msg  message to log
     * @param args message arguments
     * @see GroovyLog formatting
     */
    void errorMC(String msg, Object... args);

    default void errorMC(Object o) {
        errorMC(String.valueOf(o), 0);
    }

    /**
     * Formats and logs an exception to this log AND Minecraft's log.<br>
     * The log will be printed without formatting to Minecraft's log.
     * Unnecessary lines that clutter the log will get removed before logging to this log.<br>
     * <b>The exception will NOT be thrown!</b>
     *
     * @param throwable exception to log
     */
    void exception(Throwable throwable);

    /**
     * Formats and logs an exception to this log AND Minecraft's log with a message.<br>
     * The log will be printed without formatting to Minecraft's log.
     * Unnecessary lines that clutter the log will get removed before logging to this log.<br>
     * <b>The exception will NOT be thrown!</b>
     *
     * @param throwable exception to log
     */
    void exception(String msg, Throwable throwable);

    /**
     * Formats a {@link String} and arguments according to the defined rules.
     *
     * @param msg  message to format
     * @param args message arguments
     * @return a formatted string
     */
    static String format(String msg, Object... args) {
        return args.length == 0 ? msg : new ParameterizedMessage(msg, args).getFormattedMessage();
    }

    /**
     * A helper interface to easily make big and detailed messages.<br>
     * A message consists of a main message which always exist and any amount of sub messages.
     * The message can be posted (formatted and logged) to {@link GroovyLog} at any given time.
     */
    interface Msg {

        /**
         * Adds a sub message to this message.
         *
         * @param msg  sub message
         * @param args sub message arguments
         * @return this
         */
        Msg add(String msg, Object... args);

        /**
         * Adds a sub message to this message, but only if the given condition is true.
         * For convenience.
         *
         * @param condition sub message will only be added if this is true
         * @param msg       sub message
         * @param args      sub message arguments
         * @return this
         */
        Msg add(boolean condition, String msg, Object... args);

        /**
         * Adds a sub message to this message with exactly one parameter, but only if the given condition is true. The arg {@link Supplier}
         * is invoked if the condition is true.
         *
         * @param condition sub message will only be added if this is true
         * @param msg       sub message
         * @param arg       sub message argument
         * @return this
         */
        default Msg add(boolean condition, String msg, Supplier<Object> arg) {
            return add(condition, msg, (Object) arg);
        }

        /**
         * Adds a sub message to this message, but only if the given condition is true.
         * For convenience.
         *
         * @param condition   sub message will only be added if this is true
         * @param msgSupplier message getter
         * @return this
         */
        Msg add(boolean condition, Supplier<String> msgSupplier);

        /**
         * Consume this message builder if the given condition is true.
         * For convenience. Can be used as a builder inside the consumer to add multiple sub messages for a single condition.
         *
         * @param condition  sub message will only be added if this is true
         * @param msgBuilder message consumer
         * @return this
         */
        Msg add(boolean condition, Consumer<Msg> msgBuilder);

        /**
         * Adds an exception to the message. The exception will always be logged at last. The exception counts as a sub message. This
         * message can only have one message at a time.
         *
         * @param throwable exception.
         * @return this
         */
        Msg exception(Throwable throwable);

        /**
         * Sets the log level for this message to INFO.
         *
         * @return this
         */
        Msg info();

        /**
         * Sets the log level for this message to DEBUG. <br>
         * <b>Note!</b> {@link GroovyLog#isDebug()} must be true for this actually log something.
         *
         * @return this
         */
        Msg debug();

        /**
         * Sets the log level for this message to WARN.
         *
         * @return this
         */
        Msg warn();

        /**
         * Sets the log level for this message to FATAL.
         *
         * @return this
         */
        Msg fatal();

        /**
         * Sets the log level for this message to ERROR.
         *
         * @return this
         */
        Msg error();

        /**
         * Set if this messages should also be logged in Minecraft's log
         *
         * @param logToMC if message should be logged to Minecraft's log
         * @return this
         */
        Msg logToMc(boolean logToMC);

        default Msg logToMc() {
            return logToMc(true);
        }

        /**
         * Returns the formatted main message for this message.
         *
         * @return main message
         */
        @NotNull
        String getMainMsg();

        /**
         * Returns all formatted sub messages (excluding the exception) in an editable list.
         *
         * @return all sub messages
         */
        @NotNull
        List<String> getSubMessages();

        /**
         * Returns the currently set exception.
         *
         * @return current exception
         */
        @Nullable
        Throwable getException();

        /**
         * Returns the currently set log level
         *
         * @return log level
         */
        Level getLevel();

        /**
         * @return true if this messages should also be logged to Minecraft's log
         */
        boolean shouldLogToMc();

        /**
         * Returns if any sub messages (including the exception) exist in this message
         *
         * @return true if any sub messages exist
         */
        default boolean hasSubMessages() {
            return !getSubMessages().isEmpty() && getException() == null;
        }

        /**
         * Logs all messages of this message to {@link GroovyLog}
         */
        default void post() {
            get().log(this);
        }

        /**
         * Logs all messages of this message to {@link GroovyLog}, but only if {@link #hasSubMessages()} is true
         *
         * @return value of {@link #hasSubMessages()}
         */
        default boolean postIfNotEmpty() {
            if (hasSubMessages()) {
                post();
                return true;
            }
            return false;
        }
    }
}
