package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.helper.recipe.IIngredient;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import crazypants.enderio.base.recipe.IRecipeInput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class RecipeInput implements IRecipeInput {

    protected final Ingredient ing;

    public RecipeInput(IIngredient ingredient) {
        this(ingredient.toMcIngredient());
    }

    public RecipeInput(Ingredient ing) {
        this.ing = ing;
        if (ing.getMatchingStacks().length == 0) {
            GroovyLog.LOG.warn("EnderTweaker received an empty ingredient. This may cause problems.");
            GroovyLog.LOG.warn(ing.toString());
        }
    }

    @Nonnull
    @Override
    public IRecipeInput copy() {
        return new RecipeInput(ing);
    }

    @Override
    public boolean isFluid() {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getInput() {
        return ing.getMatchingStacks().length == 0 ? ItemStack.EMPTY : ing.getMatchingStacks()[0].copy();
    }

    @Override
    public FluidStack getFluidInput() {
        return null;
    }

    @Override
    public float getMulitplier() {
        return 0;
    }

    @Override
    public int getSlotNumber() {
        return -1;
    }

    @Override
    public boolean isInput(@Nonnull ItemStack test) {
        return ing.apply(test);
    }

    @Override
    public boolean isInput(FluidStack test) {
        return false;
    }

    @Override
    public ItemStack[] getEquivelentInputs() {
        ItemStack[] org = ing.getMatchingStacks();
        ItemStack[] copy = new ItemStack[org.length];
        for (int i = 0; i < copy.length; i++)
            copy[i] = org[i].copy();
        return copy;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void shrinkStack(int count) {
    }

    @Override
    public int getStackSize() {
        return ing.getMatchingStacks().length == 0 ? 0 : ing.getMatchingStacks()[0].getCount();
    }

}
