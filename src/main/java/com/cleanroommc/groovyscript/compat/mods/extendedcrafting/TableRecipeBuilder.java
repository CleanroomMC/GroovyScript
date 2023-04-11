package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.crafting.table.TableRecipeShaped;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeShapeless;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.vanilla.CraftingRecipeBuilder;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TableRecipeBuilder extends CraftingRecipeBuilder {
    // 0 = any table it fits in, 1-4 specifically that tier of table
    protected int tier = 0;

    public TableRecipeBuilder() {
        super(3, 3);
    }

    public TableRecipeBuilder tier(int tier) {
        this.tier = tier;
        return this;
    }

    public TableRecipeBuilder tierAny() {
        this.tier = 0;
        return this;
    }

    public TableRecipeBuilder tierBasic() {
        this.tier = 1;
        return this;
    }

    public TableRecipeBuilder tierAdvanced() {
        this.tier = 2;
        return this;
    }

    public TableRecipeBuilder tierElite() {
        this.tier = 3;
        return this;
    }

    public TableRecipeBuilder tierUltimate() {
        this.tier = 4;
        return this;
    }

    @Override
    public IRecipe register() {
        return null;
    }

    public static class Shaped extends TableRecipeBuilder {
        private final Char2ObjectOpenHashMap<IIngredient> keyMap = new Char2ObjectOpenHashMap<>();
        private final List<String> errors = new ArrayList<>();
        protected boolean mirrored = false;
        private String[] keyBasedMatrix;
        private List<List<IIngredient>> ingredientMatrix;

        public Shaped() {
            keyMap.put(' ', IIngredient.EMPTY);
        }

        public TableRecipeBuilder.Shaped mirrored(boolean mirrored) {
            this.mirrored = mirrored;
            return this;
        }

        public TableRecipeBuilder.Shaped mirrored() {
            return mirrored(true);
        }

        public TableRecipeBuilder.Shaped matrix(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        public TableRecipeBuilder.Shaped shape(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        public TableRecipeBuilder.Shaped row(String row) {
            if (this.keyBasedMatrix == null) {
                this.keyBasedMatrix = new String[]{row};
            } else {
                this.keyBasedMatrix = ArrayUtils.add(this.keyBasedMatrix, row);
            }
            return this;
        }

        public TableRecipeBuilder.Shaped key(String c, IIngredient ingredient) {
            if (c == null || c.length() != 1) {
                errors.add("key must be a single char, but found '" + c + "'");
                return this;
            }
            this.keyMap.put(c.charAt(0), ingredient);
            return this;
        }

        public TableRecipeBuilder.Shaped matrix(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        public TableRecipeBuilder.Shaped shape(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error creating Extended Crafting Table recipe").error();

            msg.add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined");
            msg.add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");

            if (keyBasedMatrix != null) {
                height = keyBasedMatrix.length;
                width = 0;
                for (String row : keyBasedMatrix) {
                    if (width < row.length()) {
                        width = row.length();
                    }
                }
            } else if (ingredientMatrix != null) {
                height = ingredientMatrix.size();
                width = 0;
                for (List<IIngredient> row : ingredientMatrix) {
                    if (width < row.size()) {
                        width = row.size();
                    }
                }
            }

            return !msg.postIfNotEmpty();
        }

        @Override
        public IRecipe register() {
            if (!validate()) return null;
            GroovyLog.Msg msg = GroovyLog.msg("Error creating Extended Crafting Table recipe").error();

            TableRecipeShaped recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, ((width1, height1, ingredients) -> {
                    NonNullList<Ingredient> input = NonNullList.withSize(height * width, Ingredient.EMPTY);
                    int i = 0;
                    for (Iterator<IIngredient> it = ingredients.stream().iterator(); it.hasNext(); ) {
                        input.set(i++, it.next().toMcIngredient());
                    }
                    return new TableRecipeShaped(tier, output, width1, height1, input);
                }));
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, ((width1, height1, ingredients) -> {
                    NonNullList<Ingredient> input = NonNullList.withSize(height * width, Ingredient.EMPTY);
                    int i = 0;
                    for (Iterator<IIngredient> it = ingredients.stream().iterator(); it.hasNext(); ) {
                        input.set(i++, it.next().toMcIngredient());
                    }
                    return new TableRecipeShaped(tier, output, width1, height1, input);
                }));
            }
            if (recipe == null) {
                msg.add("The recipe could not be parsed!");
            }

            if (msg.postIfNotEmpty()) return null;

            recipe.setMirrored(this.mirrored);
            ModSupport.EXTENDED_CRAFTING.get().tableCrafting.add(recipe);
            return recipe;
        }
    }

    public static class Shapeless extends TableRecipeBuilder {
        private final NonNullList<Ingredient> ingredients = NonNullList.create();

        public TableRecipeBuilder.Shapeless input(IIngredient ingredient) {
            ingredients.add(ingredient.toMcIngredient());
            return this;
        }

        public TableRecipeBuilder.Shapeless input(IIngredient... ingredients) {
            if (ingredients != null && ingredients.length > 0)
                for (IIngredient ingredient : ingredients)
                    input(ingredient);
            return this;
        }

        public TableRecipeBuilder.Shapeless input(Collection<IIngredient> ingredients) {
            if (ingredients != null && !ingredients.isEmpty())
                for (IIngredient ingredient : ingredients)
                    input(ingredient);
            return this;
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding Extended Crafting Table recipe").error();

            msg.add(tier < 0 || tier > 4, () -> "tier must be between 0 and 4, was instead " + tier);

            switch (tier) {
                case 1: // Basic
                    height = 3;
                    width = 3;
                    break;
                case 2: // Advanced
                    height = 5;
                    width = 5;
                    break;
                case 3: // Elite
                    height = 7;
                    width = 7;
                    break;
                case 4: // Ultimate
                case 0: // Any tier
                    height = 9;
                    width = 9;
                    break;
            }

            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            msg.add(ingredients.isEmpty(), () -> "inputs must not be empty");
            msg.add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size());

            return !msg.postIfNotEmpty();
        }

        @Override
        public IRecipe register() {
            if (!validate()) return null;
            TableRecipeShapeless recipe = new TableRecipeShapeless(tier, output.copy(), ingredients);
            ModSupport.EXTENDED_CRAFTING.get().tableCrafting.add(recipe);
            return recipe;
        }
    }
}
