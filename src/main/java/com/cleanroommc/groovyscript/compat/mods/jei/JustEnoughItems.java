package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;

public class JustEnoughItems extends ModPropertyContainer {

    public void hide(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            GroovyLog.msg("Error hiding items {}", ingredient)
                    .add("Items must not be empty")
                    .error()
                    .post();
            return;
        }
        if (IngredientHelper.isFluid(ingredient)) {
            JeiPlugin.HIDDEN_FLUIDS.add(IngredientHelper.toFluidStack(ingredient));
        } else {
            JeiPlugin.hideItem(ingredient.getMatchingStacks());
        }
    }

    public void removeAndHide(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            GroovyLog.msg("Error remove and hide items {}", ingredient)
                    .add("Items must not be empty")
                    .error()
                    .post();
            return;
        }
        VanillaModule.crafting.removeByOutput(ingredient, false);
        JeiPlugin.hideItem(ingredient.getMatchingStacks());
    }

    public void yeet(IIngredient ingredient) {
        removeAndHide(ingredient);
    }

    public void hideCategory(String category) {
        if (category == null || category.isEmpty()) {
            GroovyLog.msg("Error hiding category")
                    .add("category must not be empty")
                    .error()
                    .post();
            return;
        }
        JeiPlugin.HIDDEN_CATEGORY.add(category);
    }
}
