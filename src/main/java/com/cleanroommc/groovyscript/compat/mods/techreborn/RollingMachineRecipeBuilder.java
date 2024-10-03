package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.AbstractCraftingRecipeBuilder;
import net.minecraft.item.crafting.IRecipe;

public interface RollingMachineRecipeBuilder {

    class Shaped extends AbstractCraftingRecipeBuilder.AbstractShaped<IRecipe> implements RollingMachineRecipeBuilder {

        public Shaped() {
            super(3, 3);
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_rolling_machine_shaped_";
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shaped Rolling Machine recipe").error();
            validateName();
            msg.add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined");
            msg.add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IRecipe register() {
            if (!validate()) return null;
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shaped Rolling Machine recipe").error();

            ShapedCraftingRecipe recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, ((width1, height1, ingredients) -> new ShapedCraftingRecipe(output, ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, ((width1, height1, ingredients) -> new ShapedCraftingRecipe(output.copy(), ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            }
            if (recipe == null) {
                msg.add("The recipe could not be parsed!");
            } else {
                recipe.setRegistryName(name);
            }
            if (msg.postIfNotEmpty()) return null;

            ModSupport.TECH_REBORN.get().rollingMachine.add(name, recipe);
            return recipe;
        }

    }

    class Shapeless extends AbstractCraftingRecipeBuilder.AbstractShapeless<IRecipe> implements RollingMachineRecipeBuilder {

        public Shapeless() {
            super(3, 3);
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_rolling_machine_shapeless_";
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shapeless Rolling Machine recipe").error();
            validateName();
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            msg.add(ingredients.isEmpty(), () -> "inputs must not be empty");
            msg.add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size());
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IRecipe register() {
            if (!validate()) return null;
            ShapelessCraftingRecipe recipe = new ShapelessCraftingRecipe(output.copy(), ingredients, recipeFunction, recipeAction);
            recipe.setRegistryName(name);
            ModSupport.TECH_REBORN.get().rollingMachine.add(name, recipe);
            return recipe;
        }

    }

}
