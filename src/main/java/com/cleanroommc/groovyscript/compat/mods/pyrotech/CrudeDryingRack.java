package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CrudeDryingRackRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.Nullable;

public class CrudeDryingRack extends VirtualizedRegistry<CrudeDryingRackRecipe> {
    @Override
    public void onReload() {
        ForgeRegistry<CrudeDryingRackRecipe> registry = (ForgeRegistry<CrudeDryingRackRecipe>) ModuleTechBasic.Registries.CRUDE_DRYING_RACK_RECIPE;
        if (registry.isLocked()) {
            registry.unfreeze();
        }
        removeScripted().forEach(recipe -> registry.remove(recipe.getRegistryName()));
        getBackupRecipes().forEach(registry::register);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(CrudeDryingRackRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ModuleTechBasic.Registries.CRUDE_DRYING_RACK_RECIPE.register(recipe);
        }
    }

    public boolean remove(CrudeDryingRackRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ModuleTechBasic.Registries.CRUDE_DRYING_RACK_RECIPE.remove(recipe.getRegistryName());
        return true;
    }

    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing crude drying rack recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.CRUDE_DRYING_RACK_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getInput().test(input)) {
                addBackup(recipe);
                remove(recipe);
            }
        });
    }

    public void removeByOutput(ItemStack output) {
        if (GroovyLog.msg("Error removing crude drying rack recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.CRUDE_DRYING_RACK_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getOutput().isItemEqual(output)) {
                addBackup(recipe);
                remove(recipe);
            }
        });
    }

    public void removeAll() {
        ModuleTechBasic.Registries.CRUDE_DRYING_RACK_RECIPE.getValuesCollection().forEach(this::addBackup);
        ModuleTechBasic.Registries.CRUDE_DRYING_RACK_RECIPE.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CrudeDryingRackRecipe> {

        private int dryTime;

        public RecipeBuilder dryTime(int time) {
            this.dryTime = time;
            return this;
        }
        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Crude Drying Rack Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(dryTime < 0, "dryTime must be a non negative integer, yet it was {}", dryTime);
            msg.add(name == null, "name cannot be null.");
        }

        @Override
        public @Nullable CrudeDryingRackRecipe register() {
            if (!validate()) return null;
            CrudeDryingRackRecipe recipe = new CrudeDryingRackRecipe(output.get(0), input.get(0).toMcIngredient(), dryTime).setRegistryName(name);
            ModuleTechBasic.Registries.CRUDE_DRYING_RACK_RECIPE.register(recipe);
            return recipe;
        }
    }
}
