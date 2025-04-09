package com.cleanroommc.groovyscript.sandbox.engine;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.cleanroommc.groovyscript.sandbox.Preprocessor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class CompiledScript extends CompiledClass {

    public static String classNameFromPath(String path) {
        int i = path.lastIndexOf('.');
        path = path.substring(0, i);
        return path.replace('/', '.');
    }

    final List<CompiledClass> innerClasses = new ArrayList<>();
    long lastEdited;
    List<String> preprocessors;
    private boolean preprocessorCheckFailed;
    private boolean requiresReload;

    public CompiledScript(String path, long lastEdited) {
        this(path, classNameFromPath(path), lastEdited);
    }

    public CompiledScript(String path, String name, long lastEdited) {
        super(path, name);
        this.lastEdited = lastEdited;
    }

    public boolean isClosure() {
        return lastEdited < 0;
    }

    @Override
    public void onCompile(byte @NotNull [] data, @Nullable Class<?> clazz, String basePath) {
        super.onCompile(data, clazz, basePath);
        setRequiresReload(this.data == null);
    }

    public CompiledClass findInnerClass(String clazz) {
        for (CompiledClass comp : this.innerClasses) {
            if (comp.name.equals(clazz)) {
                return comp;
            }
        }
        CompiledClass comp = new CompiledClass(this.path, clazz);
        this.innerClasses.add(comp);
        return comp;
    }

    public @NotNull JsonObject toJson() {
        JsonObject jsonEntry = new JsonObject();
        jsonEntry.addProperty("name", this.name);
        jsonEntry.addProperty("path", this.path);
        jsonEntry.addProperty("lm", this.lastEdited);
        if (!this.innerClasses.isEmpty()) {
            JsonArray inner = new JsonArray();
            for (CompiledClass comp : this.innerClasses) {
                inner.add(comp.name);
            }
            jsonEntry.add("inner", inner);
        }
        if (this.preprocessors != null && !this.preprocessors.isEmpty()) {
            JsonArray jsonPp = new JsonArray();
            for (String pp : this.preprocessors) {
                jsonPp.add(pp);
            }
            jsonEntry.add("preprocessors", jsonPp);
        }
        return jsonEntry;
    }

    public static CompiledScript fromJson(JsonObject json, String scriptRoot, String cacheRoot) {
        CompiledScript cs = new CompiledScript(
                json.get("path").getAsString(),
                JsonHelper.getString(json, null, "name"),
                json.get("lm").getAsLong());
        if (new File(scriptRoot, cs.path).exists()) {
            if (json.has("inner")) {
                for (JsonElement element : json.getAsJsonArray("inner")) {
                    cs.innerClasses.add(new CompiledClass(cs.path, element.getAsString()));
                }
            }
            if (json.has("preprocessors")) {
                cs.preprocessors = new ArrayList<>();
                for (JsonElement element : json.getAsJsonArray("preprocessors")) {
                    cs.preprocessors.add(element.getAsString());
                }
            }
            return cs;
        }
        // script file no longer exists -> delete cache
        cs.deleteCache(cacheRoot);
        return null;
    }

    @Override
    public void deleteCache(String cachePath) {
        super.deleteCache(cachePath);
        for (CompiledClass cc : this.innerClasses) {
            cc.deleteCache(cachePath);
        }
    }

    @Override
    protected void removeClass() {
        super.removeClass();
        for (CompiledClass cc : this.innerClasses) {
            cc.removeClass();
        }
    }

    public boolean checkRequiresReload(File file, long lastModified, String rootPath) {
        // the file needs to be reparsed if:
        // - caching is disabled
        // - it wasn't parsed before
        // - there is no class (mixins don't have classes)
        // - the file was modified since the last parsing
        setRequiresReload(!ScriptEngine.ENABLE_CACHE || !readData(rootPath) || isMissingAnyClass() || lastModified > this.lastEdited);
        if (requiresReload()) {
            removeClass();
            // parse preprocessors if file was modified
            if (this.preprocessors == null || lastModified > this.lastEdited) {
                this.preprocessors = Preprocessor.parsePreprocessors(file);
            }
            this.lastEdited = lastModified;
        }
        return requiresReload();
    }

    public boolean isMissingAnyClass() {
        if (!hasClass()) return true;
        for (CompiledClass cc : this.innerClasses) {
            if (!cc.hasClass()) return true;
        }
        return false;
    }

    public boolean checkPreprocessorsFailed(File basePath) {
        setPreprocessorCheckFailed(this.preprocessors != null && !this.preprocessors.isEmpty() && !Preprocessor.validatePreprocessor(new File(basePath, this.path), this.preprocessors));
        return preprocessorCheckFailed();
    }

    public boolean requiresReload() {
        return this.requiresReload;
    }

    public boolean preprocessorCheckFailed() {
        return this.preprocessorCheckFailed;
    }

    protected void setRequiresReload(boolean requiresReload) {
        this.requiresReload = requiresReload;
    }

    protected void setPreprocessorCheckFailed(boolean preprocessorCheckFailed) {
        this.preprocessorCheckFailed = preprocessorCheckFailed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name)
                .append("path", path)
                .append("innerClasses", innerClasses)
                .append("lastEdited", lastEdited)
                .toString();
    }
}
