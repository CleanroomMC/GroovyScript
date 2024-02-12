package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.ChoppingBlockRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.Nullable;

public class ChoppingBlock extends VirtualizedRegistry<ChoppingBlockRecipe> {
    @Override
    public void onReload() {
        ForgeRegistry<ChoppingBlockRecipe> registry = (ForgeRegistry<ChoppingBlockRecipe>) ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE;
        if (registry.isLocked()) {
            registry.unfreeze();
        }
        removeScripted().forEach(recipe -> registry.remove(recipe.getRegistryName()));
        getBackupRecipes().forEach(registry::register);
    }


    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(ChoppingBlockRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE.register(recipe);
        }
    }

    public boolean remove(ChoppingBlockRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE.remove(recipe.getRegistryName());
        return true;
    }

    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing chopping block recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getInput().test(input)) {
                addBackup(recipe);
                remove(recipe);
            }
        });
    }

    public void removeByOutput(ItemStack output) {
        if (GroovyLog.msg("Error removing chopping block recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getOutput().isItemEqual(output)) {
                addBackup(recipe);
                remove(recipe);
            }
        });
    }

    public void removeAll() {
        ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE.getValuesCollection().forEach(this::addBackup);
        ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<ChoppingBlockRecipe> {

        private int chops;
        private int quantities;

        public RecipeBuilder chops(int chops) {
            this.chops = chops;
            return this;
        }

        public RecipeBuilder quantity(int quantities) {
            this.quantities = quantities;
            return this;
        }
        
        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Chopping Block Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(quantities < 0, "quantities must be a non negative integer, yet it was {}", quantities);
            msg.add(chops < 0, "chops must be a non negative integer, yet it was {}", chops);
            msg.add(name == null, "name cannot be null.");


        }

        @Override
        public @Nullable ChoppingBlockRecipe register() {
            if (!validate()) return null;
            ChoppingBlockRecipe recipe = new ChoppingBlockRecipe(output.get(0), input.get(0).toMcIngredient(), new int[]{chops}, new int[]{quantities}).setRegistryName(name);
            ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE.register(recipe);
            return recipe;
        }
    }
}
