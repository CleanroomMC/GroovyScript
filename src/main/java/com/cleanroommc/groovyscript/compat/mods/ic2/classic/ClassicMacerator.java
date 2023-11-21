package com.cleanroommc.groovyscript.compat.mods.ic2.classic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ic2.RecipeInput;
import com.cleanroommc.groovyscript.compat.mods.ic2.exp.Macerator;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.machine.IMachineRecipeList;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClassicMacerator extends Macerator {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ClassicRecipes.macerator.removeRecipe(recipe.getInput()));
        restoreFromBackup().forEach(recipe -> ClassicRecipes.macerator.addRecipe(recipe.getInput(), (ItemStack) recipe.getOutput().toArray()[0], String.valueOf(recipe.hashCode())));
    }

    @Override
    public MachineRecipe<IRecipeInput, Collection<ItemStack>> add(ItemStack output, IIngredient input) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Macerator recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(new RecipeInput(input), Collections.singleton(output));
        add(recipe);
        return recipe;
    }

    public MachineRecipe<IRecipeInput, Collection<ItemStack>> add(ItemStack output, IIngredient input, float xp) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Macerator recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        if (xp < 0) {
            GroovyLog.msg("Error adding Industrialcraft 2 Macerator recipe")
                    .add("xp must not be negative, defaulting to zero")
                    .warn()
                    .post();
            xp = 0F;
        }
        MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(new RecipeInput(input), Collections.singleton(output));
        ClassicRecipes.macerator.addRecipe(recipe.getInput(), output, xp, String.valueOf(recipe.hashCode()));
        addScripted(recipe);
        return recipe;
    }

    @Override
    public MachineRecipe<IRecipeInput, Collection<ItemStack>> add(ItemStack output, IIngredient input, NBTTagCompound tag) {
        return add(output, input);
    }

    public boolean remove(IMachineRecipeList.RecipeEntry entry) {
        addBackup(new MachineRecipe<>(entry.getInput(), entry.getOutput().getAllOutputs()));
        return ClassicRecipes.macerator.removeRecipe(entry.getInput()).size() > 0;
    }

    @Override
    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Macerator recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        for (IMachineRecipeList.RecipeEntry entry : ClassicRecipes.macerator.getRecipeMap()) {
            if (ItemStack.areItemStacksEqual(entry.getOutput().getAllOutputs().get(0), output)) {
                MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(entry.getInput(), entry.getOutput().getAllOutputs());
                remove(recipe);
            }
        }
    }

    @Override
    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Macerator recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        IMachineRecipeList.RecipeEntry entry = ClassicRecipes.macerator.getRecipeInAndOutput(input, false);
        if (entry == null) {
            GroovyLog.msg("Error removing Industrialcraft 2 Macerator recipe")
                    .add("no recipes found for", input)
                    .error()
                    .post();
            return;
        }
        MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(entry.getInput(), entry.getOutput().getAllOutputs());
        ClassicRecipes.macerator.removeRecipe(entry);
        addBackup(recipe);
    }

    @Override
    public void removeAll() {
        for (IMachineRecipeList.RecipeEntry entry : ClassicRecipes.macerator.getRecipeMap()) {
            MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(entry.getInput(), entry.getOutput().getAllOutputs());
            remove(recipe);
        }
    }

    @Override
    public void add(MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe) {
        addScripted(recipe);
        ClassicRecipes.macerator.addRecipe(recipe.getInput(), (ItemStack) recipe.getOutput().toArray()[0], String.valueOf(recipe.hashCode()));
    }

    @Override
    public boolean remove(MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe) {
        addBackup(recipe);
        return ClassicRecipes.macerator.removeRecipe(recipe.getInput()).size() > 0;
    }

    @Override
    protected List<MachineRecipe<IRecipeInput, Collection<ItemStack>>> asList() {
        List<MachineRecipe<IRecipeInput, Collection<ItemStack>>> list = new ArrayList<>();
        for (IMachineRecipeList.RecipeEntry entry : ClassicRecipes.macerator.getRecipeMap()) {
            MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(entry.getInput(), entry.getOutput().getAllOutputs());
            list.add(recipe);
        }

        return list;
    }
}
