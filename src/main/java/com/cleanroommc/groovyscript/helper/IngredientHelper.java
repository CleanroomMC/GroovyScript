package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.api.IIngredient;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

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

    public static IIngredient toIIngredient(ItemStack itemStack) {
        return (IIngredient) (Object) itemStack;
    }

    public static IIngredient toIIngredient(FluidStack fluidStack) {
        return (IIngredient) fluidStack;
    }

    public static boolean isEmpty(IIngredient ingredient) {
        return ingredient == null || ingredient.getMatchingStacks().length == 0 || ingredient.getAmount() == 0;
    }

    public static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.isEmpty();
    }

    public static boolean isEmpty(FluidStack itemStack) {
        return itemStack == null || itemStack.amount <= 0;
    }

    public static String asGroovyCode(ItemStack itemStack) {
        String code = "'<item:" + itemStack.getItem().getRegistryName();
        if (itemStack.getMetadata() != 0) {
            code += ":" + itemStack.getMetadata();
        }
        code += ">'";
        if (itemStack.hasTagCompound()) {
            code += ".withNbt(" + NbtHelper.toGroovyCode(itemStack.getTagCompound(), false) + ")";
        }
        return itemStack.getCount() != 1 ? code + " * " + itemStack.getCount() : code;
    }

    public static String asGroovyCode(FluidStack fluidStack) {
        String code = "'<fluid:" + fluidStack.getFluid().getName() + ">'";
        return fluidStack.amount != 1000 ? code + " * " + fluidStack.amount : code;
    }

    public static String asGroovyCode(String oreDict, int amount) {
        String code = "'<ore:" + oreDict + ">'";
        return amount != 1 ? code + " * " + amount : code;
    }

}
