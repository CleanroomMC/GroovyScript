package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.machine.ChargerManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager.PulverizerRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.mixin.thermalexpansion.ChargerManagerAccessor;
import com.cleanroommc.groovyscript.mixin.thermalexpansion.PulverizerManagerAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Charger extends VirtualizedRegistry<ChargerManager.ChargerRecipe> {

    public Charger() {
        super("Charger", "charger");
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        Map<ComparableItemStackValidated, ChargerManager.ChargerRecipe> map = ChargerManagerAccessor.getRecipeMap();
        removeScripted().forEach(recipe -> map.values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> map.put(new ComparableItemStackValidated(r.getInput()), r));
    }

    public void add(ChargerManager.ChargerRecipe recipe) {
        if (!PulverizerManager.recipeExists(recipe.getInput())) {
            ChargerManagerAccessor.getRecipeMap().put(new ComparableItemStackValidated(recipe.getInput()), recipe);
            addScripted(recipe);
        }
    }

    public ChargerManager.ChargerRecipe add(int energy, ItemStack input, ItemStack output) {
        ChargerManager.ChargerRecipe recipe = ChargerManager.addRecipe(energy, input, output);
        if (recipe != null) {
            addScripted(recipe);
        }
        return recipe;
    }

    public boolean remove(ChargerManager.ChargerRecipe recipe) {
        if (ChargerManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByInput(IIngredient input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Thermal Expansion Charger recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        boolean found = false;
        for (ItemStack stack : input.getMatchingStacks()) {
            ChargerManager.ChargerRecipe recipe = ChargerManager.removeRecipe(stack);
            if (recipe != null) {
                found = true;
                addBackup(recipe);
            }
        }
        if (!found) {
            GroovyLog.msg("Error removing Thermal Expansion Charger recipe")
                    .add("could not find recipe for %s", input)
                    .error()
                    .post();
        }
    }

    public SimpleObjectStream<ChargerManager.ChargerRecipe> stream() {
        return new SimpleObjectStream<>(ChargerManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    public static class RecipeBuilder extends EnergyRecipeBuilder<ChargerManager.ChargerRecipe> {
        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Charger recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (energy <= 0) energy = 3000;
        }

        @Override
        public @Nullable ChargerManager.ChargerRecipe register() {
            if (!validate()) return null;
            ChargerManager.ChargerRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                ChargerManager.ChargerRecipe recipe1 = ModSupport.THERMAL_EXPANSION.get().charger.add(energy, itemStack, output.get(0));
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
