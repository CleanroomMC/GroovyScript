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
    protected Closure<Object> nbtMatcher = IngredientHelper.MATCH_ANY;
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
        if (!OreDictionary.itemMatches(groovyscript$getThis(), stack, false) ||
            (matchCondition != null && !ClosureHelper.call(true, matchCondition, stack))) {
            return false;
        }
        if (nbtMatcher != null) {
            NBTTagCompound nbt = getNbt();
            return nbt == null || nbt.isEmpty() || ClosureHelper.call(true, nbtMatcher, nbt);
        }
        return true;
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

    public ItemStack noreturn() {
        return transform(IngredientHelper.NO_RETURN);
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
        this.nbtMatcher = NbtHelper.makeNbtPredicate(nbt1 -> NbtHelper.containsNbt(nbt1, nbt));
        return this;
    }

    @Override
    public INbtIngredient withNbtExact(NBTTagCompound nbt) {
        setNbt(nbt);
        this.nbtMatcher = NbtHelper.makeNbtPredicate(nbt1 -> nbt1.equals(nbt));
        return this;
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

    public IIngredient copyWithMeta(int meta) {
        ItemStack t = groovyscript$getThis();
        ItemStackMixin itemStack = (ItemStackMixin) (Object) new ItemStack(t.getItem(), t.getCount(), meta);
        itemStack.setMark(getMark());
        itemStack.transform(transformer);
        itemStack.when(matchCondition);
        return itemStack;
    }

    public IIngredient copyWithDamage(int meta) {
        return copyWithMeta(meta);
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
