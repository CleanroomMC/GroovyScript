package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.api.IIngredient;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return ingredient == null || ingredient.getMatchingStacks().length == 0 || ingredient.getAmount() <= 0;
    }

    public static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.isEmpty();
    }

    public static boolean isEmpty(FluidStack itemStack) {
        return itemStack == null || itemStack.amount <= 0;
    }


    public static boolean isEmpty(GasStack gasStack) {
        return gasStack == null || gasStack.getGas() == null || gasStack.amount <= 0;
    }

    /**
     * Useful when the item can be empty or null, but only want to copy non empty items
     */
    @NotNull
    public static ItemStack copy(@Nullable ItemStack item) {
        return item == null || item == ItemStack.EMPTY ? ItemStack.EMPTY : item.copy();
    }

    @Contract("null -> null")
    public static FluidStack copy(FluidStack fluid) {
        return fluid == null ? null : fluid.copy();
    }

    public static String asGroovyCode(ItemStack itemStack, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append("'<");
        if (colored) builder.append(TextFormatting.DARK_GREEN);
        builder.append("item");
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append(":");
        if (colored) builder.append(TextFormatting.AQUA);
        builder.append(itemStack.getItem().getRegistryName());
        if (colored) builder.append(TextFormatting.GRAY);
        if (itemStack.getMetadata() != 0) {
            builder.append(":");
            if (colored) builder.append(TextFormatting.GOLD);
            builder.append(itemStack.getMetadata());
            if (colored) builder.append(TextFormatting.GRAY);
        }
        builder.append(">'");
        return builder.toString();
    }

    public static String asGroovyCode(ItemStack itemStack, boolean colored, boolean prettyNbt) {
        StringBuilder builder = new StringBuilder().append(asGroovyCode(itemStack, colored));
        if (itemStack.hasTagCompound()) {
            builder.append(".withNbt(");
            builder.append(NbtHelper.toGroovyCode(itemStack.getTagCompound(), prettyNbt, colored));
            if (colored) builder.append(TextFormatting.GRAY);
            builder.append(")");
        }
        return builder.toString();
    }

    public static String asGroovyCode(FluidStack fluidStack, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append("'<");
        if (colored) builder.append(TextFormatting.DARK_GREEN);
        builder.append("fluid");
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append(":");
        if (colored) builder.append(TextFormatting.AQUA);
        builder.append(fluidStack.getFluid().getName());
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append(">'");
        return builder.toString();
    }

    public static String asGroovyCode(Gas gasStack, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append("'<");
        if (colored) builder.append(TextFormatting.DARK_GREEN);
        builder.append("gas");
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append(":");
        if (colored) builder.append(TextFormatting.AQUA);
        builder.append(gasStack.getName());
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append(">'");
        return builder.toString();
    }

    public static String asGroovyCode(String oreDict, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append("'<");
        if (colored) builder.append(TextFormatting.DARK_GREEN);
        builder.append("ore");
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append(":");
        if (colored) builder.append(TextFormatting.AQUA);
        builder.append(oreDict);
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append(">'");
        return builder.toString();
    }

}
