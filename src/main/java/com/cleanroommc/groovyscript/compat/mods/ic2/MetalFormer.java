package com.cleanroommc.groovyscript.compat.mods.ic2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.recipe.IBasicMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public class MetalFormer extends VirtualizedRegistry<MetalFormer.MetalFormerRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> remove(recipe.type, recipe.recipe, false));
        restoreFromBackup().forEach(recipe -> add(recipe.type, recipe.recipe, false));
    }

    public void add(int type, MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe) {
        add(type, recipe, true);
    }

    public MachineRecipe<IRecipeInput, Collection<ItemStack>> add(int type, IIngredient input, ItemStack output) {
        MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(new RecipeInput(input), Collections.singleton(output));
        add(type, recipe);
        return recipe;
    }

    public MachineRecipe<IRecipeInput, Collection<ItemStack>> add(int type, IIngredient input, ItemStack output, NBTTagCompound tag) {
        MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe = new MachineRecipe<>(new RecipeInput(input), Collections.singleton(output), tag);
        add(type, recipe);
        return recipe;
    }

    public SimpleObjectStream<MachineRecipe<IRecipeInput, Collection<ItemStack>>> streamRecipes() {
        return new SimpleObjectStream<>(asList()).setRemover(this::remove);
    }

    public SimpleObjectStream<MachineRecipe<IRecipeInput, Collection<ItemStack>>> streamRecipes(int type) {
        return new SimpleObjectStream<>(asList(type)).setRemover(r -> this.remove(type, r));
    }

    public boolean remove(int type, MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe) {
        return remove(type, recipe, true);
    }

    public boolean remove(MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe) {
        return remove(0, recipe) || remove(1, recipe) || remove(2, recipe);
    }

    public void removeByOutput(int type, ItemStack output) {
        if (GroovyLog.msg("Error removing Industrialcraft 2 Metal Former recipe")
                .add(type < 0 || type > 2, () -> "type must be between 0-2")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (Iterator<? extends MachineRecipe<IRecipeInput, Collection<ItemStack>>> iterator = getManager(type).getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<IRecipeInput, Collection<ItemStack>> rec = iterator.next();
            if (ItemStack.areItemStacksEqual((ItemStack) rec.getOutput().toArray()[0], output)) {
                iterator.remove();
                addBackup(new MetalFormerRecipe(type, rec));
            }
        }
    }

    public void removeByInput(int type, ItemStack input) {
        if (GroovyLog.msg("Error removing Industrialcraft 2 Metal Former recipe")
                .add(type < 0 || type > 2, () -> "type must be between 0-2")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (Iterator<? extends MachineRecipe<IRecipeInput, Collection<ItemStack>>> iterator = getManager(type).getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<IRecipeInput, Collection<ItemStack>> rec = iterator.next();
            if (rec.getInput().matches(input)) {
                iterator.remove();
                addBackup(new MetalFormerRecipe(type, rec));
            }
        }
    }

    public void removeAll(int type) {
        if (type < 0 || type > 2) {
            GroovyLog.msg("Error removing Industrialcraft 2 Metal Former recipe")
                    .add("type must be between 0-2")
                    .error()
                    .post();
            return;
        }
        for (Iterator<? extends MachineRecipe<IRecipeInput, Collection<ItemStack>>> iterator = getManager(type).getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<IRecipeInput, Collection<ItemStack>> rec = iterator.next();
            iterator.remove();
            addBackup(new MetalFormerRecipe(type, rec));
        }
    }

    private boolean remove(int type, MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe, boolean backup) {
        for (Iterator<? extends MachineRecipe<IRecipeInput, Collection<ItemStack>>> iterator = getManager(type).getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<IRecipeInput, Collection<ItemStack>> rec = iterator.next();
            if (rec.getInput().matches(recipe.getInput().getInputs().get(0))) {
                iterator.remove();
                if (backup) addBackup(new MetalFormerRecipe(type, recipe));
                return true;
            }
        }

        return false;
    }

    private void add(int type, MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe, boolean scripted) {
        getManager(type).addRecipe(recipe.getInput(), recipe.getOutput(), recipe.getMetaData(), false);
        if (scripted) addScripted(new MetalFormerRecipe(type, recipe));
    }

    public IBasicMachineRecipeManager getManager(int type) {
        switch (type) {
            default:
                return Recipes.metalformerCutting;
            case 1:
                return Recipes.metalformerExtruding;
            case 2:
                return Recipes.metalformerRolling;
        }
    }

    public static class MetalFormerRecipe {

        public int type;
        public MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe;

        public MetalFormerRecipe(int type, MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe) {
            this.type = type;
            this.recipe = recipe;
        }
    }

    private List<MachineRecipe<IRecipeInput, Collection<ItemStack>>> asList(int type) {
        List<MachineRecipe<IRecipeInput, Collection<ItemStack>>> list = new ArrayList<>();
        for (MachineRecipe<IRecipeInput, Collection<ItemStack>> rec : getManager(type).getRecipes()) {
            list.add(rec);
        }
        return list;
    }

    private List<MachineRecipe<IRecipeInput, Collection<ItemStack>>> asList() {
        List<MachineRecipe<IRecipeInput, Collection<ItemStack>>> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (MachineRecipe<IRecipeInput, Collection<ItemStack>> rec : getManager(i).getRecipes()) {
                list.add(rec);
            }
        }
        return list;
    }
}
