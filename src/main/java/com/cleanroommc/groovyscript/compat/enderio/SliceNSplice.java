package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.enderio.recipe.EnderIORecipeBuilder;
import com.cleanroommc.groovyscript.compat.enderio.recipe.ManyToOneRecipe;
import com.cleanroommc.groovyscript.compat.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.compat.enderio.recipe.RecipeUtils;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.*;
import crazypants.enderio.base.recipe.slicensplice.SliceAndSpliceRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

    @GroovyBlacklist
    public void onReload() {
        ReloadableRegistryManager.unmarkScriptRecipes(SliceNSplice.class).forEach(SliceAndSpliceRecipeManager.getInstance().getRecipes()::remove);
        ReloadableRegistryManager.recoverRecipes(SliceNSplice.class)
                .stream()
                .map(IManyToOneRecipe.class::cast)
                .forEach(SliceAndSpliceRecipeManager.getInstance().getRecipes()::add);
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
            int inputSize = input.getRealSize();
            output.trim();
            msg.add(inputSize < 1 || inputSize > 6, () -> "Must have 1 - 6 inputs, but found " + input.size());
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
            RecipeOutput recipeOutput = new RecipeOutput(output.get(0), 1, xp);
            List<IRecipeInput> inputs = new ArrayList<>();
            for (int i = 0; i < input.size(); i++) {
                IIngredient ingredient = input.get(i);
                if (IngredientHelper.isEmpty(ingredient)) continue;
                inputs.add(new RecipeInput(ingredient, i));
            }
            Recipe recipe = new ManyToOneRecipe(recipeOutput, energy, RecipeBonusType.NONE, RecipeLevel.IGNORE, inputs.toArray(new IRecipeInput[0]));
            SliceAndSpliceRecipeManager.getInstance().addRecipe(recipe);
            NNList<IManyToOneRecipe> recipes = SliceAndSpliceRecipeManager.getInstance().getRecipes();
            ReloadableRegistryManager.markScriptRecipe(SliceNSplice.class, recipes.get(recipes.size() - 1)); // SliceAndSpliceRecipeManager wraps the Recipe
            return recipe;
        }
    }
}
