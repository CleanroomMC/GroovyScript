package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

class CompiledClass {

    public static final String CLASS_SUFFIX = ".clz";

    final String path;
    String name;
    byte[] data;
    Class<?> clazz;

    public CompiledClass(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public void onCompile(byte[] data, Class<?> clazz, String basePath) {
        this.data = data;
        onCompile(clazz, basePath);
    }

    public void onCompile(Class<?> clazz, String basePath) {
        this.clazz = clazz;
        this.name = clazz.getName();
        if (this.data == null) {
            GroovyLog.get().errorMC("The class doesnt seem to be compiled yet. (" + name + ")");
            return;
        }
        if (!GroovyScriptSandbox.WRITE_CACHE) return;
        try {
            File file = getDataFile(basePath);
            file.getParentFile().mkdirs();
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(this.data);
                stream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean readData(String basePath) {
        if (this.data != null) return true;
        File file = getDataFile(basePath);
        if (!file.exists()) return false;
        try {
            this.data = Files.readAllBytes(file.toPath());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void deleteCache(String cachePath) {
        try {
            Files.deleteIfExists(getDataFile(cachePath).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File getDataFile(String basePath) {
        return FileUtil.makeFile(basePath, FileUtil.getParent(this.path), this.name + CLASS_SUFFIX);
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .toString();
    }
}
