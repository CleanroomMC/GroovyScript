package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.compat.enderio.recipe.EnderIORecipeBuilder;
import com.cleanroommc.groovyscript.compat.enderio.recipe.IEnderIORecipes;
import com.cleanroommc.groovyscript.compat.enderio.recipe.RecipeInput;
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
        public String getErrorMsg() {
            return "Error adding EnderIO Alloy Smelter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 3, 1, 1);
            validateFluids(msg);
            if (energy <= 0) energy = 5000;
            if (xp < 0) xp = 0;
        }

        @Override
        public @Nullable Void buildAndRegister() {
            if (!validate()) return null;
            AlloyRecipeManager.getInstance().addRecipe(true, ArrayUtils.mapToList(input, RecipeInput::new, new NNList<>()), output.get(0), energy, xp, level);
            return null;
        }
    }
}
