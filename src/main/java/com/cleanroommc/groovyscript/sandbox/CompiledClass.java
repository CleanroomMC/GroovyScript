package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

class CompiledClass {

    public static final String CLASS_SUFFIX = ".clz";

    final String path;
    final String name;
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
        if (!this.name.equals(clazz.getName())) throw new IllegalArgumentException();
        //this.name = clazz.getName();
        if (this.data == null) {
            GroovyLog.get().errorMC("The class doesnt seem to be compiled yet. (" + name + ")");
            return;
        }
        if (!CustomGroovyScriptEngine.ENABLE_CACHE) return;
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

    protected void ensureLoaded(GroovyClassLoader classLoader, Map<String, CompiledClass> cache, String basePath) {
        if (this.clazz == null) {
            this.clazz = classLoader.defineClass(this.name, this.data);
            cache.put(this.name, this);
        }
    }

    public boolean readData(String basePath) {
        if (this.data != null && CustomGroovyScriptEngine.ENABLE_CACHE) return true;
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
        if (this.clazz != null) {
            InvokerHelper.removeClass(this.clazz);
            this.clazz = null;
        }
        this.data = null;
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
        return new ToStringBuilder(this).append("name", name).toString();
    }
}
