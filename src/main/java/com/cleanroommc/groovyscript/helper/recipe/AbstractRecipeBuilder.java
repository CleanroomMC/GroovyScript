package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.FluidStackList;
import com.cleanroommc.groovyscript.helper.IngredientList;
import com.cleanroommc.groovyscript.helper.ItemStackList;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;
import java.util.Collections;

public abstract class AbstractRecipeBuilder<T> implements IRecipeBuilder<T> {

    protected final IngredientList<IIngredient> input = new IngredientList<>();
    protected final ItemStackList output = new ItemStackList();
    protected final FluidStackList fluidInput = new FluidStackList();
    protected final FluidStackList fluidOutput = new FluidStackList();

    public AbstractRecipeBuilder<T> input(IIngredient ingredient) {
        this.input.add(ingredient);
        return this;
    }

    public AbstractRecipeBuilder<T> input(IIngredient... ingredients) {
        Collections.addAll(input, ingredients);
        return this;
    }

    public AbstractRecipeBuilder<T> input(Collection<IIngredient> ingredients) {
        this.input.addAll(ingredients);
        return this;
    }

    public AbstractRecipeBuilder<T> output(ItemStack output) {
        this.output.add(output);
        return this;
    }

    public AbstractRecipeBuilder<T> output(ItemStack... outputs) {
        Collections.addAll(output, outputs);
        return this;
    }

    public AbstractRecipeBuilder<T> output(Collection<ItemStack> outputs) {
        this.output.addAll(outputs);
        return this;
    }

    public AbstractRecipeBuilder<T> fluidInput(FluidStack ingredient) {
        this.fluidInput.add(ingredient);
        return this;
    }

    public AbstractRecipeBuilder<T> fluidInput(FluidStack... ingredients) {
        Collections.addAll(fluidInput, ingredients);
        return this;
    }

    public AbstractRecipeBuilder<T> fluidInput(Collection<FluidStack> ingredients) {
        this.fluidInput.addAll(ingredients);
        return this;
    }

    public AbstractRecipeBuilder<T> fluidOutput(FluidStack output) {
        this.fluidOutput.add(output);
        return this;
    }

    public AbstractRecipeBuilder<T> fluidOutput(FluidStack... outputs) {
        Collections.addAll(fluidOutput, outputs);
        return this;
    }

    public AbstractRecipeBuilder<T> fluidOutput(Collection<FluidStack> outputs) {
        this.fluidOutput.addAll(outputs);
        return this;
    }

    @Override
    public boolean validate() {
        GroovyLog.Msg msg = new GroovyLog.Msg(getErrorMsg()).error();
        validate(msg);
        return !msg.logIfNotEmpty();
    }

    public abstract String getErrorMsg();

    public abstract void validate(GroovyLog.Msg msg);

    public void validateFluids(GroovyLog.Msg msg, int minFluidInput, int maxFluidInput, int minFluidOutput, int maxFluidOutput) {
        fluidInput.trim();
        fluidOutput.trim();
        msg.add(fluidInput.size() < minFluidInput || fluidInput.size() > maxFluidInput, () -> getRequiredString(minFluidInput, maxFluidInput, "fluid input") + ", but found " + fluidInput.size());
        msg.add(fluidOutput.size() < minFluidOutput || fluidOutput.size() > maxFluidOutput, () -> getRequiredString(minFluidOutput, maxFluidOutput, "fluid output") + ", but found " + fluidOutput.size());
    }

    public void validateItems(GroovyLog.Msg msg, int minInput, int maxInput, int minOutput, int maxOutput) {
        input.trim();
        output.trim();
        msg.add(input.size() < minInput || input.size() > maxInput, () -> getRequiredString(minInput, maxInput, "input") + ", but found " + input.size());
        msg.add(output.size() < minOutput || output.size() > maxOutput, () -> getRequiredString(minOutput, maxOutput, "output") + ", but found " + output.size());
    }

    public void validateItems(GroovyLog.Msg msg) {
        validateItems(msg, 0, 0, 0, 0);
    }

    public void validateFluids(GroovyLog.Msg msg) {
        validateFluids(msg, 0, 0, 0, 0);
    }

    private static String getRequiredString(int min, int max, String type) {
        if (max <= 0) {
            return "No " + type + "s allowed";
        }
        String out = "Must have ";
        if (min == max) {
            out += "exactly " + min + " " + type;
        } else {
            out += min + " - " + max + " " + type;
        }
        if (max != 1) {
            out += "s";
        }
        return out;
    }
}
