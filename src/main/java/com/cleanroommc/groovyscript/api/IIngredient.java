package com.cleanroommc.groovyscript.api;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface IIngredient extends IResourceStack, Predicate<ItemStack> {

    IIngredient exactCopy();

    Ingredient toMcIngredient();

    ItemStack[] getMatchingStacks();

    default ItemStack applyTransform(ItemStack matchedInput) {
        return matchedInput.getItem().hasContainerItem(matchedInput) ? matchedInput.getItem().getContainerItem(matchedInput) : ItemStack.EMPTY;
    }

    default boolean test(FluidStack fluidStack) {
        return false;
    }

    String asGroovyCode();

    /**
     * An empty ingredient with stack size 0, that matches empty item stacks
     */
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
        public ItemStack[] getMatchingStacks() {
            return new ItemStack[]{ItemStack.EMPTY};
        }

        @Override
        public boolean test(ItemStack stack) {
            return stack.isEmpty();
        }

        @Override
        public String asGroovyCode() {
            return "<empty>";
        }
    };

    /**
     * An ingredient with stack size 1, that matches any item stack
     */
    IIngredient ANY = new IIngredient() {

        @Override
        public IIngredient exactCopy() {
            return this;
        }

        @Override
        public Ingredient toMcIngredient() {
            return new Ingredient() {
                @Override
                public boolean apply(@Nullable ItemStack p_apply_1_) {
                    return true;
                }
            };
        }

        @Override
        public ItemStack[] getMatchingStacks() {
            return new ItemStack[0];
        }

        @Override
        public int getAmount() {
            return 1;
        }

        @Override
        public void setAmount(int amount) {
        }

        @Override
        public boolean test(ItemStack stack) {
            return true;
        }

        @Override
        public String asGroovyCode() {
            return "<any>";
        }
    };
}
