package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;

public abstract class IngredientBase implements IIngredient {

    protected Closure<Object> matchCondition;
    protected Closure<Object> transformer;

    public IngredientBase when(Closure<Object> matchCondition) {
        this.matchCondition = matchCondition;
        return this;
    }

    public IngredientBase transform(Closure<Object> transformer) {
        this.transformer = transformer;
        return this;
    }

    public IngredientBase reuse() {
        return transform(IngredientHelper.REUSE);
    }

    public IngredientBase noreturn() {
        return transform(IngredientHelper.NO_RETURN);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return (matchCondition == null || ClosureHelper.call(true, matchCondition, itemStack)) && matches(itemStack);
    }

    public abstract boolean matches(ItemStack itemStack);

    @Override
    public ItemStack applyTransform(ItemStack matchedInput) {
        if (transformer != null) {
            return ClosureHelper.call(ItemStack.EMPTY, transformer, matchedInput);
        }
        return ItemStack.EMPTY;
    }
}
