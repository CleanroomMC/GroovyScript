package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.ChargerManager;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.ChargerManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Charger extends VirtualizedRegistry<ChargerManager.ChargerRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(r -> ChargerManager.removeRecipe(r.getInput()));
        restoreFromBackup().forEach(r -> ChargerManagerAccessor.getRecipeMap().put(new ComparableItemStackValidatedNBT(r.getInput()), r));
    }

    public void add(ChargerManager.ChargerRecipe recipe) {
        ChargerManagerAccessor.getRecipeMap().put(new ComparableItemStackValidatedNBT(recipe.getInput()), recipe);
        addScripted(recipe);
    }

    public boolean remove(ChargerManager.ChargerRecipe recipe) {
        recipe = ChargerManager.removeRecipe(recipe.getInput());
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public List<ChargerManager.ChargerRecipe> add(IIngredient input, ItemStack output, int energy) {
        List<ChargerManager.ChargerRecipe> list = new ArrayList<>();
        for (ItemStack itemStack : input.getMatchingStacks()) {
            var recipe = ChargerManager.addRecipe(energy, itemStack, output);
            if (recipe != null) {
                addScripted(recipe);
                list.add(recipe);
            }
        }
        return list;
    }

    public boolean removeByInput(IIngredient input) {
        boolean result = false;
        for (ItemStack itemStack : input.getMatchingStacks()) {
            var recipe = ChargerManager.removeRecipe(itemStack);
            result |= recipe != null;
        }
        return result;
    }

    public void removeAll() {
        ChargerManagerAccessor.getRecipeMap().values().forEach(this::addBackup);
        ChargerManagerAccessor.getRecipeMap().clear();
    }

    public SimpleObjectStream<ChargerManager.ChargerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ChargerManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<List<ChargerManager.ChargerRecipe>> {

        private int energy = 3000;

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Charger recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (this.energy <= 0) this.energy = 3000;
        }

        @Override
        public @Nullable List<ChargerManager.ChargerRecipe> register() {
            return ModSupport.THERMAL_EXPANSION.get().charger.add(this.input.get(0), this.output.get(0), this.energy);
        }
    }
}
