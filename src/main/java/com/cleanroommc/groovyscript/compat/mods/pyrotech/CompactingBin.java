package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CompactingBinRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.Nullable;

public class CompactingBin extends VirtualizedRegistry<CompactingBinRecipe> {
    @Override
    public void onReload() {
        ForgeRegistry<CompactingBinRecipe> registry = (ForgeRegistry<CompactingBinRecipe>) ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE;
        if (registry.isLocked()) {
            registry.unfreeze();
        }
        removeScripted().forEach(recipe -> registry.remove(recipe.getRegistryName()));
        getBackupRecipes().forEach(registry::register);
    }


    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(CompactingBinRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.register(recipe);
        }
    }

    public boolean remove(CompactingBinRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.remove(recipe.getRegistryName());
        return true;
    }

    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing compacting bin recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getInput().test(input)) {
                addBackup(recipe);
                remove(recipe);
            }
        });
    }

    public void removeByOutput(ItemStack output) {
        if (GroovyLog.msg("Error removing compacting bin recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getOutput().isItemEqual(output)) {
                addBackup(recipe);
                remove(recipe);
            }
        });
    }

    public void removeAll() {
        ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.getValuesCollection().forEach(this::addBackup);
        ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CompactingBinRecipe> {

        private int toolUses;

        public RecipeBuilder toolUses(int toolUses) {
            this.toolUses = toolUses;
            return this;
        }
        

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Compacting Bin Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(toolUses < 0, "toolUses must be a non negative integer, yet it was {}", toolUses);
            msg.add(name == null, "name cannot be null.");
        }

        @Override
        public @Nullable CompactingBinRecipe register() {
            if (!validate()) return null;
            CompactingBinRecipe recipe = new CompactingBinRecipe(output.get(0), input.get(0).toMcIngredient(), toolUses).setRegistryName(name);
            ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.register(recipe);
            return recipe;
        }
    }
}
