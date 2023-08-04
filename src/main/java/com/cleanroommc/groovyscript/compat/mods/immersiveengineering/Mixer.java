package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.MixerRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Mixer extends VirtualizedRegistry<MixerRecipe> {

    public Mixer() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> MixerRecipe.recipeList.removeIf(r -> r == recipe));
        MixerRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(MixerRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            MixerRecipe.recipeList.add(recipe);
        }
    }

    public MixerRecipe add(FluidStack fluidOutput, FluidStack fluidInput, int energy, List<IIngredient> itemInput) {
        Object[] inputs = ArrayUtils.mapToArray(itemInput, ImmersiveEngineering::toIngredientStack);
        MixerRecipe recipe = new MixerRecipe(fluidOutput, fluidInput, inputs, energy);
        add(recipe);
        return recipe;
    }

    public boolean remove(MixerRecipe recipe) {
        if (MixerRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByOutput(FluidStack fluidOutput) {
        if (GroovyLog.msg("Error removing Immersive Engineering Mixer recipe")
                .add(IngredientHelper.isEmpty(fluidOutput), () -> "fluid output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        if (!MixerRecipe.recipeList.removeIf(recipe -> {
            if (recipe.fluidOutput.isFluidEqual(fluidOutput)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Mixer recipe")
                    .add("no recipes found for {}", fluidOutput)
                    .error()
                    .post();
        }
    }

    public void removeByInput(IIngredient... itemInputs) {
        if (GroovyLog.msg("Error removing Immersive Engineering Mixer recipe")
                .add(itemInputs == null || itemInputs.length == 0, () -> "item input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        List<MixerRecipe> recipes = MixerRecipe.recipeList.stream().filter(r -> r.itemInputs.length == itemInputs.length &&
                                                                                Arrays.stream(itemInputs).anyMatch(check -> Arrays.stream(r.itemInputs).anyMatch(target -> ImmersiveEngineering.areIngredientsEquals(target, check))))
                .collect(Collectors.toList());
        for (MixerRecipe recipe : recipes) {
            remove(recipe);
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Mixer recipe")
                    .add("no recipes found for {}", Arrays.toString(itemInputs))
                    .error()
                    .post();
        }
    }

    public void removeByInput(FluidStack fluidInput, IIngredient... itemInput) {
        if (GroovyLog.msg("Error removing Immersive Engineering Mixer recipe")
                .add(IngredientHelper.isEmpty(fluidInput), () -> "fluid input must not be empty")
                .add(itemInput == null || itemInput.length == 0, () -> "item input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        List<MixerRecipe> recipes = MixerRecipe.recipeList.stream().filter(r -> fluidInput.isFluidEqual(r.fluidInput) &&
                                                                                r.itemInputs.length == itemInput.length &&
                                                                                Arrays.stream(itemInput).anyMatch(check -> Arrays.stream(r.itemInputs).anyMatch(target -> ImmersiveEngineering.areIngredientsEquals(target, check))))
                .collect(Collectors.toList());
        for (MixerRecipe recipe : recipes) {
            remove(recipe);
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Mixer recipe")
                    .add("no recipes found for {} and {}", fluidInput, Arrays.toString(itemInput))
                    .error()
                    .post();
        }
    }

    public SimpleObjectStream<MixerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(MixerRecipe.recipeList).setRemover(this::remove);
    }

    public void removeAll() {
        MixerRecipe.recipeList.forEach(this::addBackup);
        MixerRecipe.recipeList.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<MixerRecipe> {

        private int energy;

        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Mixer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, Integer.MAX_VALUE, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
        }

        @Override
        public @Nullable MixerRecipe register() {
            if (!validate()) return null;
            Object[] inputs = ArrayUtils.mapToArray(input, ImmersiveEngineering::toIngredientStack);
            MixerRecipe recipe = new MixerRecipe(fluidOutput.get(0), fluidInput.get(0), inputs, energy);
            ModSupport.IMMERSIVE_ENGINEERING.get().mixer.add(recipe);
            return recipe;
        }
    }
}
