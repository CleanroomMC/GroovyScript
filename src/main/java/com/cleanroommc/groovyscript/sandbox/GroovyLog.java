package com.cleanroommc.groovyscript.sandbox;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public void info(String msg, Object... args) {
        String line = String.format(msg, args);
        writeLogLine(formatLine("INFO ", line));
    }

    public void debug(String msg, Object... args) {
        if (debug) {
            String line = String.format(msg, args);
            writeLogLine(formatLine("INFO ", line));
        }
    }

    public void warn(String msg, Object... args) {
        String line = String.format(msg, args);
        writeLogLine(formatLine("WARN ", line));
    }

    public void error(String msg, Object... args) {
        String line = String.format(msg, args);
        logger.error(line);
        writeLogLine(formatLine("ERROR", line));
    }

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
        return timeFormat.format(new Date()) + " " + (FMLCommonHandler.instance().getEffectiveSide().isClient() ? "[CLIENT]" : "[SERVER]") + " [" + level + "] " + msg;
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
