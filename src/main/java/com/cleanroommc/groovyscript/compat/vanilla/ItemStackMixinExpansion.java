package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.*;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.NbtHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface ItemStackMixinExpansion extends IIngredient, INbtIngredient {

    static ItemStackMixinExpansion of(ItemStack stack) {
        return (ItemStackMixinExpansion) (Object) stack;
    }

    ItemStack grs$getItemStack();

    @Nullable
    ItemStackTransformer grs$getTransformer();

    @Nullable
    Predicate<NBTTagCompound> grs$getNbtMatcher();

    @Nullable
    Predicate<ItemStack> grs$getMatcher();

    void grs$setTransformer(ItemStackTransformer transformer);

    @GroovyBlacklist
    void grs$setNbtMatcher(Predicate<NBTTagCompound> matcher);

    @GroovyBlacklist
    void grs$setMatcher(Predicate<ItemStack> matcher);

    @Nullable
    String grs$getMark();

    void grs$setMark(String mark);

    @GroovyBlacklist
    default boolean grs$isEmpty() {
        return grs$getItemStack().isEmpty();
    }

    @Override
    default ItemStackMixinExpansion exactCopy() {
        if (grs$isEmpty()) {
            return (ItemStackMixinExpansion) (Object) ItemStack.EMPTY;
        }
        ItemStackMixinExpansion copy = of(grs$getItemStack().copy());
        copy.setMark(getMark());
        copy.grs$setTransformer(grs$getTransformer());
        copy.grs$setMatcher(grs$getMatcher());
        copy.grs$setNbtMatcher(grs$getNbtMatcher());
        return copy;
    }

    @Override
    default boolean test(ItemStack stack) {
        if (!OreDictionary.itemMatches(grs$getItemStack(), stack, false) && (grs$getMatcher() == null || !grs$getMatcher().test(stack))) {
            return false;
        }
        if (grs$getNbtMatcher() != null) {
            NBTTagCompound nbt = stack.getTagCompound();
            return nbt != null && grs$getNbtMatcher().test(nbt);
        }
        return true;
    }

    default ItemStack when(Predicate<ItemStack> matchCondition) {
        ItemStackMixinExpansion fresh = exactCopy();
        fresh.grs$setMatcher(matchCondition);
        return fresh.grs$getItemStack();
    }

    default ItemStack whenAnyDamage() {
        return when(stack -> stack.getItem() == grs$getItemStack().getItem());
    }

    default ItemStack whenAnyMeta() {
        return whenAnyDamage();
    }

    default ItemStack transform(ItemStackTransformer transformer) {
        ItemStackMixinExpansion fresh = exactCopy();
        fresh.grs$setTransformer(transformer);
        return fresh.grs$getItemStack();
    }

    default ItemStack reuse() {
        return transform(self -> self);
    }

    default ItemStack noReturn() {
        return transform(self -> ItemStack.EMPTY);
    }

    default ItemStack transform(ItemStack stack) {
        return transform(self -> stack);
    }

    default ItemStack transformDamage(int amount) {
        // reliably set itemDamage field of item stack
        return transform(self -> IngredientHelper.damageItem(self, amount));
    }

    default ItemStack transformDamage() {
        return transformDamage(1);
    }

    default ItemStack transformNbt(UnaryOperator<NBTTagCompound> transformer) {
        return transform(self -> {
            of(self).exactCopy().grs$getItemStack().setTagCompound(transformer.apply(self.getTagCompound()));
            return self;
        });
    }

    @Override
    default ItemStack applyTransform(ItemStack matchedInput) {
        if (grs$getTransformer() != null) {
            ItemStack result = grs$getTransformer().transform(matchedInput);
            if (result == null || result.isEmpty()) return ItemStack.EMPTY;
            if (result.isItemStackDamageable() && result.getMetadata() > result.getMaxDamage()) {
                ForgeEventFactory.onPlayerDestroyItem(ForgeHooks.getCraftingPlayer(), result, null);
                return ItemStack.EMPTY;
            }
            return result.copy();
        }
        return ForgeHooks.getContainerItem(matchedInput);
    }

    @Override
    default Ingredient toMcIngredient() {
        return Ingredient.fromStacks(grs$getItemStack());
    }

    @Override
    default ItemStack[] getMatchingStacks() {
        return new ItemStack[]{
                IngredientHelper.toItemStack(exactCopy())
        };
    }

    @Override
    default int getAmount() {
        return grs$getItemStack().getCount();
    }

    @Override
    default void setAmount(int amount) {
        grs$getItemStack().setCount(amount);
    }


    @Override
    default @Nullable NBTTagCompound getNbt() {
        return grs$getItemStack().getTagCompound();
    }

    @Override
    default void setNbt(NBTTagCompound nbt) {
        grs$getItemStack().setTagCompound(nbt);
    }

    @Override
    default INBTResourceStack withNbt(NBTTagCompound nbt) {
        ItemStackMixinExpansion itemStackMixin = (ItemStackMixinExpansion) INbtIngredient.super.withNbt(nbt);
        itemStackMixin.grs$setNbtMatcher(nbt1 -> nbt.isEmpty() || NbtHelper.containsNbt(nbt1, nbt));
        return itemStackMixin;
    }

    @Override
    default INbtIngredient withNbtExact(NBTTagCompound nbt) {
        ItemStackMixinExpansion itemStackMixin = (ItemStackMixinExpansion) INbtIngredient.super.withNbt(nbt);
        if (nbt == null) {
            itemStackMixin.grs$setNbtMatcher(null);
        } else {
            itemStackMixin.grs$setNbtMatcher(nbt1 -> nbt.isEmpty() || nbt1.equals(nbt));
        }
        return itemStackMixin;
    }

    default INbtIngredient withNbtFilter(Predicate<NBTTagCompound> nbtFilter) {
        this.grs$setNbtMatcher(nbtFilter == null ? nbt -> true : nbtFilter);
        return this;
    }

    default INbtIngredient whenNoNbt() {
        setNbt(null);
        grs$setMatcher(self -> {
            NBTTagCompound nbt = self.getTagCompound();
            return nbt == null || nbt.isEmpty();
        });
        return this;
    }

    default INbtIngredient whenAnyNbt() {
        setNbt(new NBTTagCompound());
        grs$setMatcher(self -> {
            NBTTagCompound nbt = self.getTagCompound();
            return nbt != null && !nbt.isEmpty();
        });
        return this;
    }

    default ItemStack withMeta(int meta) {
        ItemStack itemStack = exactCopy().grs$getItemStack();
        // reliably set itemDamage field of item stack
        Items.DIAMOND.setDamage(itemStack, meta);
        return itemStack;
    }

    default ItemStack withDamage(int meta) {
        return withMeta(meta);
    }

    default ItemStack destroy() {
        if (grs$getItemStack().isEmpty()) return ItemStack.EMPTY;
        EntityPlayer player = ForgeHooks.getCraftingPlayer();
        if (player != null) {
            ForgeEventFactory.onPlayerDestroyItem(player, grs$getItemStack(), null);
            return ItemStack.EMPTY;
        }
        GroovyLog.get().error("Should only destroy item during crafting!");
        return grs$getItemStack();
    }

    @Override
    default @Nullable String getMark() {
        return grs$getMark();
    }

    @Override
    default void setMark(String mark) {
        grs$setMark(mark);
    }
}
