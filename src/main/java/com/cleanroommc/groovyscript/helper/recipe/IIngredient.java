package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.api.ICountable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.function.Predicate;

public interface IIngredient extends ICountable, Predicate<ItemStack> {

    IIngredient exactCopy();

    Ingredient toMcIngredient();

    IIngredient EMPTY = new IIngredient() {
        @Override
        public IIngredient exactCopy() {
            return this;
        }

        @Override
        public Ingredient toMcIngredient() {
            return Ingredient.EMPTY;
        }

        @Override
        public IIngredient setCount(int amount) {
            return this;
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean test(ItemStack stack) {
            return true;
        }
    };
}
