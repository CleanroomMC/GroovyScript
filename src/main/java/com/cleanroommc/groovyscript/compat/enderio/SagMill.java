package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.compat.enderio.recipe.EnderIORecipeBuilder;
import com.cleanroommc.groovyscript.compat.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.compat.enderio.recipe.SagRecipe;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.mixin.enderio.SagMillRecipeManagerAccessor;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class SagMill {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void removeByInput(ItemStack input) {
        Recipe recipe = (Recipe) SagMillRecipeManager.getInstance().getRecipeForInput(RecipeLevel.IGNORE, input);
        if (recipe == null) {
            GroovyLog.LOG.error("Can't find EnderIO Sag Mill recipe for input " + input);
        } else {
            ((SagMillRecipeManagerAccessor) (Object) SagMillRecipeManager.getInstance()).getRecipes().remove(recipe);
        }
    }

    @GroovyBlacklist
    public void onReload() {
        SagMillRecipeManagerAccessor accessor = (SagMillRecipeManagerAccessor) (Object) SagMillRecipeManager.getInstance();
        ReloadableRegistryManager.unmarkScriptRecipes(SagMill.class).forEach(accessor.getRecipes()::remove);
        ReloadableRegistryManager.recoverRecipes(SagMill.class).stream().map(Recipe.class::cast).forEach(accessor.getRecipes()::addAll);
    }

    public static class RecipeBuilder extends EnderIORecipeBuilder<Recipe> {

        private final FloatList chances = new FloatArrayList();
        private RecipeBonusType bonusType = RecipeBonusType.NONE;

        public RecipeBuilder output(ItemStack itemStack, float chance) {
            this.output.add(itemStack);
            this.chances.add(Math.max(0, chance));
            return this;
        }

        @Override
        public AbstractRecipeBuilder<Recipe> output(ItemStack output) {
            return output(output, 1f);
        }

        @Override
        public AbstractRecipeBuilder<Recipe> output(ItemStack... outputs) {
            for (ItemStack output : outputs) {
                output(output);
            }
            return this;
        }

        @Override
        public AbstractRecipeBuilder<Recipe> output(Collection<ItemStack> outputs) {
            for (ItemStack output : outputs) {
                output(output);
            }
            return this;
        }

        public RecipeBuilder bonusTypeNone() {
            this.bonusType = RecipeBonusType.NONE;
            return this;
        }

        public RecipeBuilder bonusTypeMultiply() {
            this.bonusType = RecipeBonusType.MULTIPLY_OUTPUT;
            return this;
        }

        public RecipeBuilder bonusTypeChance() {
            this.bonusType = RecipeBonusType.CHANCE_ONLY;
            return this;
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = new GroovyLog.Msg("Error adding EnderIO Sag Mill recipe").error();
            input.trim();
            output.trim();
            msg.add(input.size() != 1, () -> "Must have exactly 1 input, but found " + input.size());
            msg.add(output.size() < 1 || output.size() > 4, () -> "Must have 1 - 4 outputs, but found " + output.size());

            if (energy <= 0) energy = 5000;

            if (msg.hasSubMessages()) {
                GroovyLog.LOG.log(msg);
                return false;
            }
            return true;
        }

        @Override
        public @Nullable Recipe register() {
            if (!validate()) return null;
            RecipeOutput[] outputs = new RecipeOutput[output.size()];
            for (int i = 0; i < outputs.length; i++) {
                outputs[i] = new RecipeOutput(output.get(i), chances.get(i));
            }
            Recipe recipe = new SagRecipe(new RecipeInput(input.get(0)), energy, bonusType, level, outputs);
            ReloadableRegistryManager.markScriptRecipe(SagMill.class, recipe);
            SagMillRecipeManager.getInstance().addRecipe(recipe);
            return recipe;
        }
    }
}
