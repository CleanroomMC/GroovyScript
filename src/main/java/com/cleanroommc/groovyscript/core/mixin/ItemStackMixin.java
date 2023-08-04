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
import com.cleanroommc.groovyscript.sandbox.expand.LambdaClosure;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ItemStack.class)
public abstract class ItemStackMixin implements IIngredient, INbtIngredient, IMarkable {

    @Unique
    protected Closure<Object> matchCondition;
    @Unique
    protected Closure<Object> transformer;
    @Unique
    protected Closure<Object> nbtMatcher = null;
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
        copy.transformer = transformer;
        copy.matchCondition = matchCondition;
        copy.nbtMatcher = nbtMatcher;
        return copy;
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(groovyscript$getThis());
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return new ItemStack[]{IngredientHelper.toItemStack(exactCopy())};
    }

    @Override
    public boolean test(ItemStack stack) {
        if (!OreDictionary.itemMatches(groovyscript$getThis(), stack, false) ||
            (matchCondition != null && !ClosureHelper.call(true, matchCondition, stack))) {
            return false;
        }
        if (nbtMatcher != null) {
            NBTTagCompound nbt = stack.getTagCompound();
            return nbt != null && ClosureHelper.call(true, nbtMatcher, nbt);
        }
        return true;
    }

    public ItemStack when(Closure<Object> matchCondition) {
        ItemStackMixin fresh = (ItemStackMixin) exactCopy();
        fresh.matchCondition = matchCondition;
        return (ItemStack) (Object) fresh;
    }

    public ItemStack transform(Closure<Object> transformer) {
        ItemStackMixin fresh = (ItemStackMixin) exactCopy();
        fresh.transformer = transformer;
        return (ItemStack) (Object) fresh;
    }

    public ItemStack reuse() {
        return transform(IngredientHelper.REUSE);
    }

    public ItemStack noreturn() {
        return transform(IngredientHelper.NO_RETURN);
    }

    @Override
    public ItemStack applyTransform(ItemStack matchedInput) {
        if (transformer != null) {
            return ClosureHelper.call(ItemStack.EMPTY, transformer, matchedInput).copy();
        }
        return ForgeHooks.getContainerItem(matchedInput);
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
        ItemStackMixin itemStackMixin = (ItemStackMixin) INbtIngredient.super.withNbt(nbt);
        itemStackMixin.nbtMatcher = NbtHelper.makeNbtPredicate(nbt1 -> nbt.isEmpty() || NbtHelper.containsNbt(nbt1, nbt));
        return itemStackMixin;
    }

    @Override
    public INbtIngredient withNbtExact(NBTTagCompound nbt) {
        ItemStackMixin itemStackMixin = (ItemStackMixin) INbtIngredient.super.withNbt(nbt);
        if (nbt == null) {
            itemStackMixin.nbtMatcher = null;
        } else {
            itemStackMixin.nbtMatcher = NbtHelper.makeNbtPredicate(nbt1 -> nbt.isEmpty() || nbt1.equals(nbt));
        }
        return itemStackMixin;
    }

    public INbtIngredient withNbtFilter(Closure<Object> nbtFilter) {
        this.nbtMatcher = nbtFilter == null ? IngredientHelper.MATCH_ANY : nbtFilter;
        return this;
    }

    public INbtIngredient whenNoNbt() {
        setNbt(null);
        this.matchCondition = new LambdaClosure<>(args -> {
            NBTTagCompound nbt = ((ItemStack) args[0]).getTagCompound();
            return nbt == null || nbt.isEmpty();
        });
        return this;
    }

    public INbtIngredient whenAnyNbt() {
        setNbt(new NBTTagCompound());
        this.matchCondition = new LambdaClosure<>(args -> {
            NBTTagCompound nbt = ((ItemStack) args[0]).getTagCompound();
            return nbt != null && !nbt.isEmpty();
        });
        return this;
    }

    public IIngredient withMeta(int meta) {
        ItemStack t = groovyscript$getThis();
        ItemStackMixin itemStack = (ItemStackMixin) (Object) new ItemStack(t.getItem(), t.getCount(), meta);
        itemStack.setMark(getMark());
        itemStack.transformer = transformer;
        itemStack.matchCondition = matchCondition;
        itemStack.nbtMatcher = nbtMatcher;
        return itemStack;
    }

    public IIngredient withDamage(int meta) {
        return withMeta(meta);
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
