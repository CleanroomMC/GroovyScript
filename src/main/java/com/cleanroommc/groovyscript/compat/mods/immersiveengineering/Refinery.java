package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.RefineryRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Refinery extends VirtualizedRegistry<RefineryRecipe> {

    @RecipeBuilderDescription(example = @Example(".fluidInput(fluid('water'), fluid('water')).fluidOutput(fluid('lava')).energy(100)"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> RefineryRecipe.recipeList.removeIf(r -> r == recipe));
        RefineryRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(RefineryRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            RefineryRecipe.recipeList.add(recipe);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RefineryRecipe add(FluidStack output, FluidStack input0, FluidStack input1, int energy) {
        RefineryRecipe recipe = new RefineryRecipe(output, input0, input1, energy);
        add(recipe);
        return recipe;
    }

    public boolean remove(RefineryRecipe recipe) {
        if (RefineryRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example(value = "fluid('biodiesel')", commented = true))
    public void removeByOutput(FluidStack fluidOutput) {
        if (IngredientHelper.isEmpty(fluidOutput)) {
            GroovyLog.msg("Error removing Immersive Engineering Refinery recipe")
                    .add("fluid output must not be empty")
                    .error()
                    .post();
            return;
        }
        if (!RefineryRecipe.recipeList.removeIf(recipe -> {
            if (recipe.output.isFluidEqual(fluidOutput)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Refinery recipe")
                    .add("no recipes found for {}", fluidOutput)
                    .error()
                    .post();
        }
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("fluid('plantoil'), fluid('ethanol')"))
    public void removeByInput(FluidStack input0, FluidStack input1) {
        if (GroovyLog.msg("Error removing Immersive Engineering Refinery recipe")
                .add(IngredientHelper.isEmpty(input0), () -> "fluid input 1 must not be empty")
                .add(IngredientHelper.isEmpty(input1), () -> "fluid input 2 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        List<RefineryRecipe> recipes = RefineryRecipe.recipeList.stream().filter(r -> (r.input0.isFluidEqual(input0) && r.input1.isFluidEqual(input1)) || (r.input0.isFluidEqual(input1) && r.input1.isFluidEqual(input0))).collect(Collectors.toList());
        for (RefineryRecipe recipe : recipes) {
            remove(recipe);
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Refinery recipe")
                    .add("no recipes found for {} and {}", input0, input1)
                    .error()
                    .post();
        }
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RefineryRecipe> streamRecipes() {
        return new SimpleObjectStream<>(RefineryRecipe.recipeList).setRemover(this::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        RefineryRecipe.recipeList.forEach(this::addBackup);
        RefineryRecipe.recipeList.clear();
    }

    @Property(property = "fluidInput", valid = @Comp("2"))
    @Property(property = "fluidOutput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RefineryRecipe> {

        @Property
        private int energy;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Refinery recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 2, 2, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RefineryRecipe register() {
            if (!validate()) return null;
            RefineryRecipe recipe = new RefineryRecipe(fluidOutput.get(0), fluidInput.get(0), fluidInput.get(1), energy);
            ModSupport.IMMERSIVE_ENGINEERING.get().refinery.add(recipe);
            return recipe;
        }
    }
}
