package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.crafting.table.ITieredRecipe;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeShaped;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.AbstractCraftingRecipeBuilder;

public interface TableRecipeBuilder {

    @RecipeBuilderMethodDescription
    TableRecipeBuilder tier(int tier);

    @RecipeBuilderMethodDescription(field = "tier")
    default TableRecipeBuilder tierAny() {
        return tier(0);
    }

    @RecipeBuilderMethodDescription(field = "tier")
    default TableRecipeBuilder tierBasic() {
        return tier(1);
    }

    @RecipeBuilderMethodDescription(field = "tier")
    default TableRecipeBuilder tierAdvanced() {
        return tier(2);
    }

    @RecipeBuilderMethodDescription(field = "tier")
    default TableRecipeBuilder tierElite() {
        return tier(3);
    }

    @RecipeBuilderMethodDescription(field = "tier")
    default TableRecipeBuilder tierUltimate() {
        return tier(4);
    }

    @Property(property = "ingredientMatrix", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "81", type = Comp.Type.LTE)})
    class Shaped extends AbstractCraftingRecipeBuilder.AbstractShaped<ITieredRecipe> implements TableRecipeBuilder {

        // 0 = any table it fits in, 1-4 specifically that tier of table
        @Property(valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "4", type = Comp.Type.LTE)})
        int tier;

        public Shaped() {
            super(9, 9);
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_table_shaped_";
        }

        @Override
        @RecipeBuilderMethodDescription
        public TableRecipeBuilder.Shaped tier(int tier) {
            this.tier = tier;
            int size = this.tier == 0 ? 9 : this.tier * 2 + 1;
            this.width = size;
            this.height = size;
            return this;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public ITieredRecipe register() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shaped Extended Crafting Table recipe").error()
                    .add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined")
                    .add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");
            if (msg.postIfNotEmpty()) return null;
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            TableRecipeShaped recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, ((width1, height1, ingredients) -> ShapedTableRecipe.make(tier, output, ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, ((width1, height1, ingredients) -> ShapedTableRecipe.make(tier, output.copy(), ingredients, width1, height1, mirrored, recipeFunction, recipeAction)));
            }
            if (msg.postIfNotEmpty()) return null;
            if (recipe != null) {
                ModSupport.EXTENDED_CRAFTING.get().tableCrafting.add(recipe);
            }
            return recipe;
        }
    }

    @Property(property = "ingredients", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "81", type = Comp.Type.LTE)})
    class Shapeless extends AbstractCraftingRecipeBuilder.AbstractShapeless<ITieredRecipe> implements TableRecipeBuilder {

        // 0 = any table it fits in, 1-4 specifically that tier of table
        @Property(valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "4", type = Comp.Type.LTE)})
        int tier;

        public Shapeless() {
            super(9, 9);
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_table_shapeless_";
        }

        @Override
        @RecipeBuilderMethodDescription
        public TableRecipeBuilder.Shapeless tier(int tier) {
            this.tier = tier;
            int size = this.tier == 0 ? 9 : this.tier * 2 + 1;
            this.width = size;
            this.height = size;
            return this;
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shapeless Extended Crafting Table recipe").error();
            msg.add(tier < 0 || tier > 4, () -> "tier must be between 0 and 4, was instead " + tier);
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            msg.add(ingredients.isEmpty(), () -> "inputs must not be empty");
            msg.add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size());
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public ITieredRecipe register() {
            if (!validate()) return null;
            ShapelessTableRecipe recipe = ShapelessTableRecipe.make(tier, output.copy(), ingredients, recipeFunction, recipeAction);
            ModSupport.EXTENDED_CRAFTING.get().tableCrafting.add(recipe);
            return recipe;
        }
    }
}
