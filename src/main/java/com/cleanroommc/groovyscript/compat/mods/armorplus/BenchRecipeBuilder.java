package com.cleanroommc.groovyscript.compat.mods.armorplus;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.AbstractCraftingRecipeBuilder;
import com.sofodev.armorplus.api.crafting.IRecipe;
import com.sofodev.armorplus.api.crafting.IShapedRecipe;

public interface BenchRecipeBuilder {

    class Shaped extends AbstractCraftingRecipeBuilder.AbstractShaped<IRecipe> implements BenchRecipeBuilder {

        private final AbstractBenchRegistry registry;

        public Shaped(AbstractBenchRegistry registry, int size) {
            super(size, size);
            this.registry = registry;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IRecipe register() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shaped Armor Plus Bench recipe")
                    .error()
                    .add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined")
                    .add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");
            if (msg.postIfNotEmpty()) return null;
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            IShapedRecipe recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, ((width1, height1, ingredients) -> BenchShapedRecipe.make(width, output, ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, ((width1, height1, ingredients) -> BenchShapedRecipe.make(width, output.copy(), ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            }
            if (msg.postIfNotEmpty()) return null;
            if (recipe != null) {
                registry.add(recipe);
            }
            return recipe;
        }
    }

    class Shapeless extends AbstractCraftingRecipeBuilder.AbstractShapeless<IRecipe> implements BenchRecipeBuilder {

        private final AbstractBenchRegistry registry;

        public Shapeless(AbstractBenchRegistry registry, int size) {
            super(size, size);
            this.registry = registry;
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shapeless Armor Plus Bench recipe").error();
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            msg.add(ingredients.isEmpty(), () -> "inputs must not be empty");
            msg.add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size());
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IRecipe register() {
            if (!validate()) return null;
            IRecipe recipe = BenchShapelessRecipe.make(output.copy(), ingredients, recipeFunction, recipeAction);
            registry.add(recipe);
            return recipe;
        }
    }
}
