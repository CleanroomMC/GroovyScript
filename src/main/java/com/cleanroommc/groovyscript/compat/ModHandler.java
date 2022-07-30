package com.cleanroommc.groovyscript.compat;

import com.cleanroommc.groovyscript.api.IGroovyPropertyGetter;
import com.cleanroommc.groovyscript.compat.mekanism.Mekanism;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ModHandler implements IGroovyPropertyGetter {

    public static final ModHandler INSTANCE = new ModHandler();

    private final Map<String, Object> properties = new Object2ObjectOpenHashMap<>();

    private ModHandler() {
    }

    public void registerMod(String mod, Object modClass) {
        registerMod(mod, modClass, Loader.isModLoaded(mod));
    }

    public void registerMod(String mod, Object modClass, boolean condition) {
        if (condition) {
            properties.put(mod, modClass);
        }
    }

    @Override
    public @Nullable Object getProperty(String name) {
        return properties.get(name);
    }

    public void initDefaults() {
        registerMod("mekanism", new Mekanism());
    }
}
