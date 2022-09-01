package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.mixin.thermalexpansion.EnchanterManagerAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Enchanter extends VirtualizedRegistry<EnchanterManager.EnchanterRecipe> {

    public Enchanter() {
        super("Enchanter", "enchanter");
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        Map<List<ComparableItemStackValidatedNBT>, EnchanterManager.EnchanterRecipe> map = EnchanterManagerAccessor.getRecipeMap();
        removeScripted().forEach(recipe -> map.values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> map.put(convertInput(r.getPrimaryInput(), r.getSecondaryInput()), r));
    }

    private static List<ComparableItemStackValidatedNBT> convertInput(ItemStack primary, ItemStack secondary) {
        List<ComparableItemStackValidatedNBT> input = new ArrayList<>();
        input.add(EnchanterManager.convertInput(primary));
        input.add(EnchanterManager.convertInput(secondary));
        return input;
    }

    public void add(EnchanterManager.EnchanterRecipe recipe) {
        if (!EnchanterManager.recipeExists(recipe.getPrimaryInput(), recipe.getSecondaryInput())) {
            EnchanterManagerAccessor.getRecipeMap().put(convertInput(recipe.getPrimaryInput(), recipe.getSecondaryInput()), recipe);
            addScripted(recipe);
        }
    }

    public EnchanterManager.EnchanterRecipe add(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack output, int experience, EnchanterManager.Type type) {
        EnchanterManager.EnchanterRecipe recipe = EnchanterManager.addRecipe(energy, primaryInput, secondaryInput, output, experience, type);
        if (recipe != null) {
            addScripted(recipe);
        }
        return recipe;
    }

    public boolean remove(EnchanterManager.EnchanterRecipe recipe) {
        if (EnchanterManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByInput(IIngredient input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Thermal Expansion Enchanter recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        boolean found = false;
        for (ItemStack stack : input.getMatchingStacks()) {
            EnchanterManager.EnchanterRecipe recipe = EnchanterManager.removeRecipe(stack);
            if (recipe != null) {
                found = true;
                addBackup(recipe);
            }
        }
        if (!found) {
            GroovyLog.msg("Error removing Thermal Expansion Enchanter recipe")
                    .add("could not find recipe for %s", input)
                    .error()
                    .post();
        }
    }

    public SimpleObjectStream<EnchanterManager.EnchanterRecipe> streamRecipes() {
        return new SimpleObjectStream<>(EnchanterManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    public static class RecipeBuilder extends EnergyRecipeBuilder<EnchanterManager.EnchanterRecipe> {
        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Enchanter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (energy <= 0) energy = 3000;
        }

        @Override
        public @Nullable EnchanterManager.EnchanterRecipe register() {
            if (!validate()) return null;
            EnchanterManager.EnchanterRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                EnchanterManager.EnchanterRecipe recipe1 = ModSupport.THERMAL_EXPANSION.get().charger.add(energy, itemStack, output.get(0));
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
