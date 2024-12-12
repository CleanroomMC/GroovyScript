package com.cleanroommc.groovyscript.core.mixin.mekanism;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IResourceStack;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = GasStack.class, remap = false)
public abstract class GasStackMixin implements IIngredient, IResourceStack {

    @Shadow
    public int amount;

    @Shadow
    public abstract GasStack copy();

    @Unique
    protected String groovyScript$mark;

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

    @Override
    public @Nullable String getMark() {
        return groovyScript$mark;
    }

    @Override
    public void setMark(String mark) {
        this.groovyScript$mark = mark;
    }
}
