package com.cleanroommc.groovyscript.sandbox;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

class ScriptCache {

    private final Map<String, CompiledClass> loadedScripts = new Object2ObjectOpenHashMap<>();
    private final ScriptCache parent;
    private final boolean reloadable;

    public ScriptCache(ScriptCache parent, boolean reloadable) {
        this.parent = parent;
        this.reloadable = reloadable;
    }

    public void setScriptLoaded(CompiledClass script) {
        this.loadedScripts.put(script.getName(), script);
    }

    public void setClassLoaded(Class<?> clz) {
        CompiledClass script = this.loadedScripts.get(clz.getName());
        if (script != null) {
            setScriptLoaded(script);
        }
    }

    public Class<?> getClassForName(String className) {
        CompiledClass cs = this.loadedScripts.get(className);
        if (cs != null) {
            return cs.getClass();
        }
        if (this.parent != null) {
            return this.parent.getClassForName(className);
        }
        return null;
    }

    public void onReload() {
        if (!this.reloadable) return;
        this.loadedScripts.clear();
        if (this.parent != null) {
            this.parent.onReload();
        }
    }

    public boolean isReloadable() {
        return reloadable;
    }

    public ScriptCache getParent() {
        return parent;
    }
}
