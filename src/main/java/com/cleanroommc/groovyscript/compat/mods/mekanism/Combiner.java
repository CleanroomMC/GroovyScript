package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.DoubleMachineInput;
import mekanism.common.recipe.machines.CombinerRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Combiner extends VirtualizedMekanismRegistry<CombinerRecipe> {

    public Combiner() {
        super(RecipeHandler.Recipe.COMBINER);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public CombinerRecipe add(IIngredient ingredient, ItemStack extra, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Crusher recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(extra), () -> "extra input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        extra = extra.copy();
        output = output.copy();
        CombinerRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            CombinerRecipe recipe = new CombinerRecipe(itemStack.copy(), extra, output);
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    public boolean removeByInput(IIngredient ingredient, ItemStack extra) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Combiner recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(extra), () -> "extra input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            CombinerRecipe recipe = recipeRegistry.get().remove(new DoubleMachineInput(itemStack, extra));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for {} and {}", ingredient, extra);
        }
        return found;
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CombinerRecipe> {

        private ItemStack extra;

        public RecipeBuilder extra(ItemStack extra) {
            this.extra = extra;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Combiner recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        public @Nullable CombinerRecipe register() {
            if (!validate()) return null;
            CombinerRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                CombinerRecipe r = new CombinerRecipe(itemStack.copy(), extra, output.get(0));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().combiner.add(r);
            }
            return recipe;
        }
    }

}
