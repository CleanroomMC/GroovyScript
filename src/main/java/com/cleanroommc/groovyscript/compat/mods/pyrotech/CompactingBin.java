package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CompactingBinRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.KilnPitRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CompactingBin extends ForgeRegistryWrapper<CompactingBinRecipe> {


    public CompactingBin() {
        super(ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE, Alias.generateOfClass(CompactingBin.class));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public boolean remove(CompactingBinRecipe recipe) {
        if (recipe == null) return false;
        remove(recipe.getRegistryName());
        return true;
    }

    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing compacting bin recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CompactingBinRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing compacting bin recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CompactingBinRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
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
            msg.add(ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);

        }

        @Override
        public @Nullable CompactingBinRecipe register() {
            if (!validate()) return null;
            CompactingBinRecipe recipe = new CompactingBinRecipe(output.get(0), input.get(0).toMcIngredient(), toolUses).setRegistryName(name);
            PyroTech.compactingBin.add(recipe);
            return recipe;
        }
    }
}
