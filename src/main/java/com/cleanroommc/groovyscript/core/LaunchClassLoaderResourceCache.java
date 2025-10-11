package com.cleanroommc.groovyscript.core;

import com.google.common.collect.ForwardingMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Stolen from ZenUtils.
 * <a href="https://github.com/friendlyhj/ZenUtils/blob/master/src/main/java/youyihj/zenutils/impl/core/LaunchClassLoaderResourceCache.java">Github Link</a>
 *
 * @author youyihj
 */
/*
    Let's explain why this class exists.
    We need to inject bytecodes compiled by zs into the LaunchClassLoader's resource cache to load them by LCL.
    Basically, the resource cache is a ConcurrentHashMap. Its contents will not be lost and will be preserved for a long time.
    It is fine for us, but introduces a lot of RAM waste.
    So some mods change the cache to guava's Cache to save RAM but lost our bytecodes.
    VintageFix sets the cache that contents will be expired after 1 minute, which produces issue for us.
    Loliasm sets the cache that contents are weak references. But our bytecodes are strongly referenced in ZenModule and never garbage collected.
    But the phase of them two setting up the cache is different. VintageFix sets the cache very early (FMLLoadingPlugin), while Loliasm sets the cache "very" late (FMLLoadCompleteEvent).
    VintageFix's optimization is still important when loading textures, sounds, etc. We can not break both of them.

    This class is a wrapper for VintageFix's cache, concatenated with our bytecodes. Loliasm will set its cache later, but like talking before, our bytecodes won't be lost in its cache.
*/
public class LaunchClassLoaderResourceCache extends ForwardingMap<String, byte[]> {

    private final Map<String, byte[]> delegate;

    // immutable to thread-safe // groovyscript: we need to modify the map when loading late mixins
    private final Map<String, byte[]> injected;

    public LaunchClassLoaderResourceCache(Map<String, byte[]> delegate, Map<String, byte[]> injected) {
        this.delegate = delegate;
        this.injected = injected;
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return super.containsKey(key) || injected.containsKey(key);
    }

    @Override
    public byte[] get(@Nullable Object key) {
        byte[] bytes = super.get(key);
        if (bytes == null) {
            bytes = injected.get(key);
        }
        return bytes;
    }

    @Override
    protected Map<String, byte[]> delegate() {
        return delegate;
    }
}
