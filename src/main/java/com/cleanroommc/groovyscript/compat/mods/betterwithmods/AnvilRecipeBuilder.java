package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.registry.anvil.ShapedAnvilRecipe;
import betterwithmods.common.registry.anvil.ShapelessAnvilRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.AbstractCraftingRecipeBuilder;
import net.minecraft.item.crafting.IRecipe;

public interface AnvilRecipeBuilder {

    @Property(property = "ingredientMatrix", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "16", type = Comp.Type.LTE)})
    class Shaped extends AbstractCraftingRecipeBuilder.AbstractShaped<IRecipe> {

        public Shaped() {
            super(4, 4);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IRecipe register() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shaped Better With Mods Anvil recipe").error()
                    .add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined")
                    .add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");
            if (msg.postIfNotEmpty()) return null;
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            ShapedAnvilRecipe recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, ((width1, height1, ingredients) -> AnvilShapedRecipe.make(name, output, ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, ((width1, height1, ingredients) -> AnvilShapedRecipe.make(name, output.copy(), ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            }
            if (msg.postIfNotEmpty()) return null;
            if (recipe != null) {
                handleReplace();
                ModSupport.BETTER_WITH_MODS.get().anvilCrafting.add(recipe);
            }
            return recipe;
        }
    }

    @Property(property = "ingredients", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "16", type = Comp.Type.LTE)})
    class Shapeless extends AbstractCraftingRecipeBuilder.AbstractShapeless<IRecipe> {

        public Shapeless() {
            super(4, 4);
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shapeless Better With Mods Anvil recipe").error();
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            msg.add(ingredients.isEmpty(), () -> "inputs must not be empty");
            msg.add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size());
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IRecipe register() {
            if (!validate()) return null;
            ShapelessAnvilRecipe recipe = AnvilShapelessRecipe.make(name, output.copy(), ingredients, recipeFunction, recipeAction);
            ModSupport.BETTER_WITH_MODS.get().anvilCrafting.add(recipe);
            return recipe;
        }
    }
}
