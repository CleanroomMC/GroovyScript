package com.cleanroommc.groovyscript.compat.mods.ic2.classic;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ic2.RecipeInput;
import com.cleanroommc.groovyscript.compat.mods.ic2.exp.Extractor;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.machine.IMachineRecipeList;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;
import java.util.Collections;

public class ClassicExtractor extends Extractor {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ClassicRecipes.extractor.removeRecipe(recipe.getInput()));
        restoreFromBackup().forEach(recipe -> ClassicRecipes.extractor.addRecipe(recipe.getInput(), (ItemStack) recipe.getOutput().toArray()[0], "" + recipe.hashCode()));
    }

    @Override
    public void add(MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe) {
        ClassicRecipes.extractor.addRecipe(recipe.getInput(), (ItemStack) recipe.getOutput().toArray()[0], "" + recipe.hashCode());
        addScripted(recipe);
    }

    @Override
    public MachineRecipe<IRecipeInput, Collection<ItemStack>> add(IIngredient input, ItemStack output) {
        MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(new RecipeInput(input), Collections.singleton(output));
        add(recipe);
        return recipe;
    }

    public MachineRecipe<IRecipeInput, Collection<ItemStack>> add(IIngredient input, ItemStack output, float xp) {
        MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(new RecipeInput(input), Collections.singleton(output));
        ClassicRecipes.extractor.addRecipe(recipe.getInput(), output, xp, "" + recipe.hashCode());
        addScripted(recipe);
        return recipe;
    }

    @Override
    public MachineRecipe<IRecipeInput, Collection<ItemStack>> add(IIngredient input, ItemStack output, NBTTagCompound tag) {
        return add(input, output);
    }

    @Override
    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Extractor recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        for (IMachineRecipeList.RecipeEntry entry : ClassicRecipes.extractor.getRecipeMap()) {
            MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(entry.getInput(), entry.getOutput().getAllOutputs());
            if (ItemStack.areItemStacksEqual(entry.getOutput().getAllOutputs().get(0), output)) {
                remove(recipe);
            }
        }
    }

    @Override
    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Extractor recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        IMachineRecipeList.RecipeEntry entry = ClassicRecipes.extractor.getRecipeInAndOutput(input, false);
        if (entry == null) {
            GroovyLog.msg("Error removing Industrialcraft 2 Extractor recipe")
                    .add("no recipes found for", input)
                    .error()
                    .post();
            return;
        }
        MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(entry.getInput(), entry.getOutput().getAllOutputs());
        ClassicRecipes.extractor.removeRecipe(entry);
        addBackup(recipe);
    }

    @Override
    public void removeAll() {
        for (IMachineRecipeList.RecipeEntry entry : ClassicRecipes.extractor.getRecipeMap()) {
            MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(entry.getInput(), entry.getOutput().getAllOutputs());
            remove(recipe);
        }
    }

    @Override
    public boolean remove(MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe) {
        addBackup(recipe);
        return ClassicRecipes.extractor.removeRecipe(recipe.getInput()).size() > 0;
    }
}
