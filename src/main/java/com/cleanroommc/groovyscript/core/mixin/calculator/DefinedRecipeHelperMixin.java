package com.cleanroommc.groovyscript.core.mixin.calculator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.calculator.mod.common.recipes.ResearchRecipeType;
import sonar.core.recipes.DefinedRecipeHelper;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.List;

@Mixin(value = DefinedRecipeHelper.class, remap = false)
public abstract class DefinedRecipeHelperMixin extends RecipeHelperV2Mixin {

    @Override
    public void buildRecipe(List<ISonarRecipeObject> recipeInputs, List<ISonarRecipeObject> recipeOutputs, List<?> additionals, boolean shapeless, CallbackInfoReturnable<CalculatorRecipe> cir) {
        cir.setReturnValue(new CalculatorRecipe(recipeInputs, recipeOutputs, ResearchRecipeType.NONE, shapeless));
    }

}
