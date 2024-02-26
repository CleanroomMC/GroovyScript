package com.cleanroommc.groovyscript.core.mixin.calculator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.core.recipes.ISonarRecipeObject;
import sonar.core.recipes.RecipeHelperV2;

import java.util.List;

@Mixin(value = RecipeHelperV2.class, remap = false)
public class RecipeHelperV2Mixin {

    @Inject(method = "buildRecipe", at = @At("RETURN"), cancellable = true)
    public void buildRecipe(List<ISonarRecipeObject> recipeInputs, List<ISonarRecipeObject> recipeOutputs, List<?> additionals, boolean shapeless, CallbackInfoReturnable<CalculatorRecipe> cir) {
    }

}
