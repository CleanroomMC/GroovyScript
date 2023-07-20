package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

public abstract class IngredientBase implements IIngredient {

    protected Closure<Object> matchCondition;
    protected Closure<Object> transformer;

    public IngredientBase when(Closure<Object> matchCondition) {
        IngredientBase fresh = (IngredientBase) this.exactCopy();
        fresh.matchCondition = matchCondition;
        return fresh;
    }

    public IngredientBase transform(Closure<Object> transformer) {
        IngredientBase fresh = (IngredientBase) this.exactCopy();
        fresh.transformer = transformer;
        return fresh;
    }

    public IngredientBase reuse() {
        return transform(IngredientHelper.REUSE);
    }

    public IngredientBase noReturn() {
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
        return ForgeHooks.getContainerItem(matchedInput);
    }
}
