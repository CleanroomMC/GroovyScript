package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.config.ModConfig;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeBase;
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

public class EnderRecipeBuilder extends CraftingRecipeBuilder {

    @Property(defaultValue = "ModConfig.confEnderTimeRequired", valid = @Comp(value = "0", type = Comp.Type.GTE))
    protected int time = ModConfig.confEnderTimeRequired;

    public EnderRecipeBuilder() {
        super(3, 3);
    }

    @RecipeBuilderMethodDescription
    public EnderRecipeBuilder time(int time) {
        this.time = time;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "time")
    public EnderRecipeBuilder seconds(int seconds) {
        this.time = seconds;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "time")
    public EnderRecipeBuilder ticks(int ticks) {
        this.time = ticks * 20;
        return this;
    }

    @Override
    public IRecipe register() {
        return null;
    }

    public static class Shaped extends EnderRecipeBuilder {

        @Property(value = "groovyscript.wiki.craftingrecipe.keyMap.value", defaultValue = "' ' = IIngredient.EMPTY", priority = 200)
        private final Char2ObjectOpenHashMap<IIngredient> keyMap = new Char2ObjectOpenHashMap<>();
        private final List<String> errors = new ArrayList<>();
        @Property("groovyscript.wiki.craftingrecipe.mirrored.value")
        protected boolean mirrored = false;
        @Property(value = "groovyscript.wiki.craftingrecipe.keyBasedMatrix.value", requirement = "groovyscript.wiki.craftingrecipe.matrix.required", priority = 210)
        private String[] keyBasedMatrix;
        @Property(value = "groovyscript.wiki.craftingrecipe.ingredientMatrix.value", requirement = "groovyscript.wiki.craftingrecipe.matrix.required", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "9", type = Comp.Type.LTE)}, priority = 250)
        private List<List<IIngredient>> ingredientMatrix;

        public Shaped() {
            keyMap.put(' ', IIngredient.EMPTY);
        }

        @RecipeBuilderMethodDescription
        public EnderRecipeBuilder.Shaped mirrored(boolean mirrored) {
            this.mirrored = mirrored;
            return this;
        }

        @RecipeBuilderMethodDescription
        public EnderRecipeBuilder.Shaped mirrored() {
            return mirrored(true);
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public EnderRecipeBuilder.Shaped matrix(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public EnderRecipeBuilder.Shaped shape(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public EnderRecipeBuilder.Shaped row(String row) {
            if (this.keyBasedMatrix == null) {
                this.keyBasedMatrix = new String[]{row};
            } else {
                this.keyBasedMatrix = ArrayUtils.add(this.keyBasedMatrix, row);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public EnderRecipeBuilder.Shaped key(char c, IIngredient ingredient) {
            this.keyMap.put(c, ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public EnderRecipeBuilder.Shaped key(String c, IIngredient ingredient) {
            if (c == null || c.length() != 1) {
                errors.add("key must be a single char, but found '" + c + "'");
                return this;
            }
            this.keyMap.put(c.charAt(0), ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public EnderRecipeBuilder.Shaped key(Map<String, IIngredient> map) {
            for (Map.Entry<String, IIngredient> x : map.entrySet()) {
                key(x.getKey(), x.getValue());
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredientMatrix")
        public EnderRecipeBuilder.Shaped matrix(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredientMatrix")
        public EnderRecipeBuilder.Shaped shape(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
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

    public static class Shapeless extends EnderRecipeBuilder {

        @Property(value = "groovyscript.wiki.craftingrecipe.ingredients.value", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "9", type = Comp.Type.LTE)}, priority = 250)
        private final List<IIngredient> ingredients = new ArrayList<>();

        @RecipeBuilderMethodDescription(field = "ingredients")
        public EnderRecipeBuilder.Shapeless input(IIngredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredients")
        public EnderRecipeBuilder.Shapeless input(IIngredient... ingredients) {
            if (ingredients != null) {
                for (IIngredient ingredient : ingredients) {
                    input(ingredient);
                }
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredients")
        public EnderRecipeBuilder.Shapeless input(Collection<IIngredient> ingredients) {
            if (ingredients != null && !ingredients.isEmpty()) {
                for (IIngredient ingredient : ingredients) {
                    input(ingredient);
                }
            }
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
