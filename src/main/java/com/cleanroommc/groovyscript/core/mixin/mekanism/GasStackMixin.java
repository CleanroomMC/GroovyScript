package com.cleanroommc.groovyscript.core.mixin.mekanism;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IResourceStack;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GasStack.class, remap = false)
public abstract class GasStackMixin implements IIngredient, IResourceStack {

    @Shadow
    public int amount;

    @Shadow
    public abstract GasStack copy();

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public IIngredient exactCopy() {
        return (IIngredient) copy();
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.EMPTY;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return new ItemStack[0];
    }

    @Override
    public boolean test(ItemStack stack) {
        return false;
    }
}
