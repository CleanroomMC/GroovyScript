package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.RefineryRecipe;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class Refinery extends VirtualizedRegistry<RefineryRecipe> {

    public Refinery() {
        super("Refinery", "refinery");
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> RefineryRecipe.recipeList.removeIf(r -> r == recipe));
        RefineryRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(RefineryRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            RefineryRecipe.recipeList.add(recipe);
        }
    }

    public RefineryRecipe add(FluidStack output, FluidStack input0, FluidStack input1, int energy) {
        RefineryRecipe recipe = RefineryRecipe.addRecipe(output, input0, input1, energy);
        addScripted(recipe);
        return recipe;
    }

    public boolean remove(RefineryRecipe recipe) {
        if (RefineryRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByOutput(FluidStack fluidOutput) {
        for (Iterator<RefineryRecipe> iterator = RefineryRecipe.recipeList.iterator(); iterator.hasNext(); ) {
            RefineryRecipe recipe = iterator.next();
            if (recipe.output.isFluidEqual(fluidOutput)) {
                addBackup(recipe);
                iterator.remove();
            }
        }
    }

    public void removeByInput(FluidStack input0, FluidStack input1) {
        RefineryRecipe recipe = RefineryRecipe.findRecipe(input0, input1);
        remove(recipe);
    }

    public SimpleObjectStream<RefineryRecipe> stream() {
        return new SimpleObjectStream<>(RefineryRecipe.recipeList).setRemover(this::remove);
    }

    public void removeAll() {
        RefineryRecipe.recipeList.forEach(this::addBackup);
        RefineryRecipe.recipeList.clear();
    }

    public static class RecipeBuilder extends EnergyRecipeBuilder<RefineryRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Refinery recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 2, 2, 1, 1);
        }

        @Override
        public @Nullable RefineryRecipe register() {
            return ModSupport.IMMERSIVE_ENGINEERING.get().refinery.add(fluidOutput.get(0), fluidInput.get(0), fluidInput.get(1), energy);
        }
    }
}
