package com.cleanroommc.groovyscript.mixin;

import com.cleanroommc.groovyscript.registry.IReloadableRegistry;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.item.crafting.IRecipe;
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

// TODO
@Mixin(value = ForgeRegistry.class, remap = false)
public abstract class ForgeRegistryMixin<V extends IForgeRegistryEntry<V>> implements IReloadableRegistry<V> {

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
    boolean isFrozen;

    @Shadow
    public abstract void register(V value);

    @Shadow
    @Final
    private Class<V> superType;
    @Unique
    private BiMap<Integer, V> idsBackup;
    @Unique
    private BiMap<ResourceLocation, V> namesBackup;
    @Unique
    private BitSet availabilityMapBackup;
    @Unique
    private BitSet reloadableEntryIds;
    @Unique
    private boolean reloadable;

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
        idsBackup = HashBiMap.create();
        namesBackup = HashBiMap.create();
        availabilityMapBackup = new BitSet();
        reloadableEntryIds = new BitSet();
        reloadable = superType == IRecipe.class;
    }

    @Override
    public void onReload() {
        if (!reloadable) {
            throw new IllegalStateException("Registry of type " + superType.getName() + " is not reloadable!");
        }
        ids.clear();
        ids.putAll(idsBackup);
        names.clear();
        names.putAll(namesBackup);
        availabilityMap.clear();
        availabilityMap.or(availabilityMapBackup);
        reloadableEntryIds.clear();
    }

    @Override
    public void removeEntry(V dummy) {
        if (!reloadable) {
            throw new IllegalStateException("Registry of type " + superType.getName() + " is not reloadable!");
        }
        register(dummy);
    }

    @Inject(
            method = "add(ILnet/minecraftforge/registries/IForgeRegistryEntry;Ljava/lang/String;)I",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE", target = "Ljava/util/BitSet;set(I)V")
    )
    public void cacheEntry(int id, V value, String owner, CallbackInfoReturnable<Integer> cir, ResourceLocation key, int idToUse, IForgeRegistryEntry<V> oldEntry, Integer foundId) {
        if (!reloadable) return;
        boolean registered = false;
        if (!ReloadableRegistryManager.isFirstLoad() || (registered = reloadableEntryIds.get(idToUse))) {
            if (!registered) {
                this.reloadableEntryIds.set(idToUse);
            }
        } else {
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
        return isFrozen && (reloadable && ReloadableRegistryManager.isFirstLoad());
    }
}
