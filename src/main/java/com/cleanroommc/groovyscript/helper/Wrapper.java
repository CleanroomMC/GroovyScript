package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.FileUtil;

import java.io.File;
import java.net.URI;

public class Wrapper {

    public static void validatePath(String path) {
        if (!path.startsWith(GroovyScript.getMinecraftHome().getPath())) {
            throw new SecurityException("Only files in minecraft home and sub directories can be accessed from scripts! Tried to access " + path);
        }
    }

    public static void logFileCtorWarning() {
        GroovyLog.get().warn("Tried to use `new File(...)` constructor. Use `file(...)` instead!");
    }

    public static File wrappedFileCtor(String path) {
        logFileCtorWarning();
        return GroovyHelper.file(path);
    }

    public static File wrappedFileCtor(String parent, String child) {
        logFileCtorWarning();
        return GroovyHelper.file(parent, child);
    }

    public static File wrappedFileCtor(File parent, String child) {
        logFileCtorWarning();
        File file = new File(parent, FileUtil.sanitizePath(child));
        validatePath(file.getPath());
        return file;
    }

    public static File wrappedFileCtor(URI uri) {
        File file = new File(uri);
        validatePath(file.getPath());
        return file;
    }
}
