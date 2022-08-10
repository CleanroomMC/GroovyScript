package com.cleanroommc.groovyscript.compat.thermal;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.mixin.thermal.PulverizerManagerAccessor;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Pulverizer {

    private static final Map<ComparableItemStackValidated, PulverizerManager.PulverizerRecipe> backupRecipeMap = new Object2ObjectOpenHashMap<>();

    public static void addRecipe(ComparableItemStackValidated input, PulverizerManager.PulverizerRecipe recipe) {
        if (!ReloadableRegistryManager.isShouldRegisterAsReloadable()) {
            backupRecipeMap.put(input, recipe);
        }
    }

    public static void onReload() {
        PulverizerManagerAccessor.getRecipeMap().clear();
        PulverizerManagerAccessor.getRecipeMap().putAll(backupRecipeMap);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.LOG.error("Error removing Thermal Expansion Pulverizer recipe for empty input!");
            return;
        }
        PulverizerManager.removeRecipe(input);
    }

    public void removeAll() {
        for (PulverizerManager.PulverizerRecipe recipe : PulverizerManager.getRecipeList()) {
            PulverizerManager.removeRecipe(recipe.getInput());
        }
    }

    public static class RecipeBuilder extends EnergyRecipeBuilder<PulverizerManager.PulverizerRecipe> {

        private ItemStack secOutput;
        private int chance;

        public RecipeBuilder secondaryOutput(ItemStack itemStack, int chance) {
            this.secOutput = itemStack;
            this.chance = chance;
            return this;
        }

        public RecipeBuilder secondaryOutput(ItemStack itemStack) {
            return secondaryOutput(itemStack, 100);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Pulverizer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (secOutput == null) secOutput = ItemStack.EMPTY;
            if (secOutput.isEmpty()) {
                chance = 0;
            } else if (chance <= 0) {
                chance = 100;
            }
            if (energy <= 0) energy = 3000;
        }

        @Override
        public @Nullable PulverizerManager.PulverizerRecipe buildAndRegister() {
            if (!validate()) return null;
            PulverizerManager.PulverizerRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                PulverizerManager.PulverizerRecipe recipe1 = PulverizerManager.addRecipe(energy, itemStack, output.get(0), secOutput, chance);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
