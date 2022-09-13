package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.INBTResourceStack;
import com.cleanroommc.groovyscript.api.INbtIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.NbtHelper;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(value = ItemStack.class, remap = false)
public abstract class ItemStackMixin implements IIngredient, INbtIngredient {

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract int getCount();

    @Shadow
    public abstract void setCount(int size);

    @Shadow
    private NBTTagCompound stackTagCompound;

    @Shadow
    public abstract ItemStack copy();

    @Shadow
    public abstract int getMetadata();

    @Unique
    protected Closure<Object> matchCondition;
    @Unique
    protected Closure<Object> transformer;
    @Unique
    protected Predicate<NBTTagCompound> nbtMatcher = NbtHelper.MATCH_ANY;

    private ItemStack groovyscript$getThis() {
        return (ItemStack) (Object) this;
    }

    @Override
    public int getAmount() {
        return getCount();
    }

    @Override
    public void setAmount(int amount) {
        setCount(amount);
    }

    @Override
    public @Nullable NBTTagCompound getNbt() {
        return stackTagCompound;
    }

    @Override
    public void setNbt(NBTTagCompound nbt) {
        stackTagCompound = nbt;
    }

    @Override
    public IIngredient exactCopy() {
        return (IIngredient) (Object) copy();
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(groovyscript$getThis());
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return new ItemStack[]{copy()};
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

    @Override
    public ItemStack applyTransform(ItemStack matchedInput) {
        if (transformer != null) {
            return ClosureHelper.call(ItemStack.EMPTY, transformer, matchedInput);
        }
        return getItem().getContainerItem(matchedInput);
    }

    @Inject(method = "copy", at = @At("RETURN"), cancellable = true)
    public void injectCopy(CallbackInfoReturnable<ItemStack> cir) {
        ItemStackMixin ingredient = (ItemStackMixin) (Object) cir.getReturnValue();
        ingredient.when(matchCondition);
        ingredient.transform(transformer);
        cir.setReturnValue((ItemStack) (Object) ingredient);
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
}
