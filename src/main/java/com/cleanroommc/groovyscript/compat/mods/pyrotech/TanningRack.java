package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.TanningRackRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.Nullable;

public class TanningRack extends VirtualizedRegistry<TanningRackRecipe> {
    @Override
    public void onReload() {
        ForgeRegistry<TanningRackRecipe> registry = (ForgeRegistry<TanningRackRecipe>) ModuleTechBasic.Registries.TANNING_RACK_RECIPE;
        if (registry.isLocked()) {
            registry.unfreeze();
        }
        getScriptedRecipes().forEach(recipe -> {
            registry.remove(recipe.getRegistryName());
        });
        getBackupRecipes().forEach(registry::register);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(TanningRackRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ModuleTechBasic.Registries.TANNING_RACK_RECIPE.register(recipe);
        }
    }

    public boolean remove(TanningRackRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ModuleTechBasic.Registries.TANNING_RACK_RECIPE.remove(recipe.getRegistryName());
        return true;
    }

    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing tanning rack recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.TANNING_RACK_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getInputItem().test(input)) {
                remove(recipe);
            }
        });
    }

    public void removeByOutput(ItemStack output) {
        if (GroovyLog.msg("Error removing tanning rack recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.TANNING_RACK_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getOutput().isItemEqual(output)) {
                remove(recipe);
            }
        });
    }

    public void removeAll() {
        ModuleTechBasic.Registries.TANNING_RACK_RECIPE.getValuesCollection().forEach(this::addBackup);
        ModuleTechBasic.Registries.TANNING_RACK_RECIPE.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<TanningRackRecipe> {

        private int dryTime;
        private ItemStack failureItem;

        public RecipeBuilder failureItem(ItemStack stack) {
            this.failureItem = stack;
            return this;
        }

        public RecipeBuilder dryTime(int time) {
            this.dryTime = time;
            return this;
        }
        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Tanning Rack Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(dryTime < 0, "dryTime must be a non negative integer, yet it was {}", dryTime);
            msg.add(name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.TANNING_RACK_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @Override
        public @Nullable TanningRackRecipe register() {
            if (!validate()) return null;
            TanningRackRecipe recipe = new TanningRackRecipe(output.get(0), input.get(0).toMcIngredient(), failureItem, dryTime).setRegistryName(name);
            PyroTech.tanningRack.add(recipe);
            return recipe;
        }
    }
}
