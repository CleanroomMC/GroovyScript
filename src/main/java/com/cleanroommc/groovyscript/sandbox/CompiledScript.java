package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class CompiledScript extends CompiledClass {

    final List<CompiledClass> innerClasses = new ArrayList<>();
    long lastEdited;
    List<String> preprocessors = null;

    public CompiledScript(String path, long lastEdited) {
        this(path, null, lastEdited);
    }

    public CompiledScript(String path, String name, long lastEdited) {
        super(path, name);
        this.lastEdited = lastEdited;
    }

    public boolean isClosure() {
        return lastEdited < 0;
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

    public void ensureLoaded(GroovyClassLoader classLoader, String basePath) {
        for (CompiledClass comp : this.innerClasses) {
            if (comp.clazz == null) {
                if (comp.readData(basePath)) {
                    comp.clazz = classLoader.defineClass(comp.name, comp.data);
                } else {
                    GroovyLog.get().error("Error loading inner class {} for class {}", comp.name, this.name);
                }
            }
        }
        if (this.clazz == null) {
            this.clazz = classLoader.defineClass(this.name, this.data);
        }
    }

    @NotNull
    public JsonObject toJson() {
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
        CompiledScript cs = new CompiledScript(json.get("path").getAsString(), JsonHelper.getString(json, null, "name"), json.get("lm").getAsLong());
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

    public boolean checkPreprocessors(File basePath) {
        return this.preprocessors == null ||
               this.preprocessors.isEmpty() ||
               Preprocessor.validatePreprocessor(new File(basePath, this.path), this.preprocessors);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("path", path)
                .append("innerClasses", innerClasses)
                .append("lastEdited", lastEdited)
                .toString();
    }
}
