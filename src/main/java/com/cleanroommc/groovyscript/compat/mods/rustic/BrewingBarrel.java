package com.cleanroommc.groovyscript.compat.mods.rustic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import rustic.common.crafting.BrewingBarrelRecipe;
import rustic.common.crafting.IBrewingBarrelRecipe;
import rustic.common.crafting.Recipes;

import java.util.Collection;

@RegistryDescription
public class BrewingBarrel extends StandardListRegistry<IBrewingBarrelRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('ironberryjuice')).fluidOutput(fluid('lava'))"),
            @Example(".fluidInput(fluid('water')).fluidOutput(fluid('lava'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IBrewingBarrelRecipe> getRecipes() {
        return Recipes.brewingRecipes;
    }

    @MethodDescription(example = @Example("fluid('ale')"))
    public boolean removeByOutput(IIngredient output) {
        return Recipes.brewingRecipes.removeIf(entry -> {
            if (output.test(entry.getOuput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('ironberryjuice')"))
    public boolean removeByInput(IIngredient input) {
        return Recipes.brewingRecipes.removeIf(entry -> {
            if (input.test(entry.getInput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "fluidOutput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IBrewingBarrelRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Rustic Brewing Barrel recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 0, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IBrewingBarrelRecipe register() {
            if (!validate()) return null;
            IBrewingBarrelRecipe recipe = new BrewingBarrelRecipe(fluidOutput.get(0), fluidInput.get(0));
            ModSupport.RUSTIC.get().brewingBarrel.add(recipe);
            return recipe;
        }
    }

}
