package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.api.IIngredient;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

import java.util.List;

public class IngredientHelper {

    public static boolean isFluid(IIngredient ingredient) {
        return ingredient instanceof FluidStack;
    }

    public static boolean isGas(IIngredient ingredient) {
        return Loader.isModLoaded("mekanism") && ingredient instanceof GasStack;
    }

    @SuppressWarnings("all")
    public static boolean isItem(IIngredient ingredient) {
        return (Object) ingredient instanceof ItemStack;
    }

    @SuppressWarnings("all")
    public static ItemStack toItemStack(IIngredient ingredient) {
        return (ItemStack) (Object) ingredient;
    }

    public static FluidStack toFluidStack(IIngredient ingredient) {
        return (FluidStack) ingredient;
    }

    public static boolean isEmpty(IIngredient ingredient) {
        return ingredient == null || ingredient.getAmount() == 0;
    }

    public static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.isEmpty();
    }

}
