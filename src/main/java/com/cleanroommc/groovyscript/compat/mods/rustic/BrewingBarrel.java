package com.cleanroommc.groovyscript.compat.mods.rustic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import org.jetbrains.annotations.Nullable;
import rustic.common.crafting.BrewingBarrelRecipe;
import rustic.common.crafting.IBrewingBarrelRecipe;
import rustic.common.crafting.Recipes;

@RegistryDescription
public class BrewingBarrel extends VirtualizedRegistry<IBrewingBarrelRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('ironberryjuice')).fluidOutput(fluid('lava'))"),
            @Example(".fluidInput(fluid('water')).fluidOutput(fluid('lava'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        Recipes.brewingRecipes.removeAll(removeScripted());
        Recipes.brewingRecipes.addAll(restoreFromBackup());
    }

    public void add(IBrewingBarrelRecipe recipe) {
        Recipes.brewingRecipes.add(recipe);
        addScripted(recipe);
    }

    public boolean remove(IBrewingBarrelRecipe recipe) {
        addBackup(recipe);
        return Recipes.brewingRecipes.remove(recipe);
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("fluid('ale')"))
    public boolean removeByOutput(IIngredient output) {
        return Recipes.brewingRecipes.removeIf(entry -> {
            if (output.test(entry.getOuput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("fluid('ironberryjuice')"))
    public boolean removeByInput(IIngredient input) {
        return Recipes.brewingRecipes.removeIf(entry -> {
            if (input.test(entry.getInput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        Recipes.brewingRecipes.forEach(this::addBackup);
        Recipes.brewingRecipes.clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IBrewingBarrelRecipe> streamRecipes() {
        return new SimpleObjectStream<>(Recipes.brewingRecipes).setRemover(this::remove);
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
