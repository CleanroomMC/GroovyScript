package com.cleanroommc.groovyscript.compat.mods.enderio.recipe;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import crazypants.enderio.base.recipe.IRecipeInput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class RecipeInput implements IRecipeInput {

    protected final IIngredient ing;
    protected final int slot;

    public RecipeInput(IIngredient ing) {
        this(ing, -1);
    }

    public RecipeInput(IIngredient ing, int slot) {
        this.ing = IngredientHelper.isEmpty(ing) ? IIngredient.EMPTY : ing.exactCopy();
        this.slot = slot;
        if (this.ing.getMatchingStacks().length == 0) {
            GroovyLog.get().warn("EnderTweaker received an empty ingredient. This may cause problems. Ingredient: ", this.ing);
        }
    }

    @Override
    public @NotNull IRecipeInput copy() {
        return new RecipeInput(ing);
    }

    @Override
    public boolean isFluid() {
        return ing instanceof FluidStack;
    }

    @Override
    public @NotNull ItemStack getInput() {
        return ing.getMatchingStacks().length == 0 ? ItemStack.EMPTY : ing.getMatchingStacks()[0].copy();
    }

    @Override
    public FluidStack getFluidInput() {
        return ing instanceof FluidStack fluidStack ? fluidStack.copy() : null;
    }

    @Override
    public float getMulitplier() {
        return 1;
    }

    @Override
    public int getSlotNumber() {
        return slot;
    }

    @Override
    public boolean isInput(@NotNull ItemStack test) {
        return ing.test(test);
    }

    @Override
    public boolean isInput(FluidStack test) {
        return test != null && ing.test(test);
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
        ing.setAmount(ing.getAmount() - count);
    }

    @Override
    public int getStackSize() {
        return ing.getAmount();
    }
}
