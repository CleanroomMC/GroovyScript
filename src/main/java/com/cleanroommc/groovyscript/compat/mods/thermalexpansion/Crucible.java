package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.CrucibleManagerAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Crucible extends VirtualizedRegistry<CrucibleManager.CrucibleRecipe> {

    private final List<ComparableItemStackValidatedNBT> lavaSetScripted = new ArrayList<>();
    private final List<ComparableItemStackValidatedNBT> lavaSetBackup = new ArrayList<>();

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        Map<ComparableItemStackValidatedNBT, CrucibleManager.CrucibleRecipe> map = CrucibleManagerAccessor.getRecipeMap();
        removeScripted().forEach(recipe -> map.values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> map.put(CrucibleManager.convertInput(r.getInput()), r));
        lavaSetScripted.forEach(input -> CrucibleManagerAccessor.getLavaSet().remove(input));
        lavaSetBackup.forEach(input -> CrucibleManagerAccessor.getLavaSet().add(input));
        lavaSetScripted.clear();
        lavaSetBackup.clear();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void removeByInput(IIngredient input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Thermal Expansion Brewer recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }

        boolean found = false;
        for (ItemStack itemStack : input.getMatchingStacks()) {
            CrucibleManager.CrucibleRecipe recipe = CrucibleManager.removeRecipe(itemStack);
            if (recipe != null) {
                addBackup(recipe);
                if (recipe.getOutput().getFluid() == FluidRegistry.LAVA) {
                    lavaSetBackup.add(CrucibleManager.convertInput(recipe.getInput()));
                }
                found = true;
            }
        }
        if (!found) {
            GroovyLog.msg("Error removing Thermal Expansion Brewer recipe")
                    .add("could not find recipe for {}", input)
                    .error()
                    .post();
        }

    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CrucibleManager.CrucibleRecipe> {

        private int energy;

        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Crucible recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
            if (energy <= 0) energy = 10000;
        }

        @Override
        public @Nullable CrucibleManager.CrucibleRecipe register() {
            if (!validate()) return null;
            CrucibleManager.CrucibleRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                CrucibleManager.CrucibleRecipe recipe1 = CrucibleManager.addRecipe(energy, itemStack, fluidOutput.get(0));
                Crucible crucible = ModSupport.THERMAL_EXPANSION.get().crucible;
                crucible.addScripted(recipe1);
                if (recipe1.getOutput().getFluid() == FluidRegistry.LAVA) {
                    crucible.lavaSetScripted.add(CrucibleManager.convertInput(itemStack));
                }
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
