package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ic2.RecipeInput;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.recipe.ICannerEnrichRecipeManager;
import ic2.api.recipe.MachineRecipe;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FluidCanner extends VirtualizedRegistry<MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack>> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> remove(recipe, false));
        restoreFromBackup().forEach(recipe -> add(recipe, false));
    }

    public void add(MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> recipe) {
        add(recipe, true);
    }

    public MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> add(FluidStack input, IIngredient input1, FluidStack output) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Fluid Canner recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input 1 must not be empty")
                .add(IngredientHelper.isEmpty(input1), () -> "input 2 must not be empty")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> recipe = create(input, input1, output, null);
        add(recipe);
        return recipe;
    }

    public MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> add(FluidStack input, IIngredient input1, FluidStack output, NBTTagCompound tag) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Fluid Canner recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input 1 must not be empty")
                .add(IngredientHelper.isEmpty(input1), () -> "input 2 must not be empty")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> recipe = create(input, input1, output, tag);
        add(recipe);
        return recipe;
    }

    public SimpleObjectStream<MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack>> streamRecipes() {
        return new SimpleObjectStream<>(asList()).setRemover(this::remove);
    }

    public boolean remove(MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> recipe) {
        return remove(recipe, true);
    }

    public void removeByOutput(FluidStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Fluid Canning recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        for (Iterator<? extends MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack>> iterator = Recipes.cannerEnrich.getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> rec = iterator.next();
            if (rec.getOutput().isFluidEqual(output) && rec.getOutput().amount == output.amount) {
                iterator.remove();
                addBackup(rec);
            }
        }
    }

    public void removeByInput(FluidStack input, ItemStack input1) {
        if (GroovyLog.msg("Error removing Industrialcraft 2 Fluid Canning recipe")
                .add(IngredientHelper.isEmpty(input), () -> "fluid input must not be empty")
                .add(IngredientHelper.isEmpty(input1), () -> "input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (Iterator<? extends MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack>> iterator = Recipes.cannerEnrich.getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> rec = iterator.next();
            if (rec.getInput().fluid.getFluid() == input.getFluid() && rec.getInput().additive.matches(input1)) {
                iterator.remove();
                addBackup(rec);
            }
        }
    }

    public void removeAll() {
        for (Iterator<? extends MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack>> iterator = Recipes.cannerEnrich.getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> rec = iterator.next();
            iterator.remove();
            addBackup(rec);
        }
    }

    private boolean remove(MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> recipe, boolean backup) {
        for (Iterator<? extends MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack>> iterator = Recipes.cannerEnrich.getRecipes().iterator(); iterator.hasNext(); ) {
            MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> rec = iterator.next();
            if (recipe.getInput().matches(rec.getInput().fluid, rec.getInput().additive.getInputs().get(0))) {
                iterator.remove();
                if (backup) addBackup(rec);
                return true;
            }
        }

        return false;
    }

    private void add(MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> recipe, boolean scripted) {
        Recipes.cannerEnrich.addRecipe(recipe.getInput(), recipe.getOutput(), recipe.getMetaData(), false);
        if (scripted) addScripted(recipe);
    }

    private static MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> create(FluidStack input, IIngredient input1, FluidStack output, NBTTagCompound tag) {
        return new MachineRecipe<>(new ICannerEnrichRecipeManager.Input(input, new RecipeInput(input1)), output, tag);
    }

    private static List<MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack>> asList() {
        List<MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack>> list = new ArrayList<>();
        for (MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> rec : Recipes.cannerEnrich.getRecipes()) {
            list.add(rec);
        }
        return list;
    }
}
