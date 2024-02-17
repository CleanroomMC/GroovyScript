package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.ChoppingBlockRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CompactingBinRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ChoppingBlock extends ForgeRegistryWrapper<ChoppingBlockRecipe> {

    public ChoppingBlock() {
        super(ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE, Alias.generateOfClass(ChoppingBlock.class));
    }


    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public boolean remove(ChoppingBlockRecipe recipe) {
        if (recipe == null) return false;
        remove(recipe.getRegistryName());
        return true;
    }

    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing chopping block recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (ChoppingBlockRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing chopping block recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (ChoppingBlockRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        };
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
            msg.add(ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);

        }

        @Override
        public @Nullable ChoppingBlockRecipe register() {
            if (!validate()) return null;
            ChoppingBlockRecipe recipe = new ChoppingBlockRecipe(output.get(0), input.get(0).toMcIngredient(), new int[]{chops}, new int[]{
                    quantities}).setRegistryName(name);
            PyroTech.choppingBlock.add(recipe);
            return recipe;
        }
    }
}
