package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.config.ModConfig;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeBase;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.AbstractCraftingRecipeBuilder;
import net.minecraft.item.crafting.IRecipe;

public interface EnderRecipeBuilder {

    @RecipeBuilderMethodDescription
    EnderRecipeBuilder time(int time);

    @RecipeBuilderMethodDescription(field = "time")
    default EnderRecipeBuilder seconds(int seconds) {
        return this.time(seconds);
    }

    @RecipeBuilderMethodDescription(field = "time")
    default EnderRecipeBuilder ticks(int ticks) {
        return this.time(ticks * 20);
    }

    class Shaped extends AbstractCraftingRecipeBuilder.AbstractShaped<IRecipe> implements EnderRecipeBuilder {

        @Property(defaultValue = "ModConfig.confEnderTimeRequired", valid = @Comp(value = "0", type = Comp.Type.GTE))
        protected int time = ModConfig.confEnderTimeRequired;

        public Shaped() {
            super(3, 3);
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_ender_shaped_";
        }

        @Override
        @RecipeBuilderMethodDescription
        public Shaped time(int time) {
            this.time = time;
            return this;
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shaped Ender Crafting recipe").error();
            msg.add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined");
            msg.add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            msg.add(time < 0, "time must be a nonnegative integer, yet it was {}", time);
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IRecipe register() {
            if (!validate()) return null;
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shaped Ender Crafting recipe").error();

            ShapedTableRecipe recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, ((width1, height1, ingredients) -> ShapedTableRecipe.make(1, output, ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, ((width1, height1, ingredients) -> ShapedTableRecipe.make(1, output.copy(), ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            }
            if (recipe == null) {
                msg.add("The recipe could not be parsed!");
            } else {
                recipe.enderCrafterRecipeTimeRequired = this.time;
            }
            if (msg.postIfNotEmpty()) return null;

            ModSupport.EXTENDED_CRAFTING.get().enderCrafting.add(recipe);
            return recipe;
        }
    }

    class Shapeless extends AbstractCraftingRecipeBuilder.AbstractShapeless<IRecipe> implements EnderRecipeBuilder {

        @Property(defaultValue = "ModConfig.confEnderTimeRequired", valid = @Comp(value = "0", type = Comp.Type.GTE))
        protected int time = ModConfig.confEnderTimeRequired;

        public Shapeless() {
            super(3, 3);
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_ender_shapeless_";
        }

        @Override
        @RecipeBuilderMethodDescription
        public Shapeless time(int time) {
            this.time = time;
            return this;
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shapeless Ender Crafting recipe").error();
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            msg.add(ingredients.isEmpty(), () -> "inputs must not be empty");
            msg.add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size());
            msg.add(time < 0, "time must be a nonnegative integer, yet it was {}", time);
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IRecipe register() {
            if (!validate()) return null;
            ShapelessTableRecipe recipe = ShapelessTableRecipe.make(1, output.copy(), ingredients, recipeFunction, recipeAction);
            ((TableRecipeBase) recipe).enderCrafterRecipeTimeRequired = this.time;
            ModSupport.EXTENDED_CRAFTING.get().enderCrafting.add(recipe);
            return recipe;
        }
    }
}
