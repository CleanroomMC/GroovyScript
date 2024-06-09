package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import org.jetbrains.annotations.ApiStatus;
import reborncore.api.recipe.IBaseRecipeType;
import reborncore.api.recipe.RecipeHandler;
import reborncore.common.recipes.RecipeTranslator;

public abstract class AbstractGenericTechRebornRegistry extends VirtualizedRegistry<IBaseRecipeType> {

    abstract String reference();

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> RecipeHandler.recipeList.removeIf(r -> r == recipe));
        RecipeHandler.recipeList.addAll(restoreFromBackup());
    }

    public void add(IBaseRecipeType recipe) {
        if (recipe != null) {
            addScripted(recipe);
            RecipeHandler.recipeList.add(recipe);
        }
    }

    public boolean remove(IBaseRecipeType recipe) {
        if (RecipeHandler.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByInput(IIngredient input) {
        RecipeHandler.recipeList.removeIf(recipe -> {
            if (recipe.getRecipeName().equals(reference())) {
                if (recipe.getInputs().stream().map(RecipeTranslator::getStackFromObject).anyMatch(input)) {
                    addBackup(recipe);
                    return true;
                }
            }
            return false;
        });
    }

    public void removeByOutput(IIngredient output) {
        RecipeHandler.recipeList.removeIf(recipe -> {
            if (recipe.getRecipeName().equals(reference())) {
                if (recipe.getOutputs().stream().anyMatch(output)) {
                    addBackup(recipe);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IBaseRecipeType> streamRecipes() {
        return new SimpleObjectStream<>(RecipeHandler.getRecipeClassFromName(reference())).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        RecipeHandler.recipeList.removeIf(recipe -> {
            if (recipe.getRecipeName().equals(reference())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

}
