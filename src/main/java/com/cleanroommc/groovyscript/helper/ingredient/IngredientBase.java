package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.ItemStackMixinExpansion;
import com.cleanroommc.groovyscript.compat.vanilla.ItemStackTransformer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class IngredientBase implements IIngredient {

    protected Predicate<ItemStack> matchCondition;
    protected ItemStackTransformer transformer;
    protected String mark;

    public IngredientBase when(Predicate<ItemStack> matchCondition) {
        IngredientBase fresh = (IngredientBase) this.exactCopy();
        fresh.matchCondition = matchCondition;
        return fresh;
    }

    public IngredientBase transform(ItemStackTransformer transformer) {
        IngredientBase fresh = (IngredientBase) this.exactCopy();
        fresh.transformer = transformer;
        return fresh;
    }

    public IngredientBase transformDamage(int amount) {
        // reliably set itemDamage field of item stack
        return transform(self -> IngredientHelper.damageItem(self, amount));
    }

    public IngredientBase transformDamage() {
        return transformDamage(1);
    }

    public IngredientBase transformNbt(UnaryOperator<NBTTagCompound> transformer) {
        return transform(self -> {
            ItemStackMixinExpansion.of(self).exactCopy().grs$getItemStack().setTagCompound(transformer.apply(self.getTagCompound()));
            return self;
        });
    }

    public IngredientBase reuse() {
        return transform(self -> self);
    }

    public IngredientBase noReturn() {
        return transform(self -> ItemStack.EMPTY);
    }

    public IngredientBase transform(ItemStack stack) {
        return transform(self -> stack);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return (this.matchCondition == null || this.matchCondition.test(itemStack)) && matches(itemStack);
    }

    public abstract boolean matches(ItemStack itemStack);

    @Override
    public ItemStack applyTransform(ItemStack matchedInput) {
        if (this.transformer != null) {
            ItemStack result = this.transformer.transform(matchedInput);
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
    public @Nullable String getMark() {
        return mark;
    }

    @Override
    public void setMark(String mark) {
        this.mark = mark;
    }
}
