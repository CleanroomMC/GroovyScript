package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GroovyLogImpl implements GroovyLog {

    public static final GroovyLogImpl LOG = new GroovyLogImpl();

    private static final Logger logger = LogManager.getLogger("GroovyLog");
    private final Path logFilePath;
    private PrintWriter printWriter;
    private final DateFormat timeFormat = new SimpleDateFormat("[HH:mm:ss]");
    private List<String> errors = new ArrayList<>();

    private GroovyLogImpl() {
        File minecraftHome = (File) FMLInjectionData.data()[6];
        File logFile = new File(minecraftHome, "logs" + File.separator + getLogFileName());
        this.logFilePath = logFile.toPath();
        this.printWriter = setupLog(logFile);
    }

    public void cleanLog() {
        this.printWriter = setupLog(this.logFilePath.toFile());
    }

    private PrintWriter setupLog(File logFile) {
        PrintWriter writer;
        try {
            // delete file if it exists
            if (logFile.exists() && !logFile.isDirectory()) {
                Files.delete(logFilePath);
            }
            // create file
            Files.createFile(logFilePath);
            // create writer which automatically flushes on write
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(logFile.toPath()))), true);
        } catch (IOException e) {
            GroovyScript.LOGGER.throwing(e);
            writer = new PrintWriter(System.out);
        }
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        writer.println("============  GroovyLog  ====  " + dateFormat.format(new Date()) + "  ============");
        writer.println("GroovyScript version: " + GroovyScript.VERSION);
        return writer;
    }

    private static String getLogFileName() {
        return FMLLaunchHandler.side().isServer() ? "groovy_server.log" : "groovy.log";
    }

    @GroovyBlacklist
    public List<String> collectErrors() {
        List<String> errors = this.errors;
        this.errors = new ArrayList<>();
        return errors;
    }

    @Override
    public boolean isDebug() {
        return GroovyScript.getRunConfig().isDebug();
    }

    @Override
    public PrintWriter getWriter() {
        return printWriter;
    }

    @Override
    public Path getLogFilePath() {
        return logFilePath;
    }

    @Override
    public void log(GroovyLog.Msg msg) {
        if (msg.getLevel() == Level.OFF) return;
        if (msg.getLevel() == Level.DEBUG && !isDebug()) return;
        String level = msg.getLevel().name();
        String main = msg.getMainMsg();
        List<String> messages = msg.getSubMessages();
        if (msg.getLevel() == Level.ERROR || msg.getLevel() == Level.FATAL) {
            this.errors.add(main);
        }
        if (messages.isEmpty()) {
            // has no sub messages -> log in a single line
            writeLogLine(formatLine(level, main));
            if (msg.shouldLogToMc()) {
                logger.log(msg.getLevel(), main);
            }
        } else if (messages.size() == 1 && main.length() + messages.get(0).length() < 100) {
            // has one sub message and the main message and the sub message have less than 100 characters ->
            // log in a single line
            writeLogLine(formatLine(level, main + ": - " + messages.get(0)));
            if (msg.shouldLogToMc()) {
                logger.log(msg.getLevel(), main + ": - " + messages.get(0));
            }
        } else {
            // has multiple log lines or the main message and the first sub message are to long ->
            // log each sub message in a single line, starting with the main message
            writeLogLine(formatLine(level, main + ": "));
            for (String message : messages) {
                writeLogLine(formatLine(level, " - " + message));
            }
            if (msg.shouldLogToMc()) {
                logger.log(msg.getLevel(), main + ": ");
                for (String message : messages) {
                    logger.log(msg.getLevel(), " - " + message);
                }
            }
        }
        Throwable throwable = msg.getException();
        if (throwable != null) {
            exception(throwable);
        }
    }

    public Path getPath() {
        return logFilePath;
    }

    /**
     * Logs an info msg to the groovy log AND Minecraft's log
     *
     * @param msg  message
     * @param args arguments
     */
    @Override
    public void infoMC(String msg, Object... args) {
        info(msg, args);
        logger.info(msg, args);
    }

    /**
     * Logs a info msg to the groovy log
     *
     * @param msg  message
     * @param args arguments
     */
    @Override
    public void info(String msg, Object... args) {
        writeLogLine(formatLine("INFO", GroovyLog.format(msg, args)));
    }

    /**
     * Logs a debug msg to the groovy log AND Minecraft's log
     *
     * @param msg  message
     * @param args arguments
     */
    @Override
    public void debugMC(String msg, Object... args) {
        if (isDebug()) {
            debug(msg, args);
            logger.info(msg, args);
        }
    }

    /**
     * Logs a debug msg to the groovy log
     *
     * @param msg  message
     * @param args arguments
     */
    @Override
    public void debug(String msg, Object... args) {
        if (isDebug()) {
            writeLogLine(formatLine("DEBUG", GroovyLog.format(msg, args)));
        }
    }

    /**
     * Logs a warn msg to the groovy log AND Minecraft's log
     *
     * @param msg  message
     * @param args arguments
     */
    @Override
    public void warnMC(String msg, Object... args) {
        warn(msg, args);
        logger.warn(msg, args);
    }

    @Override
    public void fatal(String msg, Object... args) {
        msg = GroovyLog.format(msg, args);
        this.errors.add(msg);
        writeLogLine(formatLine("FATAL", msg));
    }

    @Override
    public void fatalMC(String msg, Object... args) {
        fatal(msg, args);
        logger.fatal(msg, args);
    }

    /**
     * Logs a warn msg to the groovy log
     *
     * @param msg  message
     * @param args arguments
     */
    @Override
    public void warn(String msg, Object... args) {
        writeLogLine(formatLine("WARN", GroovyLog.format(msg, args)));
    }

    /**
     * Logs a error msg to the groovy log AND Minecraft's log
     *
     * @param msg  message
     * @param args arguments
     */
    @Override
    public void error(String msg, Object... args) {
        msg = GroovyLog.format(msg, args);
        this.errors.add(msg);
        writeLogLine(formatLine("ERROR", msg));
    }

    @Override
    public void errorMC(String msg, Object... args) {
        error(msg, args);
        logger.error(msg, args);
    }

    @Override
    public void exception(Throwable throwable) {
        exception("An exception occurred while running scripts.", throwable);
    }

    /**
     * Logs an exception to the groovy log AND Minecraft's log. It does NOT throw the exception! The stacktrace for the groovy log will be
     * stripped for better readability.
     *
     * @param throwable exception
     */
    @Override
    public void exception(String msg, Throwable throwable) {
        String throwableMsg = throwable.toString();
        this.errors.add(throwableMsg);
        msg += " Look at latest.log for a full stacktrace:";
        writeLogLine(formatLine("ERROR", msg));
        writeLogLine("\t" + throwableMsg);
        Pattern pattern = Pattern.compile("(\\w*).run\\(\\1(\\.\\w*):(\\d*)\\)");
        for (String line : prepareStackTrace(throwable.getStackTrace())) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches() && RunConfig.isGroovyFile(matcher.group(2))) {
                writeLogLine("\t\tin " + matcher.group(1) + matcher.group(2) + " in line " + matcher.group(3));
            } else {
                writeLogLine("\t\tat " + line);
            }
        }
        GroovyScript.LOGGER.error(msg);
        GroovyScript.LOGGER.throwing(throwable);
    }

    private List<String> prepareStackTrace(StackTraceElement[] stackTrace) {
        List<String> lines = Arrays.stream(stackTrace).map(StackTraceElement::toString).collect(Collectors.toList());
        String engineCause = "com.cleanroommc.groovyscript.sandbox.GroovyScriptSandbox.loadScripts";
        int i = 0;
        for (String line : lines) {
            i++;
            if (line.startsWith(engineCause)) {
                break;
            }
        }
        if (i > 0 && i <= lines.size()) {
            lines = lines.subList(0, i);
        }
        lines.removeIf(s -> s.startsWith("org.codehaus.groovy.vmplugin") || s.startsWith("org.codehaus.groovy.runtime"));
        return lines;
    }

    private String formatLine(String level, String msg) {
        return timeFormat.format(new Date()) + (FMLCommonHandler.instance().getEffectiveSide().isClient() ? " [CLIENT/" : " [SERVER/") + level + "]" + " [" + getSource() + "]: " + msg;
    }

    private String getSource() {
        String source = GroovyScript.isSandboxLoaded() ? GroovyScript.getSandbox().getCurrentScript() : null;
        if (source == null) {
            ModContainer mod = Loader.instance().activeModContainer();
            return mod != null ? mod.getModId() : GroovyScript.ID;
        }
        if (isDebug()) { // Find line number when debug is on
            for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                if (element.getFileName() == null) {
                    continue;
                }
                if (RunConfig.isGroovyFile(element.getFileName())) {
                    source += (":" + element.getLineNumber());
                    break;
                }
            }
        }
        return source;
    }

    private void writeLogLine(String line) {
        this.printWriter.println(line);
    }

    public static GroovyLog.Msg msg(String msg, Object... data) {
        return new MsgImpl(msg, data);
    }

    public static class MsgImpl implements GroovyLog.Msg {

        private final String mainMsg;
        private final List<String> messages = new ArrayList<>();
        private Level level = Level.INFO;
        private boolean logToMcLog;
        private @Nullable Throwable throwable;

        private MsgImpl(String msg, Object... data) {
            this.mainMsg = GroovyLog.format(msg, data);
        }

        @Flow(source = "this.level")
        public boolean isValid() {
            return level != null;
        }

        @Override
        public Msg add(String msg, Object... data) {
            this.messages.add(GroovyLog.format(msg, data));
            return this;
        }

        @Override
        public Msg add(boolean condition, String msg, Object... args) {
            if (condition) {
                if (args != null && args.length > 0) {
                    for (int i = 0; i < args.length; i++) {
                        if (args[i] instanceof Supplier<?>s) {
                            args[i] = s.get();
                        }
                    }
                }
                return add(msg, args);
            }
            return this;
        }

        @Override
        public Msg add(boolean condition, Supplier<String> msg) {
            if (condition) {
                return add(msg.get());
            }
            return this;
        }

        @Override
        public Msg add(boolean condition, Consumer<Msg> msgBuilder) {
            if (condition) {
                msgBuilder.accept(this);
            }
            return this;
        }

        @Override
        public Msg exception(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        private MsgImpl level(Level level) {
            this.level = level;
            return this;
        }

        @Override
        public Msg info() {
            return level(Level.INFO);
        }

        @Override
        public Msg debug() {
            return level(Level.DEBUG);
        }

        @Override
        public Msg warn() {
            return level(Level.WARN);
        }

        @Override
        public Msg fatal() {
            return level(Level.FATAL);
        }

        @Override
        public Msg error() {
            return level(Level.ERROR);
        }

        @Override
        public Msg logToMc(boolean logToMC) {
            this.logToMcLog = logToMC;
            return this;
        }

        @Override
        public @NotNull String getMainMsg() {
            return mainMsg;
        }

        @Override
        public @NotNull List<String> getSubMessages() {
            return messages;
        }

        @Override
        public @Nullable Throwable getException() {
            return throwable;
        }

        @Override
        public Level getLevel() {
            return level;
        }

        @Override
        public boolean shouldLogToMc() {
            return logToMcLog;
        }

        public boolean hasMessages() {
            return !this.messages.isEmpty();
        }
    }
}
