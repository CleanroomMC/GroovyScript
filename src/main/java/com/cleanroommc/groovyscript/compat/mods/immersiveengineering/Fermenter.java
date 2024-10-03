package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.FermenterRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Fermenter extends StandardListRegistry<FermenterRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).fluidOutput(fluid('water')).energy(100)"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<FermenterRecipe> getRecipes() {
        return FermenterRecipe.recipeList;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public FermenterRecipe add(FluidStack fluidOutput, @NotNull ItemStack itemOutput, IIngredient input, int energy) {
        FermenterRecipe recipe = new FermenterRecipe(fluidOutput.copy(), itemOutput.copy(), ImmersiveEngineering.toIngredientStack(input), energy);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("fluid('ethanol')"))
    public void removeByOutput(FluidStack fluidOutput) {
        if (IngredientHelper.isEmpty(fluidOutput)) {
            GroovyLog.msg("Error removing Immersive Engineering Fermenter recipe")
                    .add("fluid output must not be empty")
                    .error()
                    .post();
        }
        if (!getRecipes().removeIf(recipe -> {
            if (recipe.fluidOutput.isFluidEqual(fluidOutput)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Fermenter recipe")
                    .add("no recipes found for {}", fluidOutput)
                    .error()
                    .post();
        }
    }

    @MethodDescription(example = @Example("item('minecraft:reeds')"))
    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Immersive Engineering Fermenter recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
        }
        FermenterRecipe recipe = FermenterRecipe.findRecipe(input);
        if (recipe != null) {
            getRecipes().remove(recipe);
            addBackup(recipe);
        } else {
            GroovyLog.msg("Error removing Immersive Engineering Fermenter recipe")
                    .add("no recipes found for {}", input)
                    .error()
                    .post();
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<FermenterRecipe> {

        @Property
        private int energy;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Fermenter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 1);
            validateFluids(msg, 0, 0, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable FermenterRecipe register() {
            if (!validate()) return null;
            FermenterRecipe recipe = new FermenterRecipe(fluidOutput.get(0), output.getOrEmpty(0), ImmersiveEngineering.toIngredientStack(input.get(0)), energy);
            ModSupport.IMMERSIVE_ENGINEERING.get().fermenter.add(recipe);
            return recipe;
        }

    }

}
