package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.FluidStackList;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientList;
import com.cleanroommc.groovyscript.helper.ingredient.ItemStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

public abstract class AbstractRecipeBuilder<T> implements IRecipeBuilder<T> {

    protected ResourceLocation name;
    protected final IngredientList<IIngredient> input = new IngredientList<>();
    protected final ItemStackList output = new ItemStackList();
    protected final FluidStackList fluidInput = new FluidStackList();
    protected final FluidStackList fluidOutput = new FluidStackList();

    public String getRecipeNamePrefix() {
        return "groovyscript_";
    }

    public AbstractRecipeBuilder<T> name(String name) {
        this.name = new ResourceLocation(GroovyScript.getRunConfig().getPackId(), name);
        return this;
    }

    public AbstractRecipeBuilder<T> name(ResourceLocation name) {
        this.name = name;
        return this;
    }

    public AbstractRecipeBuilder<T> input(IIngredient ingredient) {
        this.input.add(ingredient);
        return this;
    }

    public AbstractRecipeBuilder<T> input(IIngredient... ingredients) {
        for (IIngredient ingredient : ingredients) {
            input(ingredient);
        }
        return this;
    }

    public AbstractRecipeBuilder<T> input(Collection<IIngredient> ingredients) {
        for (IIngredient ingredient : ingredients) {
            input(ingredient);
        }
        return this;
    }

    public AbstractRecipeBuilder<T> output(ItemStack output) {
        this.output.add(output);
        return this;
    }

    public AbstractRecipeBuilder<T> output(ItemStack... outputs) {
        for (ItemStack output : outputs) {
            output(output);
        }
        return this;
    }

    public AbstractRecipeBuilder<T> output(Collection<ItemStack> outputs) {
        for (ItemStack output : outputs) {
            output(output);
        }
        return this;
    }

    public AbstractRecipeBuilder<T> fluidInput(FluidStack ingredient) {
        this.fluidInput.add(ingredient);
        return this;
    }

    public AbstractRecipeBuilder<T> fluidInput(FluidStack... ingredients) {
        for (FluidStack ingredient : ingredients) {
            fluidInput(ingredient);
        }
        return this;
    }

    public AbstractRecipeBuilder<T> fluidInput(Collection<FluidStack> ingredients) {
        for (FluidStack ingredient : ingredients) {
            fluidInput(ingredient);
        }
        return this;
    }

    public AbstractRecipeBuilder<T> fluidOutput(FluidStack output) {
        this.fluidOutput.add(output);
        return this;
    }

    public AbstractRecipeBuilder<T> fluidOutput(FluidStack... outputs) {
        for (FluidStack output : outputs) {
            fluidOutput(output);
        }
        return this;
    }

    public AbstractRecipeBuilder<T> fluidOutput(Collection<FluidStack> outputs) {
        for (FluidStack output : outputs) {
            fluidOutput(output);
        }
        return this;
    }

    @Override
    public boolean validate() {
        GroovyLog.Msg msg = GroovyLog.msg(getErrorMsg()).error();
        validate(msg);
        return !msg.postIfNotEmpty();
    }

    public abstract String getErrorMsg();

    public abstract void validate(GroovyLog.Msg msg);

    public void validateName() {
        if (name == null) {
            name = new ResourceLocation(GroovyScript.getRunConfig().getPackId(), RecipeName.generate(getRecipeNamePrefix()));
        }
    }

    public void validateFluids(GroovyLog.Msg msg, int minFluidInput, int maxFluidInput, int minFluidOutput, int maxFluidOutput) {
        fluidInput.trim();
        fluidOutput.trim();
        msg.add(fluidInput.size() < minFluidInput || fluidInput.size() > maxFluidInput, () -> getRequiredString(minFluidInput, maxFluidInput, "fluid input") + ", but found " + fluidInput.size());
        msg.add(fluidOutput.size() < minFluidOutput || fluidOutput.size() > maxFluidOutput, () -> getRequiredString(minFluidOutput, maxFluidOutput, "fluid output") + ", but found " + fluidOutput.size());
    }

    public void validateItems(GroovyLog.Msg msg, int minInput, int maxInput, int minOutput, int maxOutput) {
        input.trim();
        output.trim();
        msg.add(input.size() < minInput || input.size() > maxInput, () -> getRequiredString(minInput, maxInput, "item input") + ", but found " + input.size());
        msg.add(output.size() < minOutput || output.size() > maxOutput, () -> getRequiredString(minOutput, maxOutput, "item output") + ", but found " + output.size());
    }

    public void validateItems(GroovyLog.Msg msg) {
        validateItems(msg, 0, 0, 0, 0);
    }

    public void validateFluids(GroovyLog.Msg msg) {
        validateFluids(msg, 0, 0, 0, 0);
    }

    protected static String getRequiredString(int min, int max, String type) {
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
