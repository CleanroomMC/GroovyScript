package com.cleanroommc.groovyscript.compat.mods.avaritia;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.AbstractCraftingRecipeBuilder;
import morph.avaritia.recipe.extreme.IExtremeRecipe;

public interface ExtremeRecipeBuilder {

    @Property(property = "ingredientMatrix", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "81", type = Comp.Type.LTE)})
    class Shaped extends AbstractCraftingRecipeBuilder.AbstractShaped<IExtremeRecipe> {

        public Shaped() {
            super(9, 9);
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_extreme_shaped_";
        }

        @Override
        @RecipeBuilderRegistrationMethod(hierarchy = 5)
        public IExtremeRecipe register() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shaped Extended Crafting Table recipe").error()
                    .add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined")
                    .add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");
            if (msg.postIfNotEmpty()) return null;
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            IExtremeRecipe recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, ((width1, height1, ingredients) -> ExtremeShapedRecipe.make(output, ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, ((width1, height1, ingredients) -> ExtremeShapedRecipe.make(output.copy(), ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            }
            if (msg.postIfNotEmpty()) return null;
            if (recipe != null) {
                validateName();
                recipe.setRegistryName(this.name);
                ModSupport.AVARITIA.get().extremeCrafting.add(recipe);
            }
            return recipe;
        }
    }

    @Property(property = "ingredients", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "81", type = Comp.Type.LTE)})
    class Shapeless extends AbstractCraftingRecipeBuilder.AbstractShapeless<IExtremeRecipe> {

        public Shapeless() {
            super(9, 9);
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_extreme_shapeless_";
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shapeless Extended Crafting Table recipe").error();
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            msg.add(ingredients.isEmpty(), () -> "inputs must not be empty");
            msg.add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size());
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod(hierarchy = 5)
        public IExtremeRecipe register() {
            if (!validate()) return null;
            validateName();
            IExtremeRecipe recipe = ExtremeShapelessRecipe.make(output.copy(), ingredients, recipeFunction, recipeAction);
            recipe.setRegistryName(this.name);
            ModSupport.AVARITIA.get().extremeCrafting.add(recipe);
            return recipe;
        }
    }
}
