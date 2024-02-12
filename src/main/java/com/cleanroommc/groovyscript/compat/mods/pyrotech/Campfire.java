package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CampfireRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.Nullable;

public class Campfire extends VirtualizedRegistry<CampfireRecipe> {
    @Override
    public void onReload() {
        ForgeRegistry<CampfireRecipe> registry = (ForgeRegistry<CampfireRecipe>) ModuleTechBasic.Registries.CAMPFIRE_RECIPE;
        if (registry.isLocked()) {
            registry.unfreeze();
        }
        removeScripted().forEach(recipe -> registry.remove(recipe.getRegistryName()));
        getBackupRecipes().forEach(registry::register);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(CampfireRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ModuleTechBasic.Registries.CAMPFIRE_RECIPE.register(recipe);
        }
    }

    public boolean remove(CampfireRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ModuleTechBasic.Registries.CAMPFIRE_RECIPE.remove(recipe.getRegistryName());
        return true;
    }

    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing campfire recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.CAMPFIRE_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getInput().test(input)) {
                addBackup(recipe);
                remove(recipe);
            }
        });
    }

    public void removeByOutput(ItemStack output) {
        if (GroovyLog.msg("Error removing campfire recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.CAMPFIRE_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getOutput().isItemEqual(output)) {
                addBackup(recipe);
                remove(recipe);
            }
        });
    }

    public void removeAll() {
        ModuleTechBasic.Registries.CAMPFIRE_RECIPE.getValuesCollection().forEach(this::addBackup);
        ModuleTechBasic.Registries.CAMPFIRE_RECIPE.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CampfireRecipe> {

        private int duration;

        public RecipeBuilder duration(int time) {
            this.duration = time;
            return this;
        }
        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Campfire Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(duration < 0, "duration must be a non negative integer, yet it was {}", duration);
            msg.add(name == null, "name cannot be null.");
        }

        @Override
        public @Nullable CampfireRecipe register() {
            if (!validate()) return null;
            CampfireRecipe recipe = new CampfireRecipe(output.get(0), input.get(0).toMcIngredient(), duration).setRegistryName(name);
            ModuleTechBasic.Registries.CAMPFIRE_RECIPE.register(recipe);
            return recipe;
        }
    }
}
