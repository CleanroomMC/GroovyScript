package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

class CompiledClass {

    String name;
    byte[] data;
    Class<?> clazz;

    public CompiledClass(String name) {
        this.name = name;
    }

    public void onCompile(byte[] data, Class<?> clazz, File basePath) {
        this.data = data;
        onCompile(clazz, basePath);
    }

    public void onCompile(Class<?> clazz, File basePath) {
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

    public boolean readData(File basePath) {
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

    private File getDataFile(File basePath) {
        return new File(basePath, this.name + ".clz");
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .toString();
    }
}
