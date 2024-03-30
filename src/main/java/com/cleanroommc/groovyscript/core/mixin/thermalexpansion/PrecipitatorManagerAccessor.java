package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.util.ItemWrapper;
import cofh.thermalexpansion.util.managers.machine.PrecipitatorManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(value = PrecipitatorManager.class, remap = false)
public interface PrecipitatorManagerAccessor {

    @Accessor
    static Map<ItemWrapper, PrecipitatorManager.PrecipitatorRecipe> getRecipeMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static List<ItemStack> getOutputList() {
        throw new UnsupportedOperationException();
    }

}
