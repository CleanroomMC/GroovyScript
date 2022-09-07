package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.api.IReloadableForgeRegistry;
import com.google.common.collect.BiMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(value = ForgeRegistry.class, remap = false)
public abstract class ForgeRegistryMixin<V extends IForgeRegistryEntry<V>> implements IForgeRegistry<V>, IReloadableForgeRegistry<V> {

    @Shadow public abstract void register(V value);
    @Shadow public abstract void unfreeze();
    @Shadow public abstract V getValue(int id);
    @Shadow abstract int add(int id, V value, String owner);

    @Shadow @Final private BiMap<ResourceLocation, V> names;
    @Shadow @Final private BiMap<Integer, V> ids;
    @Shadow @Final private BiMap<Object, V> owners;

    @Unique private Set<Triple<V, Integer, Object>> backups;
    @Unique private Set<V> scripted;

    @Override
    public V registerEntry(V registryEntry) {
        V newEntry = getValue(add(-1, registryEntry, null));
        if (newEntry == registryEntry) {
            if (this.scripted == null) {
                this.scripted = new ObjectOpenHashSet<>();
            }
            this.scripted.add(registryEntry);
        }
        return newEntry;
    }

    @Override
    public void removeEntry(ResourceLocation name) {
        V entry = this.names.remove(name);
        if (entry != null) {
            if (this.backups == null) {
                this.backups = new ObjectOpenHashSet<>();
            }
            Integer id = this.ids.inverse().remove(entry);
            Object ownerOverride = this.owners.inverse().remove(entry);
            this.backups.add(Triple.of(entry, id, ownerOverride));
        }
    }

    @Override
    public void onReload() {
        unfreeze();
        if (this.scripted != null) {
            for (V entry : this.scripted) {
                this.names.inverse().remove(entry);
                this.ids.inverse().remove(entry);
                this.owners.inverse().remove(entry);
            }
            this.scripted = null;
        }
        if (this.backups != null) {
            for (Triple<V, Integer, Object> entry : this.backups) {
                this.names.put(entry.getLeft().getRegistryName(), entry.getLeft());
                this.ids.put(entry.getMiddle(), entry.getLeft());
                this.owners.put(entry.getRight(), entry.getLeft());
            }
            this.backups = null;
        }
    }

}

