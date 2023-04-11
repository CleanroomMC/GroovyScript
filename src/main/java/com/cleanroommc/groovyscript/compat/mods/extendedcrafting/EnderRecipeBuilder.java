package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.config.ModConfig;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeBase;
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

public class EnderRecipeBuilder extends CraftingRecipeBuilder {
    protected int time = ModConfig.confEnderTimeRequired;

    public EnderRecipeBuilder() {
        super(3, 3);
    }

    public EnderRecipeBuilder time(int time) {
        this.time = time;
        return this;
    }

    public EnderRecipeBuilder seconds(int seconds) {
        this.time = seconds;
        return this;
    }

    public EnderRecipeBuilder ticks(int ticks) {
        this.time = ticks * 20;
        return this;
    }

    @Override
    public IRecipe register() {
        return null;
    }

    public static class Shaped extends EnderRecipeBuilder {
        private final Char2ObjectOpenHashMap<IIngredient> keyMap = new Char2ObjectOpenHashMap<>();
        private final List<String> errors = new ArrayList<>();
        protected boolean mirrored = false;
        private String[] keyBasedMatrix;
        private List<List<IIngredient>> ingredientMatrix;

        public Shaped() {
            keyMap.put(' ', IIngredient.EMPTY);
        }

        public EnderRecipeBuilder.Shaped mirrored(boolean mirrored) {
            this.mirrored = mirrored;
            return this;
        }

        public EnderRecipeBuilder.Shaped mirrored() {
            return mirrored(true);
        }

        public EnderRecipeBuilder.Shaped matrix(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        public EnderRecipeBuilder.Shaped shape(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        public EnderRecipeBuilder.Shaped row(String row) {
            if (this.keyBasedMatrix == null) {
                this.keyBasedMatrix = new String[]{row};
            } else {
                this.keyBasedMatrix = ArrayUtils.add(this.keyBasedMatrix, row);
            }
            return this;
        }

        public EnderRecipeBuilder.Shaped key(String c, IIngredient ingredient) {
            if (c == null || c.length() != 1) {
                errors.add("key must be a single char, but found '" + c + "'");
                return this;
            }
            this.keyMap.put(c.charAt(0), ingredient);
            return this;
        }

        public EnderRecipeBuilder.Shaped matrix(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        public EnderRecipeBuilder.Shaped shape(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error creating Ender Crafting recipe").error();

            msg.add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined");
            msg.add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            msg.add(time < 0, "time must be a nonnegative integer, yet it was {}", time);

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
            GroovyLog.Msg msg = GroovyLog.msg("Error creating Ender Crafting recipe").error();

            TableRecipeShaped recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, ((width1, height1, ingredients) -> {
                    NonNullList<Ingredient> input = NonNullList.withSize(height * width, Ingredient.EMPTY);
                    int i = 0;
                    for (Iterator<IIngredient> it = ingredients.stream().iterator(); it.hasNext(); ) {
                        input.set(i++, it.next().toMcIngredient());
                    }
                    return new TableRecipeShaped(1, output, width1, height1, input);
                }));
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, ((width1, height1, ingredients) -> {
                    NonNullList<Ingredient> input = NonNullList.withSize(height * width, Ingredient.EMPTY);
                    int i = 0;
                    for (Iterator<IIngredient> it = ingredients.stream().iterator(); it.hasNext(); ) {
                        input.set(i++, it.next().toMcIngredient());
                    }
                    return new TableRecipeShaped(1, output, width1, height1, input);
                }));
            }
            if (recipe == null) {
                msg.add("The recipe could not be parsed!");
            }

            if (msg.postIfNotEmpty()) return null;

            recipe.setMirrored(this.mirrored);
            recipe.enderCrafterRecipeTimeRequired = this.time;
            ModSupport.EXTENDED_CRAFTING.get().enderCrafting.add(recipe);
            return recipe;
        }
    }

    public static class Shapeless extends EnderRecipeBuilder {
        private final NonNullList<Ingredient> ingredients = NonNullList.create();

        public EnderRecipeBuilder.Shapeless input(IIngredient ingredient) {
            ingredients.add(ingredient.toMcIngredient());
            return this;
        }

        public EnderRecipeBuilder.Shapeless input(IIngredient... ingredients) {
            if (ingredients != null && ingredients.length > 0)
                for (IIngredient ingredient : ingredients)
                    input(ingredient);
            return this;
        }

        public EnderRecipeBuilder.Shapeless input(Collection<IIngredient> ingredients) {
            if (ingredients != null && !ingredients.isEmpty())
                for (IIngredient ingredient : ingredients)
                    input(ingredient);
            return this;
        }

        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding Ender Crafting recipe").error();
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");
            msg.add(ingredients.isEmpty(), () -> "inputs must not be empty");
            msg.add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size());
            msg.add(time < 0, "time must be a nonnegative integer, yet it was {}", time);
            return !msg.postIfNotEmpty();
        }

        @Override
        public IRecipe register() {
            if (!validate()) return null;
            TableRecipeShapeless recipe = new TableRecipeShapeless(1, output.copy(), ingredients);
            ((TableRecipeBase) recipe).enderCrafterRecipeTimeRequired = this.time;
            ModSupport.EXTENDED_CRAFTING.get().enderCrafting.add(recipe);
            return recipe;
        }
    }
}
