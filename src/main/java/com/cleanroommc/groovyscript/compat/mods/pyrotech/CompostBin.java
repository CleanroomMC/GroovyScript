package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CompostBinRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.KilnPitRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CompostBin extends ForgeRegistryWrapper<CompostBinRecipe> {


    public CompostBin() {
        super(ModuleTechBasic.Registries.COMPOST_BIN_RECIPE, Alias.generateOfClass(CompostBin.class));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }


    public boolean remove(CompostBinRecipe recipe) {
        if (recipe == null) return false;
        remove(recipe.getRegistryName());
        return true;
    }

    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing compost bin recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CompostBinRecipe recipe : getRegistry()) {
            if (recipe.getInput().isItemEqual(input)) {
                remove(recipe);
            }
        }
    }

    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing compost bin recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CompostBinRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        };
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CompostBinRecipe> {

        private int compostValue;

        public RecipeBuilder compostValue(int compostValue) {
            this.compostValue = compostValue;
            return this;
        }


        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Compacting Bin Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(compostValue < 0, "compostValue must be a non negative integer, yet it was {}", compostValue);
            msg.add(name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);

        }

        @Override
        public @Nullable CompostBinRecipe register() {
            if (!validate()) return null;
            CompostBinRecipe recipe = new CompostBinRecipe(output.get(0), IngredientHelper.toItemStack(input.get(0)), compostValue).setRegistryName(name);
            PyroTech.compostBin.add(recipe);
            return recipe;
        }
    }

}
