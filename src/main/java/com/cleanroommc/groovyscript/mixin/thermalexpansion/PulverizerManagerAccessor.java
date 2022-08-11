package com.cleanroommc.groovyscript.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(value = PulverizerManager.class, remap = false)
public interface PulverizerManagerAccessor {

    @Accessor
    static Map<ComparableItemStackValidated, PulverizerManager.PulverizerRecipe> getRecipeMap() {
        throw new AssertionError();
    }

    @Accessor
    static void setRecipeMap(Map<ComparableItemStackValidated, PulverizerManager.PulverizerRecipe> map) { }

    @Invoker
    static ComparableItemStackValidated invokeConvertInput(ItemStack stack) {
        throw new AssertionError();
    }

}
