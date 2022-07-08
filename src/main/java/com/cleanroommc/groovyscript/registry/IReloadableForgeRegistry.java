package com.cleanroommc.groovyscript.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

public interface IReloadableForgeRegistry<V extends IForgeRegistryEntry<V>> {

    void onReload();

    default void registerReloadableEntry(V value) {
        addReloadableEntry(-1, value);
    }

    default int addReloadableEntry(int id, V value) {
        ModContainer mc = Loader.instance().activeModContainer();
        String owner = mc == null || (mc instanceof InjectedModContainer && ((InjectedModContainer) mc).wrappedContainer instanceof FMLContainer) ? null : mc.getModId().toLowerCase();
        return addReloadableEntry(id, value, owner);
    }

    int addReloadableEntry(int i, V value, String owner);

    void removeEntry(ResourceLocation rl);

    @Unmodifiable
    Collection<V> getReloadableEntries();
}
