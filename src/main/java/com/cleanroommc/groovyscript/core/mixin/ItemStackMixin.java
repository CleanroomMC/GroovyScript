package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IMarkable;
import com.cleanroommc.groovyscript.api.INBTResourceStack;
import com.cleanroommc.groovyscript.api.INbtIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.NbtHelper;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Predicate;

@Mixin(value = ItemStack.class)
public abstract class ItemStackMixin implements IIngredient, INbtIngredient, IMarkable {

    @Unique
    protected Closure<Object> matchCondition;
    @Unique
    protected Closure<Object> transformer;
    @Unique
    protected Predicate<NBTTagCompound> nbtMatcher = NbtHelper.MATCH_ANY;
    @Unique
    protected String mark;

    private ItemStack groovyscript$getThis() {
        return (ItemStack) (Object) this;
    }

    @Override
    public int getAmount() {
        return groovyscript$getThis().getCount();
    }

    @Override
    public void setAmount(int amount) {
        groovyscript$getThis().setCount(amount);
    }

    @Override
    public @Nullable NBTTagCompound getNbt() {
        return groovyscript$getThis().getTagCompound();
    }

    @Override
    public void setNbt(NBTTagCompound nbt) {
        groovyscript$getThis().setTagCompound(nbt);
    }

    @Override
    public IIngredient exactCopy() {
        ItemStackMixin copy = (ItemStackMixin) (Object) groovyscript$getThis().copy();
        copy.setMark(getMark());
        copy.transform(transformer);
        copy.when(matchCondition);
        copy.withNbtFilter(nbtMatcher);
        return copy;
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(groovyscript$getThis());
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return new ItemStack[]{groovyscript$getThis().copy()};
    }

    @Override
    public boolean test(ItemStack stack) {
        return (matchCondition == null || ClosureHelper.call(true, matchCondition, stack)) &&
               OreDictionary.itemMatches(groovyscript$getThis(), stack, false) &&
               this.nbtMatcher.test(stack.getTagCompound());
    }

    public ItemStack when(Closure<Object> matchCondition) {
        this.matchCondition = matchCondition;
        return groovyscript$getThis();
    }

    public ItemStack transform(Closure<Object> transformer) {
        this.transformer = transformer;
        return groovyscript$getThis();
    }

    public ItemStack reuse() {
        return transform(IngredientHelper.REUSE);
    }

    @Override
    public ItemStack applyTransform(ItemStack matchedInput) {
        if (transformer != null) {
            return ClosureHelper.call(ItemStack.EMPTY, transformer, matchedInput).copy();
        }
        return groovyscript$getThis().getItem().getContainerItem(matchedInput);
    }

    @Nullable
    @Override
    public String getMark() {
        return mark;
    }

    @Override
    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public INBTResourceStack withNbt(NBTTagCompound nbt) {
        setNbt(nbt);
        this.nbtMatcher = nbt1 -> NbtHelper.containsNbt(nbt1, nbt);
        return this;
    }

    @Override
    public INbtIngredient withNbtExact(NBTTagCompound nbt) {
        setNbt(nbt);
        this.nbtMatcher = nbt1 -> nbt1.equals(nbt);
        return this;
    }

    @Override
    public INbtIngredient withNbtFilter(Predicate<NBTTagCompound> nbtFilter) {
        this.nbtMatcher = nbtFilter == null ? nbt -> true : nbtFilter;
        return this;
    }

    public void addOreDict(OreDictIngredient ingredient) {
        VanillaModule.oreDict.add(ingredient.getOreDict(), groovyscript$getThis());
    }

    public void removeOreDict(OreDictIngredient ingredient) {
        VanillaModule.oreDict.remove(ingredient.getOreDict(), groovyscript$getThis());
    }

    public boolean isCase(OreDictIngredient ingredient) {
        ItemStack itemStack = groovyscript$getThis();
        for (ItemStack stack : OreDictionary.getOres(ingredient.getOreDict())) {
            if (OreDictionary.itemMatches(itemStack, stack, false)) {
                return true;
            }
        }
        return false;
    }
}
