package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class CompiledScript extends CompiledClass {

    final String path;
    final List<CompiledClass> innerClasses = new ArrayList<>();
    long lastEdited;

    public CompiledScript(String path, long lastEdited) {
        this(path, null, lastEdited);
    }

    public CompiledScript(String path, String name, long lastEdited) {
        super(name);
        this.path = path;
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
        CompiledClass comp = new CompiledClass(clazz);
        this.innerClasses.add(comp);
        return comp;
    }

    public void ensureLoaded(GroovyClassLoader classLoader, File basePath) {
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
