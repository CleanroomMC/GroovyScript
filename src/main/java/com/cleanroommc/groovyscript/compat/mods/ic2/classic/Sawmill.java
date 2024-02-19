package com.cleanroommc.groovyscript.compat.mods.ic2.classic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ic2.RecipeInput;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.machine.IMachineRecipeList;
import ic2.api.classic.recipe.machine.MachineExpOutput;
import ic2.api.classic.recipe.machine.MachineOutput;
import net.minecraft.item.ItemStack;

public class Sawmill extends VirtualizedRegistry<IMachineRecipeList.RecipeEntry> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ClassicRecipes.sawMill.removeRecipe(recipe));
        restoreFromBackup().forEach(recipe -> ClassicRecipes.sawMill.addRecipe(recipe.getInput(), recipe.getOutput(), String.valueOf(recipe.hashCode())));
    }

    public IMachineRecipeList.RecipeEntry add(ItemStack output, IIngredient input) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Sawmill recipe")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        IMachineRecipeList.RecipeEntry entry = new IMachineRecipeList.RecipeEntry(new RecipeInput(input), new MachineOutput(null, output), String.valueOf(output.hashCode()));
        ClassicRecipes.sawMill.addRecipe(entry.getInput(), entry.getOutput(), entry.getRecipeID());
        addScripted(entry);
        return entry;
    }

    public IMachineRecipeList.RecipeEntry add(ItemStack output, IIngredient input, float xp) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Sawmill recipe")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        if (xp < 0) {
            GroovyLog.msg("Error adding Industrialcraft 2 Sawmill recipe")
                    .add("xp must not be negative, defaulting to zero")
                    .warn()
                    .post();
            xp = 0F;
        }
        IMachineRecipeList.RecipeEntry entry = new IMachineRecipeList.RecipeEntry(new RecipeInput(input), new MachineExpOutput(null, xp, output), String.valueOf(output.hashCode()));
        ClassicRecipes.sawMill.addRecipe(entry.getInput(), entry.getOutput(), entry.getRecipeID());
        addScripted(entry);
        return entry;
    }

    public SimpleObjectStream<IMachineRecipeList.RecipeEntry> streamRecipes() {
        return new SimpleObjectStream<>(ClassicRecipes.sawMill.getRecipeMap()).setRemover(this::remove);
    }

    public boolean remove(IMachineRecipeList.RecipeEntry entry) {
        addScripted(entry);
        return !ClassicRecipes.sawMill.removeRecipe(entry.getInput()).isEmpty();
    }

    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Industrialcraft Sawmill recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        for (IMachineRecipeList.RecipeEntry entry : ClassicRecipes.sawMill.getRecipeMap()) {
            if (ItemStack.areItemStacksEqual(entry.getOutput().getAllOutputs().get(0), output)) {
                remove(entry);
            }
        }
    }

    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Industrialcraft Sawmill recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        for (IMachineRecipeList.RecipeEntry entry : ClassicRecipes.sawMill.getRecipeMap()) {
            if (entry.getInput().matches(input)) remove(entry);
        }
    }

    public void removeAll() {
        for (IMachineRecipeList.RecipeEntry entry : ClassicRecipes.sawMill.getRecipeMap()) {
            remove(entry);
        }
    }
}
