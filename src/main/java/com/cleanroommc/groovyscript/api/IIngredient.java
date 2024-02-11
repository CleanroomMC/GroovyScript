package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.helper.ingredient.OrIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Base ingredient class for every ingredient. Most useful for item stacks and ore dicts.
 */
public interface IIngredient extends IResourceStack, Predicate<ItemStack> {

    @Override
    IIngredient exactCopy();

    Ingredient toMcIngredient();

    ItemStack[] getMatchingStacks();

    default boolean isEmpty() {
        return getAmount() <= 0 || getMatchingStacks().length == 0;
    }

    default ItemStack applyTransform(ItemStack matchedInput) {
        return ForgeHooks.getContainerItem(matchedInput);
    }

    default boolean test(FluidStack fluidStack) {
        return false;
    }

    default IIngredient or(IIngredient ingredient) {
        OrIngredient orIngredient = new OrIngredient();
        orIngredient.addIngredient(this);
        orIngredient.addIngredient(ingredient);
        return orIngredient;
    }

    // >> operator
    default boolean rightShift(ItemStack ingredient) {
        return isCase(ingredient) && getAmount() >= ingredient.getCount();
    }

    // in operator
    // item('minecraft:iron_ingot') in ore('ingotIron') // true
    default boolean isCase(ItemStack ingredient) {
        return ingredient != null && test(ingredient);
    }

    @Override
    default IIngredient withAmount(int amount) {
        IIngredient iIngredientStack = exactCopy();
        iIngredientStack.setAmount(amount);
        return iIngredientStack;
    }

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
        public boolean isEmpty() {
            return true;
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
        public boolean isEmpty() {
            return false;
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
    };
}
