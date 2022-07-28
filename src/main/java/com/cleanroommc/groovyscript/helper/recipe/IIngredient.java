package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.api.IResourceStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.function.Predicate;

public interface IIngredient extends IResourceStack, Predicate<ItemStack> {

    IIngredient exactCopy();

    Ingredient toMcIngredient();

    default ItemStack applyTransform(ItemStack matchedInput) {
        return matchedInput.getItem().hasContainerItem(matchedInput) ? matchedInput.getItem().getContainerItem(matchedInput) : ItemStack.EMPTY;
    }

    IIngredient EMPTY = new IIngredient() {
        @Override
        public int getAmount() {
            return 0;
        }

        @Override
        public void setAmount(int amount) {
        }

        @Override
        public IIngredient exactCopy() {
            return this;
        }

        @Override
        public Ingredient toMcIngredient() {
            return Ingredient.EMPTY;
        }

        @Override
        public boolean test(ItemStack stack) {
            return stack.isEmpty();
        }
    };
}
