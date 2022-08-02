package com.cleanroommc.groovyscript.mixin;

import com.cleanroommc.groovyscript.api.INBTResourceStack;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FluidStack.class, remap = false)
public abstract class FluidStackMixin implements IIngredient, INBTResourceStack {

    @Shadow
    public int amount;

    @Shadow
    public NBTTagCompound tag;

    @Shadow
    public abstract FluidStack copy();

    @Shadow
    public abstract boolean isFluidEqual(FluidStack other);

    @Unique
    protected Closure<Object> matchCondition;
    @Unique
    protected Closure<Object> transformer;

    @Override
    public IIngredient exactCopy() {
        return (IIngredient) copy();
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
        this.matchCondition = matchCondition;
        return (FluidStack) (Object) this;
    }

    public FluidStack transform(Closure<Object> transformer) {
        this.transformer = transformer;
        return (FluidStack) (Object) this;
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

    @Inject(method = "copy", at = @At("RETURN"), cancellable = true)
    public void injectCopy(CallbackInfoReturnable<FluidStack> cir) {
        FluidStackMixin fluid = (FluidStackMixin) (Object) cir.getReturnValue();
        fluid.when(matchCondition);
        fluid.transform(transformer);
        cir.setReturnValue((FluidStack) (Object) fluid);
    }
}
