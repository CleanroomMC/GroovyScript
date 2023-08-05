package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.INBTResourceStack;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = FluidStack.class, remap = false)
public abstract class FluidStackMixin implements IIngredient, INBTResourceStack {

    @Shadow
    public int amount;

    @Shadow
    public NBTTagCompound tag;

    @NotNull
    @Shadow
    public abstract FluidStack copy();

    @Shadow
    public abstract boolean isFluidEqual(FluidStack other);

    @Shadow
    public abstract Fluid getFluid();

    @Unique
    protected Closure<Object> matchCondition;
    @Unique
    protected Closure<Object> transformer;

    @Override
    public IIngredient exactCopy() {
        FluidStackMixin fluidStackMixin = (FluidStackMixin) (Object) copy();
        fluidStackMixin.matchCondition = matchCondition;
        fluidStackMixin.transformer = transformer;
        return fluidStackMixin;
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(FluidUtil.getFilledBucket((FluidStack) (Object) this));
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return new ItemStack[0];
    }

    @Override
    public boolean isEmpty() {
        return getAmount() <= 0;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (matchCondition == null || ClosureHelper.call(true, matchCondition, stack)) {
            IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            if (fluidHandler == null) return false;
            net.minecraftforge.fluids.FluidStack result = fluidHandler.drain(copy(), false);
            return result != null && result.amount == getAmount();
        }
        return false;
    }

    @Override
    public boolean test(FluidStack fluidStack) {
        return isFluidEqual(fluidStack);
    }

    @Override
    public ItemStack applyTransform(ItemStack matchedInput) {
        IFluidHandlerItem fluidHandler = matchedInput.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (fluidHandler == null) return ItemStack.EMPTY;
        fluidHandler.drain(copy(), true);
        return fluidHandler.getContainer();
    }

    public FluidStack when(Closure<Object> matchCondition) {
        FluidStackMixin fresh = (FluidStackMixin) exactCopy();
        fresh.matchCondition = matchCondition;
        return (FluidStack) (Object) fresh;
    }

    public FluidStack transform(Closure<Object> transformer) {
        FluidStackMixin fresh = (FluidStackMixin) exactCopy();
        fresh.transformer = transformer;
        return (FluidStack) (Object) fresh;
    }

    @Override
    public @Nullable NBTTagCompound getNbt() {
        return tag;
    }

    @Override
    public void setNbt(NBTTagCompound nbt) {
        this.tag = nbt;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
