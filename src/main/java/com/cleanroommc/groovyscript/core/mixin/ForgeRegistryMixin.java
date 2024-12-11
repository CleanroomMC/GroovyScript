package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.api.GroovyLog;
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
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(value = ForgeRegistry.class, remap = false)
public abstract class ForgeRegistryMixin<V extends IForgeRegistryEntry<V>> implements IForgeRegistry<V>, IReloadableForgeRegistry<V> {

    @Override
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
    private Set<VirtualizedForgeRegistryEntry<V>> groovyScript$backups;
    @Unique
    private Set<V> groovyScript$scripted;
    @Unique
    private Supplier<V> groovyScript$dummySupplier;
    @Unique
    @Final
    private final Set<ResourceLocation> groovyScript$dummies = new ObjectOpenHashSet<>();

    @Unique
    private IReloadableForgeRegistry<V> groovyScript$vanilla;
    @Unique
    private IReloadableForgeRegistry<V> groovyScript$frozen;

    @Override
    public V groovyScript$registerEntry(V registryEntry) {
        if (stage != RegistryManager.ACTIVE) throw new IllegalStateException("Do not modify VANILLA or FROZEN registry directly!");
        Objects.requireNonNull(registryEntry);
        Objects.requireNonNull(registryEntry.getRegistryName());
        int id = groovyScript$removeDummy(registryEntry.getRegistryName(), DummyContext.ADDITION);
        id = add(id, registryEntry, null);
        V newEntry = getValue(id);
        if (newEntry == registryEntry) {
            if (this.groovyScript$scripted == null) {
                this.groovyScript$scripted = new ObjectOpenHashSet<>();
            }
            this.groovyScript$scripted.add(registryEntry);
            Object owner = this.owners.inverse().get(registryEntry);
            groovyScript$initReg();
            this.groovyScript$vanilla.groovyScript$forceAdd(registryEntry, id, owner);
            this.groovyScript$frozen.groovyScript$forceAdd(registryEntry, id, owner);
        }
        return newEntry;
    }

    @Override
    public void groovyScript$removeEntry(ResourceLocation name) {
        if (stage != RegistryManager.ACTIVE) throw new IllegalStateException("Do not modify VANILLA or FROZEN registry directly!");
        if (this.groovyScript$dummies.contains(name)) return;
        V entry = this.names.remove(name);
        if (entry != null) {
            if (this.groovyScript$backups == null) {
                this.groovyScript$backups = new ObjectOpenHashSet<>();
            }
            Integer id = this.ids.inverse().remove(entry);
            Object ownerOverride = this.owners.inverse().remove(entry);
            if (id == null) {
                throw new IllegalStateException(GroovyLog.format("Found recipe for {}, but no id! Entry {}", name, entry));
            }
            if (this.groovyScript$scripted == null || !this.groovyScript$scripted.contains(entry)) {
                this.groovyScript$backups.add(new VirtualizedForgeRegistryEntry<>(entry, id, ownerOverride));
            }
            V dummy = groovyScript$putDummy(entry, name, id, ownerOverride, DummyContext.REMOVAL);
            groovyScript$initReg();
            this.groovyScript$vanilla.groovyScript$putDummy(dummy, entry, name, id, ownerOverride);
            this.groovyScript$frozen.groovyScript$putDummy(dummy, entry, name, id, ownerOverride);
        }
    }

    @Override
    public void groovyScript$onReload() {
        if (stage != RegistryManager.ACTIVE) throw new IllegalStateException("Do not modify VANILLA or FROZEN registry directly!");
        unfreeze();
        groovyScript$initReg();
        if (this.groovyScript$scripted != null) {
            for (V entry : this.groovyScript$scripted) {
                ResourceLocation rl = this.names.inverse().remove(entry);
                Integer id = this.ids.inverse().remove(entry);
                Object owner = this.owners.inverse().remove(entry);
                if (id == null || rl == null) continue; // can happen, but no one knows why
                V dummy = groovyScript$putDummy(entry, rl, id, owner, DummyContext.RELOADING);
                this.groovyScript$vanilla.groovyScript$putDummy(dummy, entry, rl, id, owner);
                this.groovyScript$frozen.groovyScript$putDummy(dummy, entry, rl, id, owner);
            }
            this.groovyScript$scripted = null;
        }
        if (this.groovyScript$backups != null) {
            for (VirtualizedForgeRegistryEntry<V> entry : this.groovyScript$backups) {
                this.names.put(entry.getValue().getRegistryName(), entry.getValue());
                this.ids.put(entry.getId(), entry.getValue());
                this.owners.put(entry.getOverride(), entry.getValue());
                this.groovyScript$dummies.remove(entry.getValue().getRegistryName());
                this.groovyScript$vanilla.groovyScript$forceAdd(entry.getValue(), entry.getId(), entry.getOverride());
                this.groovyScript$frozen.groovyScript$forceAdd(entry.getValue(), entry.getId(), entry.getOverride());
            }
            this.groovyScript$backups = null;
        }
    }

    @Unique
    public V groovyScript$putDummy(V entry, ResourceLocation rl, Integer id, Object owner, DummyContext context) {
        if (entry == null || rl == null || id == null) {
            GroovyLog.get()
                    .errorMC(
                            "Error putting dummy in forge registry for {} during {} at stage {}. Are null: entry-{}, name-{}, id-{}",
                            superType.getSimpleName(),
                            context.name().toLowerCase(Locale.ROOT),
                            stage.getName(),
                            entry == null,
                            rl == null,
                            id == null);
            return null;
        }
        V dummy = groovyScript$getDummy(rl);
        groovyScript$putDummy(dummy, entry, rl, id, owner);
        return dummy;
    }

    @Override
    public void groovyScript$putDummy(V dummy, V entry, ResourceLocation rl, int id, Object owner) {
        if (dummy != null) {
            this.names.put(rl, dummy);
            this.ids.put(id, dummy);
            if (owner != null) {
                this.owners.put(owner, dummy);
            }
            this.groovyScript$dummies.add(rl);
        }
    }

    @Unique
    public int groovyScript$removeDummy(ResourceLocation rl, DummyContext context) {
        V dummy = this.names.remove(rl);
        int id0 = -1;
        if (dummy != null) {
            Integer id = this.ids.inverse().remove(dummy);
            if (id == null) {
                GroovyLog.get()
                        .errorMC(
                                "No id found while removing a dummy with name '{}' from {} registry at stage {}.",
                                rl,
                                superType.getSimpleName(),
                                stage.getName());
            } else {
                this.availabilityMap.clear(id);
                id0 = id;
            }
            this.owners.inverse().remove(dummy);
        }
        this.groovyScript$dummies.remove(rl);
        return id0;
    }

    @Unique
    public @Nullable V groovyScript$getDummy(ResourceLocation rl) {
        if (dummyFactory != null) {
            return dummyFactory.createDummy(rl);
        }
        if (groovyScript$dummySupplier == null) {
            groovyScript$dummySupplier = ReloadableRegistryManager.getDummySupplier(getRegistrySuperType());
        }
        V value = groovyScript$dummySupplier.get();
        if (value != null) value.setRegistryName(rl);
        return value;
    }

    @Override
    public void groovyScript$forceAdd(V entry, int id, Object owner) {
        names.forcePut(entry.getRegistryName(), entry);
        ids.forcePut(id, entry);
        if (owner != null) owners.forcePut(owner, entry);
        availabilityMap.set(id);
    }

    @SuppressWarnings("unchecked")
    @Unique
    private void groovyScript$initReg() {
        if (groovyScript$frozen == null || groovyScript$frozen.groovyScript$isDummy()) {
            groovyScript$frozen = (IReloadableForgeRegistry<V>) RegistryManager.FROZEN.getRegistry(superType);
            if (groovyScript$frozen == null) groovyScript$frozen = new DummyRFG<>();
        }
        if (groovyScript$vanilla == null || groovyScript$vanilla.groovyScript$isDummy()) {
            groovyScript$vanilla = (IReloadableForgeRegistry<V>) RegistryManager.VANILLA.getRegistry(superType);
            if (groovyScript$vanilla == null) groovyScript$vanilla = new DummyRFG<>();
        }
    }
}
