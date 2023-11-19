package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ic2.RecipeInput;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;

import java.util.*;

public class BlockCutter extends VirtualizedRegistry<MachineRecipe<IRecipeInput, Collection<ItemStack>>> {

    public BlockCutter() {
        super(Alias.generateOf("Cutter"));
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> remove(recipe, false));
        restoreFromBackup().forEach(recipe -> add(recipe, false));
    }

    public void add(MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe) {
        add(recipe, true);
    }

    public MachineRecipe<IRecipeInput, Collection<ItemStack>> add(IIngredient input, ItemStack output) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Block Cutter recipe")
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

    public SimpleObjectStream<MachineRecipe<IRecipeInput, Collection<ItemStack>>> streamRecipes() {
        return new SimpleObjectStream<>(asList()).setRemover(this::remove);
    }

    public boolean remove(MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe) {
        return remove(recipe, true);
    }

    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Block Cutter recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        for (Iterator<? extends MachineRecipe<IRecipeInput, Collection<ItemStack>>> iterator = Recipes.blockcutter.getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<IRecipeInput, Collection<ItemStack>> rec = iterator.next();
            if (ItemStack.areItemStacksEqual((ItemStack) rec.getOutput().toArray()[0], output)) {
                iterator.remove();
                addBackup(rec);
            }
        }
    }

    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Block Cutter recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        for (Iterator<? extends MachineRecipe<IRecipeInput, Collection<ItemStack>>> iterator = Recipes.blockcutter.getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<IRecipeInput, Collection<ItemStack>> rec = iterator.next();
            if (rec.getInput().matches(input)) {
                iterator.remove();
                addBackup(rec);
            }
        }
    }

    public void removeAll() {
        for (Iterator<? extends MachineRecipe<IRecipeInput, Collection<ItemStack>>> iterator = Recipes.blockcutter.getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<IRecipeInput, Collection<ItemStack>> rec = iterator.next();
            iterator.remove();
            addBackup(rec);
        }
    }

    private boolean remove(MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe, boolean backup) {
        for (Iterator<? extends MachineRecipe<IRecipeInput, Collection<ItemStack>>> iterator = Recipes.blockcutter.getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<IRecipeInput, Collection<ItemStack>> rec = iterator.next();
            if (rec.getInput().matches(recipe.getInput().getInputs().get(0))) {
                iterator.remove();
                if (backup) addBackup(recipe);
                return true;
            }
        }

        return false;
    }

    private void add(MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe, boolean scripted) {
        Recipes.blockcutter.addRecipe(recipe.getInput(), recipe.getOutput(), recipe.getMetaData(), false);
        if (scripted) addScripted(recipe);
    }

    private static List<MachineRecipe<IRecipeInput, Collection<ItemStack>>> asList() {
        List<MachineRecipe<IRecipeInput, Collection<ItemStack>>> list = new ArrayList<>();
        for (MachineRecipe<IRecipeInput, Collection<ItemStack>> rec : Recipes.blockcutter.getRecipes()) {
            list.add(rec);
        }
        return list;
    }
}
