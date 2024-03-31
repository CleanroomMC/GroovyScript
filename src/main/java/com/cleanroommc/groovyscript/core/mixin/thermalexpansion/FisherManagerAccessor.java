package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStack;
import cofh.thermalexpansion.util.managers.device.FisherManager;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = FisherManager.class, remap = false)
public interface FisherManagerAccessor {

    @Accessor
    static List<ItemStack> getFishList() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static List<Integer> getWeightList() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static int getTotalWeight() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static void setTotalWeight(int totalWeight) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static TObjectIntHashMap<ComparableItemStack> getBaitMap() {
        throw new UnsupportedOperationException();
    }

}
