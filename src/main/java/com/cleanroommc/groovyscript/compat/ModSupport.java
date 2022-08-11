package com.cleanroommc.groovyscript.compat;

import com.cleanroommc.groovyscript.api.IGroovyPropertyGetter;
import com.cleanroommc.groovyscript.compat.enderio.EnderIO;
import com.cleanroommc.groovyscript.compat.jei.JustEnoughItems;
import com.cleanroommc.groovyscript.compat.mekanism.Mekanism;
import com.cleanroommc.groovyscript.compat.thermalexpansion.ThermalExpansion;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ModSupport implements IGroovyPropertyGetter {

    public static final ModSupport INSTANCE = new ModSupport();

    public static final Container<EnderIO> ENDER_IO = new Container<>("enderio", "Ender IO", EnderIO::new);
    public static final Container<JustEnoughItems> JEI = new Container<>("jei", "Just Enough Items", JustEnoughItems::new);
    public static final Container<Mekanism> MEKANISM = new Container<>("mekanism", "Mekanism", Mekanism::new);
    public static final Container<ThermalExpansion> THERMAL_EXPANSION = new Container<>("thermalexpansion", "Thermal Expansion", ThermalExpansion::new);

    private final Map<String, Container<?>> containers = new Object2ObjectOpenHashMap<>();

    @Override
    @Nullable
    public Object getProperty(String name) {
        Container<?> container = containers.get(name);
        if (container != null) {
            return container.modProperty.get();
        }
        return null;
    }

    public static class Container<T> {

        private final String modId, modName;
        private final Supplier<T> modProperty;

        public Container(String modId, String modName, @NotNull Supplier<T> modProperty) {
            this.modId = modId;
            this.modName = modName;
            this.modProperty = Suppliers.memoize(modProperty);
            ModSupport.INSTANCE.containers.put(modId, this);
        }

        public boolean isLoaded() {
            return Loader.isModLoaded(modId);
        }

        public String getId() {
            return modId;
        }

        public T get() {
            return modProperty == null ? null : modProperty.get();
        }

        @Override
        public String toString() {
            return modName;
        }

    }

}
