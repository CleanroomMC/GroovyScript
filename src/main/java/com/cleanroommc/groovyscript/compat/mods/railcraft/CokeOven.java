package com.cleanroommc.groovyscript.compat.mods.railcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.ICokeOvenCrafter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class CokeOven extends StandardListRegistry<ICokeOvenCrafter.IRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:log')).output(item('railcraft:fuel_coke')).fluid(fluid('creosote') * 500).time(1800)"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ICokeOvenCrafter.IRecipe> getRecipes() {
        return Crafters.cokeOven().getRecipes();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public ICokeOvenCrafter.IRecipe add(IIngredient input, ItemStack output, FluidStack fluidOutput, int time) {
        if (time <= 0) {
            GroovyLog.msg("Error adding Railcraft Coke Oven recipe")
                    .error()
                    .add("time must be greater than 0, got: {}", time)
                    .post();
            return null;
        }
        RecipeBuilder builder = recipeBuilder();
        builder.input(input);
        builder.output(output);
        builder.fluid = fluidOutput;
        builder.time = time;
        return builder.register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public ICokeOvenCrafter.IRecipe add(IIngredient input, ItemStack output, FluidStack fluidOutput) {
        return add(input, output, fluidOutput, ICokeOvenCrafter.DEFAULT_COOK_TIME);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public ICokeOvenCrafter.IRecipe add(IIngredient input, ItemStack output, int time) {
        return add(input, output, null, time);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public ICokeOvenCrafter.IRecipe add(IIngredient input, ItemStack output) {
        return add(input, output, null, ICokeOvenCrafter.DEFAULT_COOK_TIME);
    }

    @MethodDescription(example = @Example("item('railcraft:fuel_coke')"))
    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Railcraft Coke Oven recipe")
                    .error()
                    .add("output must not be empty")
                    .post();
            return;
        }
        if (!getRecipes().removeIf(recipe -> {
            if (ItemStack.areItemStacksEqual(recipe.getOutput(), output)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Railcraft Coke Oven recipe")
                    .error()
                    .add("no recipes found for {}", output)
                    .post();
        }
    }

    @MethodDescription(example = @Example("item('minecraft:log')"))
    public void removeByInput(IIngredient input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Railcraft Coke Oven recipe")
                    .error()
                    .add("input must not be empty")
                    .post();
            return;
        }
        if (!getRecipes().removeIf(recipe -> {
            if (recipe.getInput().test(input.getMatchingStacks()[0])) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Railcraft Coke Oven recipe")
                    .error()
                    .add("no recipes found for {}", input)
                    .post();
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ICokeOvenCrafter.IRecipe> {

        @Property(comp = @Comp(gte = 0), defaultValue = "ICokeOvenCrafter.DEFAULT_COOK_TIME")
        private int time = ICokeOvenCrafter.DEFAULT_COOK_TIME;
        @Property
        private FluidStack fluid;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder fluid(FluidStack fluid) {
            this.fluid = fluid;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Railcraft Coke Oven recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (time <= 0) {
                msg.add("time must be greater than 0, got: {}", time);
                time = ICokeOvenCrafter.DEFAULT_COOK_TIME;
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ICokeOvenCrafter.IRecipe register() {
            if (!validate()) return null;
            ItemStack outputStack = output.get(0);
            Ingredient inputIngredient = input.get(0).toMcIngredient();
            FluidStack fluidCopy = fluid != null ? fluid.copy() : null;

            ICokeOvenCrafter.IRecipe recipe = new ICokeOvenCrafter.IRecipe() {
                @Override
                public net.minecraft.util.ResourceLocation getName() {
                    return new net.minecraft.util.ResourceLocation("groovyscript", "cokeoven_" + System.currentTimeMillis());
                }

                @Override
                public Ingredient getInput() {
                    return inputIngredient;
                }

                @Override
                public int getTickTime(ItemStack input) {
                    return time;
                }

                @Override
                public @Nullable FluidStack getFluidOutput() {
                    return fluidCopy != null ? fluidCopy.copy() : null;
                }

                @Override
                public ItemStack getOutput() {
                    return outputStack.copy();
                }
            };

            ModSupport.RAILCRAFT.get().cokeOven.add(recipe);
            return recipe;
        }
    }
}
