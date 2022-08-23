package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager.PulverizerRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.mixin.thermalexpansion.PulverizerManagerAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Pulverizer extends VirtualizedRegistry<PulverizerRecipe> {

    public Pulverizer() {
        super("Pulverizer", "pulverizer");
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        Map<ComparableItemStackValidated, PulverizerRecipe> map = PulverizerManagerAccessor.getRecipeMap();
        removeScripted().forEach(recipe -> map.values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> map.put(PulverizerManager.convertInput(r.getInput()), r));
    }

    public void add(PulverizerRecipe recipe) {
        if (!PulverizerManager.recipeExists(recipe.getInput())) {
            PulverizerManagerAccessor.getRecipeMap().put(PulverizerManager.convertInput(recipe.getInput()), recipe);
            addScripted(recipe);
        }
    }

    public PulverizerRecipe add(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {
        PulverizerRecipe recipe = PulverizerManager.addRecipe(energy, input, primaryOutput, secondaryOutput, secondaryChance);
        if (recipe != null) {
            addScripted(recipe);
        }
        return recipe;
    }

    public void remove(PulverizerRecipe recipe) {
        if (PulverizerManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe)) {
            addBackup(recipe);
        }
    }

    public void removeByInput(IIngredient input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.LOG.error("Error removing Thermal Expansion Pulverizer recipe for empty input!");
            return;
        }
        for (ItemStack stack : input.getMatchingStacks()) {
            addBackup(PulverizerManager.removeRecipe(stack));
        }
    }

    public void removeAll() {
        for (PulverizerManager.PulverizerRecipe recipe : PulverizerManager.getRecipeList()) {
            addBackup(PulverizerManager.removeRecipe(recipe.getInput()));
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
        public @Nullable PulverizerManager.PulverizerRecipe register() {
            if (!validate()) return null;
            PulverizerManager.PulverizerRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                PulverizerRecipe recipe1 = ModSupport.THERMAL_EXPANSION.get().pulverizer.add(energy, itemStack, output.get(0), secOutput, chance);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
