package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class JustEnoughItems extends ModPropertyContainer {

    public void hide(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            GroovyLog.msg("Error hiding items {}", ingredient)
                    .add("Items must not be empty")
                    .error()
                    .post();
            return;
        }
        if (IngredientHelper.isFluid(ingredient)) {
            JeiPlugin.HIDDEN_FLUIDS.add(IngredientHelper.toFluidStack(ingredient));
        } else {
            JeiPlugin.hideItem(ingredient.getMatchingStacks());
        }
    }

    public void hide(IIngredient... ingredients) {
        for (IIngredient ingredient : ingredients) {
            hide(ingredient);
        }
    }

    public void hide(Iterable<IIngredient> ingredients) {
        for (IIngredient ingredient : ingredients) {
            hide(ingredient);
        }
    }

    public void removeAndHide(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            GroovyLog.msg("Error remove and hide items {}", ingredient)
                    .add("Items must not be empty")
                    .error()
                    .post();
            return;
        }
        VanillaModule.crafting.removeByOutput(ingredient, false);
        JeiPlugin.hideItem(ingredient.getMatchingStacks());
    }

    public void removeAndHide(IIngredient... ingredients) {
        for (IIngredient ingredient : ingredients) {
            if (IngredientHelper.isEmpty(ingredient)) {
                GroovyLog.msg("Error remove and hide items {}", ingredient)
                        .add("Items must not be empty")
                        .error()
                        .post();
                return;
            }
            JeiPlugin.hideItem(ingredient.getMatchingStacks());
        }
        for (IRecipe recipe : ForgeRegistries.RECIPES) {
            if (recipe.getRegistryName() != null) {
                for (IIngredient ingredient : ingredients) {
                    if (ingredient.test(recipe.getRecipeOutput())) {
                        ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, recipe.getRegistryName());
                        break;
                    }
                }
            }
        }
    }

    public void removeAndHide(Iterable<IIngredient> ingredients) {
        for (IIngredient ingredient : ingredients) {
            if (IngredientHelper.isEmpty(ingredient)) {
                GroovyLog.msg("Error remove and hide items {}", ingredient)
                        .add("Items must not be empty")
                        .error()
                        .post();
                return;
            }
            JeiPlugin.hideItem(ingredient.getMatchingStacks());
        }
        for (IRecipe recipe : ForgeRegistries.RECIPES) {
            if (recipe.getRegistryName() != null) {
                for (IIngredient ingredient : ingredients) {
                    if (ingredient.test(recipe.getRecipeOutput())) {
                        ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, recipe.getRegistryName());
                        break;
                    }
                }
            }
        }
    }

    public void yeet(IIngredient ingredient) {
        removeAndHide(ingredient);
    }

    public void yeet(IIngredient... ingredients) {
        removeAndHide(ingredients);
    }

    public void yeet(Iterable<IIngredient> ingredients) {
        removeAndHide(ingredients);
    }

    public void hideCategory(String category) {
        if (category == null || category.isEmpty()) {
            GroovyLog.msg("Error hiding category")
                    .add("category must not be empty")
                    .error()
                    .post();
            return;
        }
        JeiPlugin.HIDDEN_CATEGORY.add(category);
    }
}
