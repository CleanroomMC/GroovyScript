package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.util.BlockWrapper;
import cofh.core.util.ItemWrapper;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import com.google.common.collect.SetMultimap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = TapperManager.class, remap = false)
public interface TapperManagerAccessor {

    @Accessor
    static Map<BlockWrapper, FluidStack> getBlockMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Map<ItemWrapper, FluidStack> getItemMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static SetMultimap<BlockWrapper, BlockWrapper> getLeafMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static TObjectIntHashMap<ComparableItemStack> getFertilizerMap() {
        throw new UnsupportedOperationException();
    }

}
