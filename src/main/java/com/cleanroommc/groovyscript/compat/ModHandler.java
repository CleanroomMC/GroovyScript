package com.cleanroommc.groovyscript.compat;

import com.cleanroommc.groovyscript.api.IGroovyPropertyGetter;
import com.cleanroommc.groovyscript.compat.enderio.EnderIO;
import com.cleanroommc.groovyscript.compat.mekanism.Mekanism;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class ModHandler implements IGroovyPropertyGetter {

    public static final ModHandler INSTANCE = new ModHandler();

    private final Map<String, Object> properties = new Object2ObjectOpenHashMap<>();

    private ModHandler() {
    }

    public void registerMod(String mod, Object modClass) {
        registerMod(mod, modClass, Loader.isModLoaded(mod));
    }

    public void registerMod(String mod, Object modClass, boolean condition) {
        registerMod(mod, () -> modClass, condition);
    }

    public void registerMod(String mod, Supplier<Object> modSupplier) {
        registerMod(mod, modSupplier, Loader.isModLoaded(mod));
    }

    public void registerMod(String mod, Supplier<Object> modSupplier, boolean condition) {
        if (condition) {
            properties.put(mod, Objects.requireNonNull(modSupplier.get(), () -> "Mod compat object must be non null!"));
        }
    }

    @Override
    public @Nullable Object getProperty(String name) {
        return properties.get(name);
    }

    public void initDefaults() {
        registerMod("mekanism", Mekanism::new);
        registerMod("enderio", EnderIO::new);
    }
}
