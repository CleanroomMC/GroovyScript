package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.registry.anvil.ShapedAnvilRecipe;
import betterwithmods.common.registry.anvil.ShapelessAnvilRecipe;
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
import net.minecraft.item.crafting.IRecipe;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AnvilRecipeBuilder extends CraftingRecipeBuilder {

    public AnvilRecipeBuilder() {
        super(4, 4);
    }

    public static class Shaped extends AnvilRecipeBuilder {

        @Property(value = "groovyscript.wiki.craftingrecipe.keyMap.value", defaultValue = "' ' = IIngredient.EMPTY", priority = 200)
        private final Char2ObjectOpenHashMap<IIngredient> keyMap = new Char2ObjectOpenHashMap<>();
        private final List<String> errors = new ArrayList<>();
        @Property("groovyscript.wiki.craftingrecipe.mirrored.value")
        protected boolean mirrored = false;
        @Property(value = "groovyscript.wiki.craftingrecipe.keyBasedMatrix.value", requirement = "groovyscript.wiki.craftingrecipe.matrix.required", priority = 210)
        private String[] keyBasedMatrix;
        @Property(value = "groovyscript.wiki.craftingrecipe.ingredientMatrix.value", requirement = "groovyscript.wiki.craftingrecipe.matrix.required", valid = {
                @Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "81", type = Comp.Type.LTE)}, priority = 250)
        private List<List<IIngredient>> ingredientMatrix;

        public Shaped() {
            keyMap.put(' ', IIngredient.EMPTY);
        }

        @RecipeBuilderMethodDescription
        public AnvilRecipeBuilder.Shaped mirrored(boolean mirrored) {
            this.mirrored = mirrored;
            return this;
        }

        @RecipeBuilderMethodDescription
        public AnvilRecipeBuilder.Shaped mirrored() {
            return mirrored(true);
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public AnvilRecipeBuilder.Shaped matrix(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public AnvilRecipeBuilder.Shaped shape(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public AnvilRecipeBuilder.Shaped row(String row) {
            if (this.keyBasedMatrix == null) {
                this.keyBasedMatrix = new String[]{row};
            } else {
                this.keyBasedMatrix = ArrayUtils.add(this.keyBasedMatrix, row);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public AnvilRecipeBuilder.Shaped key(char c, IIngredient ingredient) {
            this.keyMap.put(c, ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public AnvilRecipeBuilder.Shaped key(String c, IIngredient ingredient) {
            if (c == null || c.length() != 1) {
                errors.add("key must be a single char, but found '" + c + "'");
                return this;
            }
            this.keyMap.put(c.charAt(0), ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public AnvilRecipeBuilder.Shaped key(Map<String, IIngredient> map) {
            for (Map.Entry<String, IIngredient> x : map.entrySet()) {
                key(x.getKey(), x.getValue());
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredientMatrix")
        public AnvilRecipeBuilder.Shaped matrix(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredientMatrix")
        public AnvilRecipeBuilder.Shaped shape(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
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
                ModSupport.BETTER_WITH_MODS.get().anvilCrafting.add(recipe);
            }
            return recipe;
        }
    }

    public static class Shapeless extends AnvilRecipeBuilder {

        @Property(value = "groovyscript.wiki.craftingrecipe.ingredients.value", valid = {@Comp(value = "1", type = Comp.Type.GTE),
                                                                                         @Comp(value = "81", type = Comp.Type.LTE)}, priority = 250)
        private final List<IIngredient> ingredients = new ArrayList<>();

        @RecipeBuilderMethodDescription(field = "ingredients")
        public AnvilRecipeBuilder.Shapeless input(IIngredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredients")
        public AnvilRecipeBuilder.Shapeless input(IIngredient... ingredients) {
            if (ingredients != null) {
                for (IIngredient ingredient : ingredients) {
                    input(ingredient);
                }
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredients")
        public AnvilRecipeBuilder.Shapeless input(Collection<IIngredient> ingredients) {
            if (ingredients != null && !ingredients.isEmpty()) {
                for (IIngredient ingredient : ingredients) {
                    input(ingredient);
                }
            }
            return this;
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
