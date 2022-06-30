package com.cleanroommc.groovyscript.sandbox;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
        writeLogLine(formatLine("ERROR", throwable.toString()));
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
            if (line.startsWith(engineCause)) {
                break;
            }
            i++;
        }
        if (i < lines.size()) {
            lines = lines.subList(0, i + 1);
        }
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
}
