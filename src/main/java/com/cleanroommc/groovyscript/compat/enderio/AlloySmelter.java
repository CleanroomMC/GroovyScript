package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AlloySmelter {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void remove(ItemStack output) {
        ((IEnderIORecipes) AlloyRecipeManager.getInstance()).removeRecipes(output);
    }

    public static class RecipeBuilder extends EnderIORecipeBuilder<Void> {

        private float xp;

        public RecipeBuilder xpChance(float xp) {
            this.xp = xp;
            return this;
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = new GroovyLog.Msg("Error adding EnderIO Alloy Smelter recipe").error();
            msg.add(input.size() < 1 || input.size() > 3, () -> "Must have 1 - 3 inputs, but found " + input.size());
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
        public @Nullable Void register() {
            if (!validate()) return null;
            AlloyRecipeManager.getInstance().addRecipe(true, ArrayUtils.mapToList(input, RecipeInput::new, new NNList<>()), output.get(0), energy, xp, level);
            return null;
        }
    }
}
