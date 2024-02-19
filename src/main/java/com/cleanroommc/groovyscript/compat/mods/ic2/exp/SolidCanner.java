package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ic2.RecipeInput;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.recipe.ICannerBottleRecipeManager;
import ic2.api.recipe.MachineRecipe;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SolidCanner extends VirtualizedRegistry<MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack>> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> remove(recipe, false));
        restoreFromBackup().forEach(recipe -> add(recipe, false));
    }

    public void add(MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> recipe) {
        add(recipe, true);
    }

    public MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> add(IIngredient input, IIngredient input1, ItemStack output) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Solid Canner recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input 1 must not be emtpy")
                .add(IngredientHelper.isEmpty(input1), () -> "input 2 must not be empty")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> recipe = create(input, input1, output, null);
        add(recipe);
        return recipe;
    }

    public MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> add(IIngredient input, IIngredient input1, ItemStack output, NBTTagCompound tag) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Solid Canner recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input 1 must not be emtpy")
                .add(IngredientHelper.isEmpty(input1), () -> "input 2 must not be empty")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> recipe = create(input, input1, output, tag);
        add(recipe);
        return recipe;
    }

    public SimpleObjectStream<MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack>> streamRecipes() {
        return new SimpleObjectStream<>(asList()).setRemover(this::remove);
    }

    public boolean remove(MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> recipe) {
        return remove(recipe, true);
    }

    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Solid Canning recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        for (Iterator<? extends MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack>> iterator = Recipes.cannerBottle.getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> rec = iterator.next();
            if (ItemStack.areItemStacksEqual(rec.getOutput(), output)) {
                iterator.remove();
                addBackup(rec);
            }
        }
    }

    public void removeByInput(ItemStack input, ItemStack input1) {
        if (GroovyLog.msg("Error removing industrialcraft 2 Solid Canning recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input 1 must not be empty")
                .add(IngredientHelper.isEmpty(input1), () -> "input 2 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (Iterator<? extends MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack>> iterator = Recipes.cannerBottle.getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> rec = iterator.next();
            if (rec.getInput().container.matches(input) && rec.getInput().fill.matches(input1)) {
                iterator.remove();
                addBackup(rec);
            }
        }
    }

    public void removeAll() {
        for (Iterator<? extends MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack>> iterator = Recipes.cannerBottle.getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> rec = iterator.next();
            iterator.remove();
            addBackup(rec);
        }
    }

    private boolean remove(MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> recipe, boolean backup) {
        for (Iterator<? extends MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack>> iterator = Recipes.cannerBottle.getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> rec = iterator.next();
            if (recipe.getInput().matches(rec.getInput().container.getInputs().get(0), rec.getInput().fill.getInputs().get(0))) {
                iterator.remove();
                if (backup) addBackup(rec);
                return true;
            }
        }

        return false;
    }

    private void add(MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> recipe, boolean scripted) {
        Recipes.cannerBottle.addRecipe(recipe.getInput().container, recipe.getInput().fill, recipe.getOutput(), false);
        if (scripted) addScripted(recipe);
    }

    private static MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> create(IIngredient input, IIngredient input1, ItemStack output, NBTTagCompound tag) {
        return new MachineRecipe<>(new ICannerBottleRecipeManager.Input(new RecipeInput(input), new RecipeInput(input1)), output, tag);
    }

    private static List<MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack>> asList() {
        List<MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack>> list = new ArrayList<>();
        for (MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> rec : Recipes.cannerBottle.getRecipes()) {
            list.add(rec);
        }
        return list;
    }
}
