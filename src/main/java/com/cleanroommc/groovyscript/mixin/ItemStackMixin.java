package com.cleanroommc.groovyscript.mixin;

import com.cleanroommc.groovyscript.api.ICountable;
import com.cleanroommc.groovyscript.helper.recipe.IIngredient;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IIngredient {

    @Shadow private boolean isEmpty;
    @Shadow private int stackSize;

    @Shadow public abstract Item getItem();

    @Unique
    protected Closure<Object> matchCondition;
    @Unique
    protected Closure<Object> transformer;

    public ItemStack getThis() {
        return (ItemStack) (Object) this;
    }

    @Override
    public ICountable setCount(int amount) {
        getThis().setCount(amount);
        return this;
    }

    @Override
    public int getCount() {
        return isEmpty ? 0 : stackSize;
    }

    @Override
    public IIngredient exactCopy() {
        return (IIngredient) (Object) getThis().copy();
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(getThis());
    }

    @Override
    public boolean test(ItemStack stack) {
        return (matchCondition == null || ClosureHelper.call(true, matchCondition, stack)) && OreDictionary.itemMatches(getThis(), stack, false);
    }

    public ItemStack when(Closure<Object> matchCondition) {
        this.matchCondition = matchCondition;
        return getThis();
    }

    public ItemStack transform(Closure<Object> transformer) {
        this.transformer = transformer;
        return getThis();
    }

    @Override
    public ItemStack applyTransform(ItemStack matchedInput) {
        if (transformer != null) {
            return ClosureHelper.call(ItemStack.EMPTY, transformer, matchedInput);
        }
        return getItem().getContainerItem(matchedInput);
    }

    @Inject(method = "copy", at = @At("RETURN"), cancellable = true)
    public void copy(CallbackInfoReturnable<ItemStack> cir) {
        ItemStackMixin ingredient = (ItemStackMixin) (Object) cir.getReturnValue();
        ingredient.when(matchCondition);
        ingredient.transform(transformer);
        cir.setReturnValue((ItemStack) (Object) ingredient);
    }
}
