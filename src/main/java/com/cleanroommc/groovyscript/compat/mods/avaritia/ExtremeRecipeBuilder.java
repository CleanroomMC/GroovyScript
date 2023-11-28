package com.cleanroommc.groovyscript.compat.mods.avaritia;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.vanilla.CraftingRecipeBuilder;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import morph.avaritia.recipe.extreme.IExtremeRecipe;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ExtremeRecipeBuilder extends CraftingRecipeBuilder {

    public ExtremeRecipeBuilder() {
        super(9, 9);
    }

    public static class Shaped extends ExtremeRecipeBuilder {

        @Property(value = "groovyscript.wiki.craftingrecipe.keyMap.value", defaultValue = "' ' = IIngredient.EMPTY", priority = 210, hierarchy = 20)
        private final Char2ObjectOpenHashMap<IIngredient> keyMap = new Char2ObjectOpenHashMap<>();
        private final List<String> errors = new ArrayList<>();
        @Property(value = "groovyscript.wiki.craftingrecipe.mirrored.value", hierarchy = 20)
        protected boolean mirrored = false;
        @Property(value = "groovyscript.wiki.craftingrecipe.keyBasedMatrix.value", requirement = "groovyscript.wiki.craftingrecipe.matrix.required", priority = 200, hierarchy = 20)
        private String[] keyBasedMatrix;
        @Property(value = "groovyscript.wiki.craftingrecipe.ingredientMatrix.value", requirement = "groovyscript.wiki.craftingrecipe.matrix.required", valid = {
                @Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "81", type = Comp.Type.LTE)}, priority = 200, hierarchy = 20)
        private List<List<IIngredient>> ingredientMatrix;

        public Shaped() {
            keyMap.put(' ', IIngredient.EMPTY);
        }

        @RecipeBuilderMethodDescription
        public ExtremeRecipeBuilder.Shaped mirrored(boolean mirrored) {
            this.mirrored = mirrored;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ExtremeRecipeBuilder.Shaped mirrored() {
            return mirrored(true);
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public ExtremeRecipeBuilder.Shaped matrix(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public ExtremeRecipeBuilder.Shaped shape(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public ExtremeRecipeBuilder.Shaped row(String row) {
            if (this.keyBasedMatrix == null) {
                this.keyBasedMatrix = new String[]{row};
            } else {
                this.keyBasedMatrix = ArrayUtils.add(this.keyBasedMatrix, row);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public ExtremeRecipeBuilder.Shaped key(String c, IIngredient ingredient) {
            if (c == null || c.length() != 1) {
                errors.add("key must be a single char, but found '" + c + "'");
                return this;
            }
            this.keyMap.put(c.charAt(0), ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredientMatrix")
        public ExtremeRecipeBuilder.Shaped matrix(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredientMatrix")
        public ExtremeRecipeBuilder.Shaped shape(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
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

        @Override
        public String getRecipeNamePrefix() {
            return "grs_extreme_shaped_";
        }
    }

    public static class Shapeless extends ExtremeRecipeBuilder {

        @Property(value = "groovyscript.wiki.craftingrecipe.ingredients.value", valid = {@Comp(value = "1", type = Comp.Type.GTE),
                                                                                         @Comp(value = "81", type = Comp.Type.LTE)}, priority = 250, hierarchy = 20)
        private final List<IIngredient> ingredients = new ArrayList<>();

        @RecipeBuilderMethodDescription(field = "ingredients")
        public ExtremeRecipeBuilder.Shapeless input(IIngredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredients")
        public ExtremeRecipeBuilder.Shapeless input(IIngredient... ingredients) {
            if (ingredients != null) {
                for (IIngredient ingredient : ingredients) {
                    input(ingredient);
                }
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredients")
        public ExtremeRecipeBuilder.Shapeless input(Collection<IIngredient> ingredients) {
            if (ingredients != null && !ingredients.isEmpty()) {
                for (IIngredient ingredient : ingredients) {
                    input(ingredient);
                }
            }
            return this;
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

        @Override
        public String getRecipeNamePrefix() {
            return "grs_extreme_shapeless_";
        }
    }
}
