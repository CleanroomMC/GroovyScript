package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.AbstractCraftingRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public abstract class CraftingRecipeBuilder {

    @Property(property = "replace")
    public static class Shaped extends AbstractCraftingRecipeBuilder.AbstractShaped<IRecipe> {

        public Shaped() {
            super(3, 3);
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_shaped_";
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IRecipe register() {
            validateName();
            GroovyLog.Msg msg = GroovyLog.msg("Error adding Minecraft Shaped Crafting recipe '{}'", this.name).error()
                    .add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined")
                    .add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!")
                    .add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            if (msg.postIfNotEmpty()) return null;

            ShapedCraftingRecipe recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, ((width1, height1, ingredients) -> new ShapedCraftingRecipe(output, ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, ((width1, height1, ingredients) -> new ShapedCraftingRecipe(output.copy(), ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            }

            if (recipe != null) {
                handleReplace();
                msg.add(ReloadableRegistryManager.hasNonDummyRecipe(this.name), () -> "a recipe with that name already exists! Either replace or remove the recipe first");
                if (msg.postIfNotEmpty()) return null;
                ReloadableRegistryManager.addRegistryEntry(ForgeRegistries.RECIPES, name, recipe);
            }

            return recipe;
        }
    }

    @Property(property = "replace")
    public static class Shapeless extends AbstractCraftingRecipeBuilder.AbstractShapeless<IRecipe> {

        public Shapeless() {
            super(3, 3);
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_shapeless_";
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IRecipe register() {
            validateName();
            IngredientHelper.trim(ingredients);
            GroovyLog.Msg msg = GroovyLog.msg("Error adding Minecraft Shapeless Crafting recipe '{}'", this.name);
            if (msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty")
                    .add(ingredients.isEmpty(), () -> "inputs must not be empty")
                    .add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size())
                    .error()
                    .postIfNotEmpty()) {
                return null;
            }
            handleReplace();
            msg.add(ReloadableRegistryManager.hasNonDummyRecipe(this.name), () -> "a recipe with that name already exists! Either replace or remove the recipe first");
            if (msg.postIfNotEmpty()) return null;
            ShapelessCraftingRecipe recipe = new ShapelessCraftingRecipe(output.copy(), ingredients, recipeFunction, recipeAction);
            ReloadableRegistryManager.addRegistryEntry(ForgeRegistries.RECIPES, name, recipe);
            return recipe;
        }
    }
}
