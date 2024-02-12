package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.AnvilRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Anvil extends VirtualizedRegistry<AnvilRecipe> {
    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ModuleTechBasic.Registries.ANVIL_RECIPE.remove(recipe.getRegistryName()));
        getBackupRecipes().forEach(ModuleTechBasic.Registries.ANVIL_RECIPE::register);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(AnvilRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ModuleTechBasic.Registries.ANVIL_RECIPE.register(recipe);
        }
    }

    public boolean remove(AnvilRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ModuleTechBasic.Registries.ANVIL_RECIPE.remove(recipe.getRegistryName());
        return true;
    }

    public void removeByOutput(ItemStack output) {
        if (GroovyLog.msg("Error removing pyrotech anvil recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.ANVIL_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getOutput().isItemEqual(output)) {
                addBackup(recipe);
                remove(recipe);
            }
        });
    }

    public void removeAll() {
        ModuleTechBasic.Registries.ANVIL_RECIPE.getValuesCollection().forEach(this::addBackup);
        ModuleTechBasic.Registries.ANVIL_RECIPE.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<AnvilRecipe> {

        private int hits;
        private AnvilRecipe.EnumType type;
        private AnvilRecipe.EnumTier tier;


        public RecipeBuilder hits(int hits) {
            this.hits = hits;
            return this;
        }

        public RecipeBuilder type(AnvilRecipe.EnumType type) {
            this.type = type;
            return this;
        }
        public RecipeBuilder tier(AnvilRecipe.EnumTier tier) {
            this.tier = tier;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Anvil Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(hits < 0, "duration must be a non negative integer, yet it was {}", hits);
            msg.add(type == null, "type cannot be null. ");
            msg.add(tier == null, "tier cannot be null.");
            msg.add(name == null, "name cannot be null.");
        }

        @Override
        public @Nullable AnvilRecipe register() {
            if (!validate()) return null;

            AnvilRecipe recipe = new AnvilRecipe(output.get(0), input.get(0).toMcIngredient(), hits, type, tier).setRegistryName(name);
            ModuleTechBasic.Registries.ANVIL_RECIPE.register(recipe);
            return recipe;
        }
    }
}
