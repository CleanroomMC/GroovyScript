package com.cleanroommc.groovyscript.compat.thermal;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
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

    public static class RecipeBuilder implements IRecipeBuilder<PulverizerManager.PulverizerRecipe> {

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
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
