package com.cleanroommc.groovyscript.sandbox.engine;

import com.cleanroommc.groovyscript.sandbox.FileUtil;
import com.cleanroommc.groovyscript.sandbox.SandboxData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@ApiStatus.Internal
public class CompiledClass {

    public static final String CLASS_SUFFIX = ".clz";

    final String path;
    final String name;
    byte[] data;
    Class<?> clazz;
    boolean mixin;
    boolean earlyMixin;

    public CompiledClass(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public CompiledClass(String path, String name, boolean mixin) {
        this(path, name);
        this.mixin = mixin;
    }

    public void onCompile(byte @NotNull [] data, @Nullable Class<?> clazz, String basePath) {
        this.data = data;
        this.clazz = clazz;
        if (clazz != null && !this.name.equals(clazz.getName())) {
            throw new IllegalArgumentException("Expected class name to be " + this.name + ", but was " + clazz.getName());
        }
        if (!ScriptEngine.ENABLE_CACHE) return;
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
        if (!ScriptEngine.ENABLE_CACHE) return false;
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
        removeClass();
        this.data = null;
    }

    protected void removeClass() {
        if (this.clazz != null) {
            InvokerHelper.removeClass(this.clazz);
            this.clazz = null;
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

    public Class<?> getScriptClass() {
        return clazz;
    }

    public boolean isMixin() {
        return mixin;
    }

    public boolean hasData() {
        return data != null;
    }

    public byte[] getData() {
        return data;
    }

    public boolean hasClass() {
        return isMixin() || this.clazz != null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).toString();
    }
}
