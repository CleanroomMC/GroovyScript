package com.cleanroommc.groovyscript.sandbox;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.launchwrapper.Launch;

import java.util.Map;

public class CachedClassLoader extends ClassLoader {

    private final Map<String, Class<?>> cache = new Object2ObjectOpenHashMap<>();

    public CachedClassLoader() {
        super(Launch.classLoader);
    }

    public Class<?> defineClass(String name, byte[] bytes) {
        Class<?> clz = super.defineClass(name, bytes, 0, bytes.length);
        resolveClass(clz);
        this.cache.put(clz.getName(), clz);
        return clz;
    }

    public void clearCache() {
        this.cache.clear();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clz = tryLoadClass(name);
        if (clz != null) return clz;
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clz = tryLoadClass(name);
        if (clz != null) return clz;
        return super.findClass(name);
    }

    public Class<?> tryLoadClass(String name) {
        Class<?> clz = this.cache.get(name);
        if (clz != null) return clz;
        try {
            return Launch.classLoader.findClass(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
