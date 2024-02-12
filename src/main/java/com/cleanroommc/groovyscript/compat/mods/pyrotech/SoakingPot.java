package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.SoakingPotRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.Nullable;

public class SoakingPot extends VirtualizedRegistry<SoakingPotRecipe> {
    @Override
    public void onReload() {
        ForgeRegistry<SoakingPotRecipe> registry = (ForgeRegistry<SoakingPotRecipe>) ModuleTechBasic.Registries.SOAKING_POT_RECIPE;
        if (registry.isLocked()) {
            registry.unfreeze();
        }
        removeScripted().forEach(recipe -> registry.remove(recipe.getRegistryName()));
        getBackupRecipes().forEach(registry::register);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(SoakingPotRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ModuleTechBasic.Registries.SOAKING_POT_RECIPE.register(recipe);
        }
    }

    public boolean remove(SoakingPotRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ModuleTechBasic.Registries.SOAKING_POT_RECIPE.remove(recipe.getRegistryName());
        return true;
    }

    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing soaking pot recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.SOAKING_POT_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getInputItem().test(input)) {
                addBackup(recipe);
                remove(recipe);
            }
        });
    }

    public void removeByOutput(ItemStack output) {
        if (GroovyLog.msg("Error removing soaking pot recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.SOAKING_POT_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getOutput().isItemEqual(output)) {
                addBackup(recipe);
                remove(recipe);
            }
        });
    }

    public void removeAll() {
        ModuleTechBasic.Registries.SOAKING_POT_RECIPE.getValuesCollection().forEach(this::addBackup);
        ModuleTechBasic.Registries.SOAKING_POT_RECIPE.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<SoakingPotRecipe> {

        private boolean campfireRequired;
        private int time;

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        public RecipeBuilder campfireRequired(boolean campfireRequired) {
            this.campfireRequired = campfireRequired;
            return this;
        }
        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Soaking Pot Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(name == null, "name cannot be null.");
        }

        @Override
        public @Nullable SoakingPotRecipe register() {
            if (!validate()) return null;
            SoakingPotRecipe recipe = new SoakingPotRecipe(output.get(0), input.get(0).toMcIngredient(), fluidInput.get(0), campfireRequired, time).setRegistryName(name);
            ModuleTechBasic.Registries.SOAKING_POT_RECIPE.register(recipe);
            return recipe;
        }
    }
}
