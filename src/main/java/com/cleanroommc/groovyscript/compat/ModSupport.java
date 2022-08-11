package com.cleanroommc.groovyscript.compat;

import com.cleanroommc.groovyscript.api.IGroovyPropertyGetter;
import com.cleanroommc.groovyscript.compat.enderio.EnderIO;
import com.cleanroommc.groovyscript.compat.mekanism.Mekanism;
import com.cleanroommc.groovyscript.compat.thermalexpansion.ThermalExpansion;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import groovy.lang.Binding;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ModSupport implements IGroovyPropertyGetter {

    ENDER_IO("enderio", "Ender IO", EnderIO::new),
    JEI("jei", "JustEnoughItems"),
    MEKANISM("mekanism", "Mekanism", Mekanism::new),
    THERMAL_EXPANSION("thermalexpansion", "Thermal Expansion", ThermalExpansion::new);

    public static void initBindings(Binding binding) {
        for (ModSupport modSupport : ModSupport.values()) {
            binding.setVariable("mods", modSupport);
        }
    }

    private final String modId, modName;
    @Nullable private final Supplier<?> modProperty;

    ModSupport(String modId, String modName) {
        this.modId = modId;
        this.modName = modName;
        this.modProperty = null;
    }

    ModSupport(String modId, String modName, @NotNull Supplier<?> modProperty) {
        this.modId = modId;
        this.modName = modName;
        this.modProperty = Suppliers.memoize(modProperty);
    }

    public boolean isLoaded() {
        return Loader.isModLoaded(modId);
    }

    public String getId() {
        return modId;
    }

    @Override
    @Nullable
    public Object getProperty(String name) {
        return modProperty == null ? null : modProperty.get();
    }

    @Nullable
    public <T> T getProperty(Class<T> clazz) {
        return modProperty == null ? null : (T) modProperty.get();
    }

    @Override
    public String toString() {
        return modName;
    }

}
