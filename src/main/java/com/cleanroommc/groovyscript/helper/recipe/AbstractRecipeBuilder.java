package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.helper.ingredient.FluidStackList;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientList;
import com.cleanroommc.groovyscript.helper.ingredient.ItemStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

public abstract class AbstractRecipeBuilder<T> implements IRecipeBuilder<T> {

    @Property(value = "groovyscript.wiki.name.value", needsOverride = true, priority = 100, hierarchy = 20)
    protected ResourceLocation name;
    @Property(value = "groovyscript.wiki.input.value", needsOverride = true, priority = 200, hierarchy = 20)
    protected final IngredientList<IIngredient> input = new IngredientList<>();
    @Property(value = "groovyscript.wiki.output.value", needsOverride = true, priority = 700, hierarchy = 20)
    protected final ItemStackList output = new ItemStackList();
    @Property(value = "groovyscript.wiki.fluidInput.value", needsOverride = true, priority = 250, hierarchy = 20)
    protected final FluidStackList fluidInput = new FluidStackList();
    @Property(value = "groovyscript.wiki.fluidOutput.value", needsOverride = true, priority = 750, hierarchy = 20)
    protected final FluidStackList fluidOutput = new FluidStackList();

    public String getRecipeNamePrefix() {
        return "groovyscript_";
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> name(String name) {
        if (name.contains(":")) {
            this.name = new ResourceLocation(name);
        } else {
            this.name = new ResourceLocation(GroovyScript.getRunConfig().getPackId(), name);
        }
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> name(ResourceLocation name) {
        this.name = name;
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> input(IIngredient ingredient) {
        this.input.add(ingredient);
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> input(IIngredient... ingredients) {
        for (IIngredient ingredient : ingredients) {
            input(ingredient);
        }
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> input(Collection<IIngredient> ingredients) {
        for (IIngredient ingredient : ingredients) {
            input(ingredient);
        }
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> output(ItemStack output) {
        this.output.add(output);
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> output(ItemStack... outputs) {
        for (ItemStack output : outputs) {
            output(output);
        }
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> output(Collection<ItemStack> outputs) {
        for (ItemStack output : outputs) {
            output(output);
        }
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> fluidInput(FluidStack ingredient) {
        this.fluidInput.add(ingredient);
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> fluidInput(FluidStack... ingredients) {
        for (FluidStack ingredient : ingredients) {
            fluidInput(ingredient);
        }
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> fluidInput(Collection<FluidStack> ingredients) {
        for (FluidStack ingredient : ingredients) {
            fluidInput(ingredient);
        }
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> fluidOutput(FluidStack output) {
        this.fluidOutput.add(output);
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractRecipeBuilder<T> fluidOutput(FluidStack... outputs) {
        for (FluidStack output : outputs) {
            fluidOutput(output);
        }
        return this;
    }

    @RecipeBuilderMethodDescription
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

    @GroovyBlacklist
    public void validateName() {
        if (name == null) {
            name = RecipeName.generateRl(getRecipeNamePrefix());
        }
    }

    @GroovyBlacklist
    public void validateFluids(GroovyLog.Msg msg, int minFluidInput, int maxFluidInput, int minFluidOutput, int maxFluidOutput) {
        fluidInput.trim();
        fluidOutput.trim();
        msg.add(fluidInput.size() < minFluidInput || fluidInput.size() > maxFluidInput, () -> getRequiredString(minFluidInput, maxFluidInput, "fluid input") + ", but found " + fluidInput.size());
        msg.add(fluidOutput.size() < minFluidOutput || fluidOutput.size() > maxFluidOutput, () -> getRequiredString(minFluidOutput, maxFluidOutput, "fluid output") + ", but found " + fluidOutput.size());
    }

    @GroovyBlacklist
    public void validateItems(GroovyLog.Msg msg, int minInput, int maxInput, int minOutput, int maxOutput) {
        input.trim();
        output.trim();
        msg.add(input.size() < minInput || input.size() > maxInput, () -> getRequiredString(minInput, maxInput, "item input") + ", but found " + input.size());
        msg.add(output.size() < minOutput || output.size() > maxOutput, () -> getRequiredString(minOutput, maxOutput, "item output") + ", but found " + output.size());
    }

    @GroovyBlacklist
    public void validateItems(GroovyLog.Msg msg) {
        validateItems(msg, 0, 0, 0, 0);
    }

    @GroovyBlacklist
    public void validateFluids(GroovyLog.Msg msg) {
        validateFluids(msg, 0, 0, 0, 0);
    }

    @GroovyBlacklist
    public void validateCustom(GroovyLog.Msg msg, Collection<?> collection, int min, int max, String type) {
        msg.add(collection.size() < min || collection.size() > max, () -> getRequiredString(min, max, type) + ", but found " + collection.size());
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
