package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import crazypants.enderio.base.recipe.*;
import crazypants.enderio.base.recipe.slicensplice.SliceAndSpliceRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class SliceNSplice {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(ItemStack output, List<IIngredient> input, int energy) {
        recipeBuilder()
                .energy(energy)
                .output(output)
                .input(input)
                .register();
    }

    public void remove(ItemStack output) {
        int oldSize = SliceAndSpliceRecipeManager.getInstance().getRecipes().size();
        SliceAndSpliceRecipeManager.getInstance().getRecipes().removeIf(recipe -> OreDictionary.itemMatches(output, recipe.getOutput(), false));
        if (oldSize == SliceAndSpliceRecipeManager.getInstance().getRecipes().size()) {
            GroovyLog.LOG.error("No EnderIO Slice'n'Splice recipe found for " + output.getDisplayName());
        }
    }

    public void removeByInput(List<ItemStack> input) {
        IRecipe recipe = SliceAndSpliceRecipeManager.getInstance().getRecipeForInputs(RecipeLevel.IGNORE, RecipeUtils.getMachineInputs(input));
        if (recipe instanceof IManyToOneRecipe) {
            SliceAndSpliceRecipeManager.getInstance().getRecipes().remove(recipe);
        } else {
            GroovyLog.LOG.error("No EnderIO Slice'n'Splice recipe found for " + input);
        }
    }

    public static class RecipeBuilder extends EnderIORecipeBuilder<IRecipe> {

        private float xp;

        public RecipeBuilder xp(float xp) {
            this.xp = xp;
            return this;
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = new GroovyLog.Msg("Error adding EnderIO Slice'n'Splice recipe").error();
            msg.add(input.size() < 1 || input.size() > 6, () -> "Must have 1 - 6 inputs, but found " + input.size());
            msg.add(output.size() != 1, () -> "Must have exactly 1 output, but found " + output.size());

            if (energy <= 0) energy = 5000;
            if (xp < 0) xp = 0;

            if (msg.hasSubMessages()) {
                GroovyLog.LOG.log(msg);
                return false;
            }
            return true;
        }

        @Override
        public @Nullable IRecipe register() {
            if (!validate()) return null;
            RecipeOutput recipeOutput = new RecipeOutput(output.get(0), xp);
            Recipe recipe = new Recipe(ArrayUtils.map(input, RecipeInput::new, new RecipeInput[0]), new RecipeOutput[]{recipeOutput}, energy, RecipeBonusType.NONE, RecipeLevel.IGNORE);
            SliceAndSpliceRecipeManager.getInstance().addRecipe(recipe);
            return recipe;
        }
    }
}
