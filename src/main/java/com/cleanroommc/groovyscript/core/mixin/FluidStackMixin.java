package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.INBTResourceStack;
import com.cleanroommc.groovyscript.api.INbtIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.NbtHelper;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import com.cleanroommc.groovyscript.sandbox.expand.LambdaClosure;
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
public abstract class FluidStackMixin implements IIngredient, INbtIngredient, INBTResourceStack {

    @Shadow
    public int amount;

    @Shadow
    public NBTTagCompound tag;

    @Shadow
    public abstract @NotNull FluidStack copy();

    @Shadow
    public abstract boolean isFluidEqual(FluidStack other);

    @Shadow
    public abstract Fluid getFluid();

    @Unique
    protected Closure<Object> matchCondition;
    @Unique
    protected Closure<Object> transformer;
    @Unique
    protected Closure<Object> nbtMatcher;
    @Unique
    protected String groovyScript$mark;

    @Override
    public IIngredient exactCopy() {
        FluidStackMixin fluidStackMixin = (FluidStackMixin) (Object) copy();
        fluidStackMixin.matchCondition = matchCondition;
        fluidStackMixin.transformer = transformer;
        fluidStackMixin.nbtMatcher = nbtMatcher;
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
        if (nbtMatcher != null) {
            NBTTagCompound nbt = stack.getTagCompound();
            return nbt != null && ClosureHelper.call(true, nbtMatcher, nbt);
        }
        return false;
    }

    @Override
    public boolean test(FluidStack fluidStack) {
        if (fluidStack == null) {
            return false;
        }
        if (nbtMatcher != null) {
            NBTTagCompound nbt = fluidStack.tag;
            return nbt != null && ClosureHelper.call(true, nbtMatcher, nbt);
        }
        return getFluid() == fluidStack.getFluid();
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
    public INBTResourceStack withNbt(NBTTagCompound nbt) {
        FluidStackMixin fluidStackMixin = (FluidStackMixin) INbtIngredient.super.withNbt(nbt);
        fluidStackMixin.nbtMatcher = NbtHelper.makeNbtPredicate(nbt1 -> nbt.isEmpty() || NbtHelper.containsNbt(nbt1, nbt));
        return fluidStackMixin;
    }

    @Override
    public INbtIngredient withNbtExact(NBTTagCompound nbt) {
        FluidStackMixin fluidStackMixin = (FluidStackMixin) INbtIngredient.super.withNbt(nbt);
        if (nbt == null) {
            fluidStackMixin.nbtMatcher = null;
        } else {
            fluidStackMixin.nbtMatcher = NbtHelper.makeNbtPredicate(nbt1 -> nbt.isEmpty() || nbt1.equals(nbt));
        }
        return fluidStackMixin;
    }

    public INbtIngredient withNbtFilter(Closure<Object> nbtFilter) {
        this.nbtMatcher = nbtFilter == null ? IngredientHelper.MATCH_ANY : nbtFilter;
        return this;
    }

    public INbtIngredient whenNoNbt() {
        setNbt(null);
        this.matchCondition = new LambdaClosure<>(args -> {
            NBTTagCompound nbt = ((FluidStack) args[0]).tag;
            return nbt == null || nbt.isEmpty();
        });
        return this;
    }

    public INbtIngredient whenAnyNbt() {
        setNbt(new NBTTagCompound());
        this.matchCondition = new LambdaClosure<>(args -> {
            NBTTagCompound nbt = ((FluidStack) args[0]).tag;
            return nbt != null && !nbt.isEmpty();
        });
        return this;
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

    @Override
    public @Nullable String getMark() {
        return groovyScript$mark;
    }

    @Override
    public void setMark(String mark) {
        this.groovyScript$mark = mark;
    }
}
