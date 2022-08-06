package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import crazypants.enderio.base.recipe.IRecipeInput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class RecipeInput implements IRecipeInput {

    protected final IIngredient ing;
    protected final int slot;

    public RecipeInput(IIngredient ing) {
        this(ing, -1);
    }

    public RecipeInput(IIngredient ing, int slot) {
        this.ing = ing == null ? IIngredient.EMPTY : ing.exactCopy();
        this.slot = slot;
        if (this.ing.getMatchingStacks().length == 0) {
            GroovyLog.LOG.warn("EnderTweaker received an empty ingredient. This may cause problems.");
            GroovyLog.LOG.warn(this.ing.toString());
        }
    }

    @Nonnull
    @Override
    public IRecipeInput copy() {
        return new RecipeInput(ing);
    }

    @Override
    public boolean isFluid() {
        return ing instanceof FluidStack;
    }

    @Nonnull
    @Override
    public ItemStack getInput() {
        return ing.getMatchingStacks().length == 0 ? ItemStack.EMPTY : ing.getMatchingStacks()[0].copy();
    }

    @Override
    public FluidStack getFluidInput() {
        return ing instanceof FluidStack ? ((FluidStack) ing).copy() : null;
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
    public boolean isInput(@Nonnull ItemStack test) {
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
