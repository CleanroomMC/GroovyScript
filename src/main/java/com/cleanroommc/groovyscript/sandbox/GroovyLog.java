package com.cleanroommc.groovyscript.sandbox;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.groovy.sandbox.impl.Checker;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GroovyLog {

    public static final GroovyLog LOG = new GroovyLog();

    private static final Logger logger = LogManager.getLogger("GroovyLog");
    private final File logFile;
    private final Path logFilePath;
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final DateFormat timeFormat = new SimpleDateFormat("[HH:mm:ss]");

    public boolean debug = false;

    private GroovyLog() {
        logFile = new File(Loader.instance().getConfigDir().toPath().getParent().toString() + "/groovy.log");
        logFilePath = logFile.toPath();
        try {
            if (logFile.exists() && !logFile.isDirectory()) {
                Files.delete(logFilePath);
            }
            Files.createFile(logFilePath);
            writeLogLine("============  GroovyLog  ====  " + dateFormat.format(new Date()) + "  ============");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path getPath() {
        return logFilePath;
    }

    public void log(Msg msg) {
        if (msg.level == Level.OFF || !msg.hasMessages()) return;
        if (msg.level == Level.DEBUG && !debug) return;
        String level = msg.level.name();
        String main = msg.messages.get(0);
        if (msg.messages.size() == 1) {
            writeLogLine(formatLine(level, main));
            if (msg.logToMcLog) {
                logger.log(msg.level, main + " in line " + Checker.getLineNumber());
            }
        } else if (msg.messages.size() == 2) {
            writeLogLine(formatLine(level, main + ": - " + msg.messages.get(1)));
            if (msg.logToMcLog) {
                logger.log(msg.level, main + ": - " + msg.messages.get(1) + "  in line " + Checker.getLineNumber());
            }
        } else {
            writeLogLine(formatLine(level, main + ": "));
            for (int i = 1; i < msg.messages.size(); i++) {
                writeLogLine(formatLine(level, " - " + msg.messages.get(i)));
            }
            if (msg.logToMcLog) {
                logger.log(msg.level, main + " in line " + Checker.getLineNumber() + " : - ");
                for (int i = 1; i < msg.messages.size(); i++) {
                    logger.log(msg.level, " - " + msg.messages.get(i));
                }
            }
        }
    }

    /**
     * Logs a info msg to the groovy log AND Minecraft's log
     *
     * @param msg  message
     * @param args arguments
     */
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
    public void info(String msg, Object... args) {
        String line = String.format(msg, args);
        writeLogLine(formatLine("INFO ", line));
    }

    /**
     * Logs a debug msg to the groovy log AND Minecraft's log
     *
     * @param msg  message
     * @param args arguments
     */
    public void debugMC(String msg, Object... args) {
        if (debug) {
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
    public void debug(String msg, Object... args) {
        if (debug) {
            String line = String.format(msg, args);
            writeLogLine(formatLine("DEBUG", line));
        }
    }

    /**
     * Logs a warn msg to the groovy log AND Minecraft's log
     *
     * @param msg  message
     * @param args arguments
     */
    public void warnMC(String msg, Object... args) {
        warn(msg, args);
        logger.warn(msg, args);
    }

    /**
     * Logs a warn msg to the groovy log
     *
     * @param msg  message
     * @param args arguments
     */
    public void warn(String msg, Object... args) {
        String line = String.format(msg, args);
        writeLogLine(formatLine("WARN ", line));
    }

    /**
     * Logs a error msg to the groovy log AND Minecraft's log
     *
     * @param msg  message
     * @param args arguments
     */
    public void error(String msg, Object... args) {
        String line = String.format(msg, args);
        logger.error(line);
        writeLogLine(formatLine("ERROR", line));
    }

    /**
     * Logs an exception to the groovy log AND Minecraft's log.
     * It does NOT throw the exception!
     * The stacktrace for the groovy log will be stripped for better readability.
     *
     * @param throwable exception
     */
    public void exception(Throwable throwable) {
        writeLogLine(formatLine("ERROR", "An exception occurred while running scripts. Look at latest.log for a full stacktrace:"));
        writeLogLine("\t" + throwable.toString());
        throwable.printStackTrace();
        for (String line : prepareStackTrace(throwable.getStackTrace())) {
            writeLogLine("\t\tat " + line);
        }
    }

    private List<String> prepareStackTrace(StackTraceElement[] stackTrace) {
        // TODO figure out what can be stripped out since those stacktrace get pretty large
        List<String> lines = Arrays.stream(stackTrace).map(StackTraceElement::toString).collect(Collectors.toList());
        String engineCause = "groovy.util.GroovyScriptEngine.run";
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
        String groovyInternal = "org.codehaus.groovy.runtime";
        String groovySandbox = "org.kohsuke";
        lines.removeIf(s -> s.startsWith(groovyInternal) || s.startsWith(groovySandbox));
        return lines;
    }

    private String formatLine(String level, String msg) {
        return timeFormat.format(new Date()) + " [" + getSource() + "] " + (FMLCommonHandler.instance().getEffectiveSide().isClient() ? "[CLIENT]" : "[SERVER]") + " [" + level + "] " + msg;
    }

    private String getSource() {
        String source = Checker.getSource();
        if (source == Checker.UNKNOWN_SOURCE) {
            return Loader.instance().activeModContainer().getModId();
        }
        return SandboxRunner.relativizeSource(source) + ":" + Checker.getLineNumber();
    }

    private void writeLogLine(String line) {
        try {
            line += "\n";
            Files.write(logFilePath, line.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Msg msg(String msg, Object... data) {
        return new Msg(msg, data);
    }

    public static Msg msg(String msg) {
        return new Msg(msg);
    }

    public static class Msg {

        private final List<String> messages = new ArrayList<>();
        private Level level = Level.INFO;
        private boolean logToMcLog = false;

        public Msg(String msg, Object... data) {
            this.messages.add(String.format(msg, data));
        }

        public Msg(String msg) {
            this.messages.add(msg);
        }

        public Msg add(String msg, Object... data) {
            this.messages.add(String.format(msg, data));
            return this;
        }

        public Msg add(String msg) {
            this.messages.add(msg);
            return this;
        }

        public Msg add(boolean condition, Supplier<String> msg) {
            if (condition) {
                this.messages.add(msg.get());
            }
            return this;
        }

        private Msg level(Level level) {
            this.level = level;
            return this;
        }

        public Msg info() {
            return level(Level.ERROR);
        }

        public Msg debug() {
            return level(Level.DEBUG);
        }

        public Msg warn() {
            return level(Level.WARN);
        }

        public Msg error() {
            return level(Level.ERROR);
        }

        public Msg logToMc() {
            this.logToMcLog = true;
            return this;
        }

        public boolean hasMessages() {
            return !this.messages.isEmpty();
        }

        public boolean hasSubMessages() {
            return this.messages.size() > 1;
        }

        public void post() {
            LOG.log(this);
        }

        public boolean postIfNotEmpty() {
            if (hasSubMessages()) {
                LOG.log(this);
                return true;
            }
            return false;
        }
    }
}
