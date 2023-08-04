package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.CrusherRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Crusher extends VirtualizedMekanismRegistry<CrusherRecipe> {

    public Crusher() {
        super(RecipeHandler.Recipe.CRUSHER);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public CrusherRecipe add(IIngredient ingredient, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Crusher recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        CrusherRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            CrusherRecipe recipe = new CrusherRecipe(itemStack.copy(), output);
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    public boolean removeByInput(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            removeError("input must not be empty");
            return false;
        }
        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            CrusherRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for {}", ingredient);
        }
        return found;
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CrusherRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Crusher recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        public @Nullable CrusherRecipe register() {
            if (!validate()) return null;
            CrusherRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                CrusherRecipe r = new CrusherRecipe(itemStack.copy(), output.get(0));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().crusher.add(r);
            }
            return recipe;
        }
    }
}
