package com.cleanroommc.groovyscript.compat.mods.ic2.classic;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ic2.RecipeInput;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.machine.IMachineRecipeList;
import ic2.api.classic.recipe.machine.MachineExpOutput;
import ic2.api.classic.recipe.machine.MachineOutput;
import net.minecraft.item.ItemStack;

public class Sawmill extends VirtualizedRegistry<IMachineRecipeList.RecipeEntry> {

    public Sawmill() {
        super("Sawmill", "sawmill");
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ClassicRecipes.sawMill.removeRecipe(recipe));
        restoreFromBackup().forEach(recipe -> ClassicRecipes.sawMill.addRecipe(recipe.getInput(), recipe.getOutput(), "" + recipe.hashCode()));
    }

    public IMachineRecipeList.RecipeEntry add(ItemStack output, IIngredient input) {
        IMachineRecipeList.RecipeEntry entry = new IMachineRecipeList.RecipeEntry(new RecipeInput(input), new MachineOutput(null, output), "" + output.hashCode());
        ClassicRecipes.sawMill.addRecipe(entry.getInput(), entry.getOutput(), entry.getRecipeID());
        addScripted(entry);
        return entry;
    }

    public IMachineRecipeList.RecipeEntry add(ItemStack output, IIngredient input, float xp) {
        IMachineRecipeList.RecipeEntry entry = new IMachineRecipeList.RecipeEntry(new RecipeInput(input), new MachineExpOutput(null, xp, output), "" + output.hashCode());
        ClassicRecipes.sawMill.addRecipe(entry.getInput(), entry.getOutput(), entry.getRecipeID());
        addScripted(entry);
        return entry;
    }

    public void remove(IMachineRecipeList.RecipeEntry entry) {
        ClassicRecipes.sawMill.removeRecipe(entry);
        addScripted(entry);
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

    public void  removeAll() {
        for (IMachineRecipeList.RecipeEntry entry : ClassicRecipes.sawMill.getRecipeMap()) {
            remove(entry);
        }
    }
}
