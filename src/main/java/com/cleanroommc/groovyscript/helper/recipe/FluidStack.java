package com.cleanroommc.groovyscript.helper.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidStack extends IngredientBase {

    public static FluidStack parse(String s) {
        return new FluidStack(FluidRegistry.getFluidStack(s, 1000));
    }

    private final net.minecraftforge.fluids.FluidStack internal;

    public FluidStack(net.minecraftforge.fluids.FluidStack internal) {
        this.internal = internal;
        if (internal != null && internal.amount < 0) {
            internal.amount = 0;
        }
    }

    @Override
    public FluidStack setCount(int amount) {
        if (internal != null) {
            internal.amount = Math.max(amount, 0);
        }
        return this;
    }

    @Override
    public int getCount() {
        return internal == null ? 0 : internal.amount;
    }

    public boolean isEmpty() {
        return internal == null || internal.amount <= 0;
    }

    @Override
    public IIngredient exactCopy() {
        return new FluidStack(internal.copy());
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(FluidUtil.getFilledBucket(internal));
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (internal == null) return false;
        IFluidHandlerItem fluidHandler = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (fluidHandler == null) return false;
        net.minecraftforge.fluids.FluidStack result = fluidHandler.drain(internal.copy(), false);
        return result != null && result.amount == internal.amount;
    }

    @Override
    public ItemStack applyTransform(ItemStack matchedInput) {
        IFluidHandlerItem fluidHandler = matchedInput.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (internal == null || fluidHandler == null) return fluidHandler == null ? ItemStack.EMPTY : matchedInput;
        fluidHandler.drain(internal.copy(), true);
        return fluidHandler.getContainer();
    }
}
