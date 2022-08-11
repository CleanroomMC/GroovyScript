package com.cleanroommc.groovyscript.compat.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager.PulverizerRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.mixin.thermalexpansion.PulverizerManagerAccessor;
import com.cleanroommc.groovyscript.registry.IReloadableRegistry;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Pulverizer implements IReloadableRegistry<PulverizerRecipe> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        Map<ComparableItemStackValidated, PulverizerRecipe> currentMap = PulverizerManagerAccessor.getRecipeMap();
        Map<ComparableItemStackValidated, PulverizerRecipe> newMap = new Object2ObjectOpenHashMap<>(currentMap);
        ReloadableRegistryManager.unmarkScriptRecipes(Pulverizer.class).forEach(recipe -> newMap.values().removeIf(r -> r == recipe));
        ReloadableRegistryManager.recoverRecipes(Pulverizer.class)
                .stream()
                .map(recipe -> (PulverizerRecipe) recipe)
                .forEach(recipe -> newMap.put(PulverizerManagerAccessor.invokeConvertInput(recipe.getInput()), recipe));
        PulverizerManagerAccessor.setRecipeMap(newMap);
    }

    @Override
    public void removeEntry(PulverizerRecipe recipe) {
        ReloadableRegistryManager.addRecipeForRecovery(Pulverizer.class, recipe);
        PulverizerManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe);
    }

    public static class RecipeBuilder implements IRecipeBuilder<PulverizerRecipe> {

        private IIngredient input;
        private ItemStack output;
        private ItemStack secOutput;
        private int chance;
        private int energy;

        public RecipeBuilder input(IIngredient ingredient) {
            this.input = ingredient;
            return this;
        }

        public RecipeBuilder output(ItemStack itemStack) {
            this.output = itemStack;
            return this;
        }

        public RecipeBuilder secondaryOutput(ItemStack itemStack, int chance) {
            this.secOutput = itemStack;
            this.chance = chance;
            return this;
        }

        public RecipeBuilder secondaryOutput(ItemStack itemStack) {
            return secondaryOutput(itemStack, 100);
        }

        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = new GroovyLog.Msg("Error adding Thermal Pulverizer recipe").error();
            msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
            msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
            if (secOutput == null) secOutput = ItemStack.EMPTY;
            if (secOutput.isEmpty()) {
                chance = 0;
            } else if (chance <= 0) {
                chance = 100;
            }
            if (energy <= 0) energy = 3000;
            return !msg.logIfNotEmpty();
        }

        @Override
        public @Nullable PulverizerManager.PulverizerRecipe register() {
            if (!validate()) return null;
            PulverizerManager.PulverizerRecipe recipe = null;
            for (ItemStack itemStack : input.getMatchingStacks()) {
                PulverizerManager.PulverizerRecipe recipe1 = PulverizerManager.addRecipe(energy, itemStack, output, secOutput, chance);
                ReloadableRegistryManager.markScriptRecipe(Pulverizer.class, recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
