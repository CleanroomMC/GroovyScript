package com.cleanroommc.groovyscript.sandbox;

import com.google.common.base.Joiner;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtil {

    private static final Joiner fileJoiner = Joiner.on(File.separator);

    public static String relativize(String rootPath, String longerThanRootPath) {
        if (File.separatorChar != '/') {
            longerThanRootPath = longerThanRootPath.replace('/', File.separatorChar);
        }
        int index = longerThanRootPath.indexOf(rootPath);
        if (index < 0) throw new IllegalArgumentException();
        return longerThanRootPath.substring(index + rootPath.length() + 1);
    }

    public static String getParent(String path) {
        int i = path.lastIndexOf(File.separatorChar);
        if (i <= 0) return StringUtils.EMPTY;
        path = path.substring(0, i);
        if (path.length() == 2 && Character.isLetter(path.charAt(0)) && path.charAt(1) == ':') return StringUtils.EMPTY;
        return path;
    }

    public static String makePath(String... pieces) {
        return fileJoiner.join(pieces);
    }

    public static File makeFile(String... pieces) {
        return new File(makePath(pieces));
    }

    public static String getMinecraftHome() {
        return Loader.instance().getConfigDir().getParent();
    }

    public static boolean mkdirs(File file) {
        if (file.isDirectory()) {
            return file.mkdirs();
        }
        return file.getParentFile().mkdirs();
    }

    public static boolean mkdirsAndFile(File file) {
        boolean b = mkdirs(file);
        if (file.isFile()) {
            try {
                Files.createFile(file.toPath());
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return b;
    }
}
