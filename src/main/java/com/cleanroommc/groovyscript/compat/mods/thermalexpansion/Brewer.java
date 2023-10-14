package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.core.util.helpers.FluidHelper;
import cofh.thermalexpansion.util.managers.machine.BrewerManager;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.BrewerManagerAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Brewer extends VirtualizedRegistry<BrewerManager.BrewerRecipe> {

    private final List<ComparableItemStackValidatedNBT> validationSetScripted = new ArrayList<>();
    private final List<ComparableItemStackValidatedNBT> validationSetBackup = new ArrayList<>();
    private final List<String> validationFluidsScripted = new ArrayList<>();
    private final List<String> validationFluidsBackup = new ArrayList<>();

    public Brewer() {
        super(VirtualizedRegistry.generateAliases("Imbuer"));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void removeByInput(IIngredient ingredient, FluidStack input) {
        if (GroovyLog.msg("Error removing Thermal Expansion Brewer recipe").error()
                .add(IngredientHelper.isEmpty(ingredient), () -> "item input must not be empty")
                .add(IngredientHelper.isEmpty(input), () -> "fluid input must not be empty")
                .postIfNotEmpty()) {
            boolean found = false;
            for (ItemStack itemStack : ingredient.getMatchingStacks()) {
                BrewerManager.BrewerRecipe recipe = BrewerManager.removeRecipe(itemStack, input);
                if (recipe != null) {
                    addBackup(recipe);
                    validationSetBackup.add(BrewerManager.convertInput(itemStack));
                    validationFluidsBackup.add(input.getFluid().getName());
                    found = true;
                }
            }
            if (!found) {
                GroovyLog.msg("Error removing Thermal Expansion Brewer recipe")
                        .add("could not find recipe for {} and {}", ingredient, input)
                        .error()
                        .post();
            }
        }
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        Map<List<Integer>, BrewerManager.BrewerRecipe> map = BrewerManagerAccessor.getRecipeMap();
        removeScripted().forEach(recipe -> map.values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> map.put(Arrays.asList(BrewerManager.convertInput(r.getInput()).hashCode(), FluidHelper.getFluidHash(r.getInputFluid())), r));
        validationSetScripted.forEach(input -> BrewerManagerAccessor.getValidationSet().remove(input));
        validationFluidsScripted.forEach(input -> BrewerManagerAccessor.getValidationFluids().remove(input));
        validationSetBackup.forEach(input -> BrewerManagerAccessor.getValidationSet().add(input));
        validationFluidsBackup.forEach(input -> BrewerManagerAccessor.getValidationFluids().add(input));
        validationSetScripted.clear();
        validationFluidsScripted.clear();
        validationSetBackup.clear();
        validationFluidsBackup.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<BrewerManager.BrewerRecipe> {

        private int energy;

        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Brewer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
            if (energy <= 0) energy = 4800;
        }

        @Override
        public @Nullable BrewerManager.BrewerRecipe register() {
            if (!validate()) return null;
            BrewerManager.BrewerRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                BrewerManager.BrewerRecipe recipe1 = BrewerManager.addRecipe(energy, itemStack, fluidInput.get(0), fluidOutput.get(0));
                Brewer brewer = ModSupport.THERMAL_EXPANSION.get().brewer;
                brewer.addScripted(recipe1);
                brewer.validationSetScripted.add(BrewerManager.convertInput(itemStack));
                brewer.validationFluidsScripted.add(fluidInput.get(0).getFluid().getName());
                if (recipe == null) {
                    recipe = recipe1;
                }
            }
            return recipe;
        }
    }
}
