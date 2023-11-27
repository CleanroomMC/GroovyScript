package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.api.IReloadableForgeRegistry;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.registry.VirtualizedForgeRegistryEntry;
import com.google.common.collect.BiMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.BitSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(value = ForgeRegistry.class, remap = false)
public abstract class ForgeRegistryMixin<V extends IForgeRegistryEntry<V>> implements IForgeRegistry<V>, IReloadableForgeRegistry<V> {

    @Shadow
    public abstract void register(V value);

    @Shadow
    public abstract void unfreeze();

    @Shadow
    public abstract V getValue(int id);

    @Shadow
    abstract int add(int id, V value, String owner);

    @Shadow
    @Final
    private BiMap<ResourceLocation, V> names;
    @Shadow
    @Final
    private BiMap<Integer, V> ids;
    @Shadow
    @Final
    private BiMap<Object, V> owners;
    @Shadow
    @Final
    private DummyFactory<V> dummyFactory;
    @Shadow
    @Final
    private BitSet availabilityMap;

    @Shadow
    public abstract V remove(ResourceLocation key);

    @Shadow
    @Final
    private Class<V> superType;
    @Shadow
    @Final
    private RegistryManager stage;
    @Unique
    private Set<VirtualizedForgeRegistryEntry<V>> backups;
    @Unique
    private Set<V> scripted;
    @Unique
    private Supplier<V> dummySupplier;
    @Unique
    @Final
    private final Set<ResourceLocation> dummies = new ObjectOpenHashSet<>();

    @Unique
    private IReloadableForgeRegistry<V> vanilla;
    @Unique
    private IReloadableForgeRegistry<V> frozen;

    @Override
    public V registerEntry(V registryEntry) {
        if (stage != RegistryManager.ACTIVE) throw new IllegalStateException("Do not modify VANILLA or FROZEN registry directly!");
        if (registryEntry != null) {
            ResourceLocation rl = registryEntry.getRegistryName();
            if (rl != null) {
                groovyscript$removeDummy(rl);
            }
        }
        int id = add(-1, registryEntry, null);
        V newEntry = getValue(id);
        if (newEntry == registryEntry) {
            if (this.scripted == null) {
                this.scripted = new ObjectOpenHashSet<>();
            }
            this.scripted.add(registryEntry);
            Object owner = this.owners.inverse().get(registryEntry);
            grs$do(reg -> reg.groovyscript$forceAdd(registryEntry, id, owner));
        }
        return newEntry;
    }

    @Override
    public void removeEntry(ResourceLocation name) {
        if (stage != RegistryManager.ACTIVE) throw new IllegalStateException("Do not modify VANILLA or FROZEN registry directly!");
        if (this.dummies.contains(name)) return;
        V entry = this.names.remove(name);
        if (entry != null) {
            if (this.backups == null) {
                this.backups = new ObjectOpenHashSet<>();
            }
            Integer id = this.ids.inverse().remove(entry);
            Object ownerOverride = this.owners.inverse().remove(entry);
            if (id == null) throw new IllegalStateException();
            if (this.scripted == null || !this.scripted.contains(entry)) {
                this.backups.add(new VirtualizedForgeRegistryEntry<>(entry, id, ownerOverride));
            }
            V dummy = groovyscript$putDummy(entry, name, id, ownerOverride);
            grs$do(reg -> reg.groovyscript$putDummy(dummy, entry, name, id, ownerOverride));
        }
    }

    @Override
    public void onReload() {
        if (stage != RegistryManager.ACTIVE) throw new IllegalStateException("Do not modify VANILLA or FROZEN registry directly!");
        unfreeze();
        if (this.scripted != null) {
            for (V entry : this.scripted) {
                ResourceLocation rl = this.names.inverse().remove(entry);
                Integer id = this.ids.inverse().remove(entry);
                Object owner = this.owners.inverse().remove(entry);
                V dummy = groovyscript$putDummy(entry, rl, id, owner);
                grs$do(reg -> reg.groovyscript$putDummy(dummy, entry, rl, id, owner));
            }
            this.scripted = null;
        }
        if (this.backups != null) {
            for (VirtualizedForgeRegistryEntry<V> entry : this.backups) {
                this.names.put(entry.getValue().getRegistryName(), entry.getValue());
                this.ids.put(entry.getId(), entry.getValue());
                this.owners.put(entry.getOverride(), entry.getValue());
                this.dummies.remove(entry.getValue().getRegistryName());
                grs$do(reg -> reg.groovyscript$forceAdd(entry.getValue(), entry.getId(), entry.getOverride()));
            }
            this.backups = null;
        }
    }

    public V groovyscript$putDummy(V entry, ResourceLocation rl, Integer id, Object owner) {
        if (entry == null || rl == null || id == null) throw new IllegalArgumentException();
        V dummy = groovyscript$getDummy(rl);
        groovyscript$putDummy(dummy, entry, rl, id, owner);
        return dummy;
    }

    @Override
    public void groovyscript$putDummy(V dummy, V entry, ResourceLocation rl, int id, Object owner) {
        if (dummy != null) {
            this.names.put(rl, dummy);
            this.ids.put(id, dummy);
            if (owner != null) {
                this.owners.put(owner, dummy);
            }
            this.dummies.add(rl);
        }
    }

    public void groovyscript$removeDummy(ResourceLocation rl) {
        V dummy = this.names.remove(rl);
        if (dummy != null) {
            int id = this.ids.inverse().remove(dummy);
            this.owners.inverse().remove(dummy);
            this.availabilityMap.clear(id);
        }
        this.dummies.remove(rl);
    }

    @Nullable
    public V groovyscript$getDummy(ResourceLocation rl) {
        if (dummyFactory != null) {
            return dummyFactory.createDummy(rl);
        }
        if (dummySupplier == null) {
            dummySupplier = ReloadableRegistryManager.getDummySupplier(getRegistrySuperType());
        }
        V value = dummySupplier.get();
        if (value != null) value.setRegistryName(rl);
        return value;
    }

    @Override
    public void groovyscript$forceAdd(V entry, int id, Object owner) {
        names.put(entry.getRegistryName(), entry);
        ids.put(id, entry);
        if (owner != null) owners.put(owner, entry);
        availabilityMap.set(id);
    }

    private void grs$do(Consumer<IReloadableForgeRegistry<V>> consumer) {
        if (frozen == null) frozen = (IReloadableForgeRegistry<V>) RegistryManager.FROZEN.getRegistry(superType);
        if (vanilla == null) vanilla = (IReloadableForgeRegistry<V>) RegistryManager.VANILLA.getRegistry(superType);
        if (frozen != null) consumer.accept(frozen);
        if (vanilla != null) consumer.accept(vanilla);
    }
}

