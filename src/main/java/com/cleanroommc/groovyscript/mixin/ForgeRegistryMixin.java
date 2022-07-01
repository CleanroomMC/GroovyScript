package com.cleanroommc.groovyscript.mixin;

import com.cleanroommc.groovyscript.registry.IReloadableForgeRegistry;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

@Mixin(value = ForgeRegistry.class, remap = false)
public abstract class ForgeRegistryMixin<V extends IForgeRegistryEntry<V>> implements IReloadableForgeRegistry<V> {

    @Shadow
    @Final
    private BitSet availabilityMap;

    @Shadow
    @Final
    private BiMap<Integer, V> ids;
    @Shadow
    @Final
    private BiMap<ResourceLocation, V> names;

    @Shadow
    abstract int add(int id, V value, String owner);

    @Shadow
    public abstract V getValue(ResourceLocation key);

    @Shadow private boolean isFrozen;
    @Unique
    private Set<ResourceLocation> removedEntries;
    @Unique
    private BiMap<Integer, V> idsBackup;
    @Unique
    private BiMap<ResourceLocation, V> namesBackup;
    @Unique
    private BitSet availabilityMapBackup;

    private boolean isReloadableEntry = false;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void construct(Class<V> superType,
                          ResourceLocation defaultKey,
                          int min, int max,
                          IForgeRegistry.CreateCallback<V> create,
                          IForgeRegistry.AddCallback<V> add,
                          IForgeRegistry.ClearCallback<V> clear,
                          IForgeRegistry.ValidateCallback<V> validate,
                          RegistryManager stage,
                          boolean allowOverrides,
                          boolean isModifiable,
                          IForgeRegistry.DummyFactory<V> dummyFactory,
                          IForgeRegistry.MissingFactory<V> missing,
                          CallbackInfo ci) {
        removedEntries = new HashSet<>();
        idsBackup = HashBiMap.create();
        namesBackup = HashBiMap.create();
        availabilityMapBackup = new BitSet();
    }

    @Override
    public void onReload() {
        removedEntries.clear();
        ids.clear();
        ids.putAll(idsBackup);
        names.clear();
        names.putAll(namesBackup);
        availabilityMap.clear();
        availabilityMap.or(availabilityMapBackup);
    }

    @Override
    public int addReloadableEntry(int i, V value, String owner) {
        isReloadableEntry = true;
        int result = add(i, value, owner);
        isReloadableEntry = false;
        return result;
    }

    @Override
    public void removeEntry(ResourceLocation rl) {
        V value = getValue(rl);
        if (value != null) {
            Integer id = ids.inverse().remove(value);
            names.inverse().remove(value);
            availabilityMap.clear(id);
        }
    }

    @Inject(
            method = "add(ILnet/minecraftforge/registries/IForgeRegistryEntry;Ljava/lang/String;)I",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE", target = "Ljava/util/BitSet;set(I)V")
    )
    public void cacheEntry(int id, V value, String owner, CallbackInfoReturnable<Integer> cir, ResourceLocation key, int idToUse, IForgeRegistryEntry<V> oldEntry, Integer foundId) {
        if (!isReloadableEntry) {
            this.namesBackup.put(key, value);
            this.idsBackup.put(idToUse, value);
            this.availabilityMapBackup.set(idToUse);
        }
    }

    @Redirect(
            method = "add(ILnet/minecraftforge/registries/IForgeRegistryEntry;Ljava/lang/String;)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/registries/ForgeRegistry;isLocked()Z")
    )
    public boolean lateAdditionCheck(ForgeRegistry<V> instance) {
        return isFrozen && !isReloadableEntry;
    }
}
