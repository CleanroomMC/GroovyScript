package com.cleanroommc.groovyscript.compat.mods.betterwithaddons;

import betterwithaddons.crafting.recipes.infuser.InfuserRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.AbstractCraftingRecipeBuilder;

public interface InfuserRecipeBuilder {

    @RecipeBuilderMethodDescription
    InfuserRecipeBuilder spirits(int spirits);

    class Shaped extends AbstractCraftingRecipeBuilder.AbstractShaped<InfuserRecipe> implements InfuserRecipeBuilder {

        @Property(comp = @Comp(gte = 0))
        protected int spirits;

        public Shaped() {
            super(3, 3);
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_infuser_shaped_";
        }

        @Override
        @RecipeBuilderMethodDescription
        public Shaped spirits(int spirits) {
            this.spirits = spirits;
            return this;
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shaped Infuser Crafting recipe").error();
            msg.add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined");
            msg.add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            msg.add(spirits < 0, "spirits must be a nonnegative integer, yet it was {}", spirits);
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public InfuserRecipe register() {
            if (!validate()) return null;
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shaped Infuser Crafting recipe").error();

            ShapedCraftingRecipe recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, ((width1, height1, ingredients) -> new ShapedCraftingRecipe(output, ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, ((width1, height1, ingredients) -> new ShapedCraftingRecipe(output.copy(), ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            }
            if (msg.postIfNotEmpty()) return null;

            InfuserRecipe external = new InfuserRecipe(recipe, this.spirits);
            ModSupport.BETTER_WITH_ADDONS.get().infuser.add(external);
            return external;
        }
    }

    class Shapeless extends AbstractCraftingRecipeBuilder.AbstractShapeless<InfuserRecipe> implements InfuserRecipeBuilder {

        @Property(comp = @Comp(gte = 0))
        protected int spirits;

        public Shapeless() {
            super(3, 3);
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_infuser_shapeless_";
        }

        @Override
        @RecipeBuilderMethodDescription
        public Shapeless spirits(int spirits) {
            this.spirits = spirits;
            return this;
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shapeless Infuser Crafting recipe").error();
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            msg.add(ingredients.isEmpty(), () -> "inputs must not be empty");
            msg.add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size());
            msg.add(spirits < 0, "spirits must be a nonnegative integer, yet it was {}", spirits);
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public InfuserRecipe register() {
            if (!validate()) return null;
            ShapelessCraftingRecipe recipe = new ShapelessCraftingRecipe(output, ingredients, recipeFunction, recipeAction);
            InfuserRecipe external = new InfuserRecipe(recipe, this.spirits);
            ModSupport.BETTER_WITH_ADDONS.get().infuser.add(external);
            return external;
        }
    }
}
