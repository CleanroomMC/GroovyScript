package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import groovy.lang.Closure;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class CraftingRecipeBuilder {

    protected ItemStack output;
    protected String name;
    protected Closure<ItemStack> recipeFunction;
    protected byte replace = 0;

    protected final int width, height;

    public CraftingRecipeBuilder(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public CraftingRecipeBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CraftingRecipeBuilder output(ItemStack item) {
        this.output = item;
        return this;
    }

    public CraftingRecipeBuilder recipeFunction(Closure<ItemStack> recipeFunction) {
        this.recipeFunction = recipeFunction;
        return this;
    }

    public CraftingRecipeBuilder replace() {
        this.replace = 1;
        return this;
    }

    public CraftingRecipeBuilder replaceByName() {
        this.replace = 2;
        return this;
    }

    public abstract CraftingRecipe register();

    @GroovyBlacklist
    protected void handleReplace() {
        if (replace == 1) {
            VanillaModule.crafting.removeByOutput(IngredientHelper.toIIngredient(output), false);
        } else if (replace == 2) {
            if (name == null) {
                GroovyLog.msg("Error replacing Minecraft Crafting recipe")
                        .add("Name must not be null when replacing by name")
                        .error()
                        .post();
                return;
            }
            ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, name);
        }
    }

    @GroovyBlacklist
    protected ResourceLocation createName(@Nullable String name, @Nullable String prefix) {
        if (name == null) {
            return new ResourceLocation(GroovyScript.ID, prefix == null ? RecipeName.generate() : RecipeName.generate(prefix));
        }
        if (name.contains(":")) {
            return new ResourceLocation(name);
        }
        return new ResourceLocation(GroovyScript.ID, name);
    }

    public static class Shaped extends CraftingRecipeBuilder {

        private static final String ID_PREFIX = "shaped_";

        protected boolean mirrored = false;
        private String[] keyBasedMatrix;
        private final Char2ObjectOpenHashMap<IIngredient> keyMap = new Char2ObjectOpenHashMap<>();

        private List<List<IIngredient>> ingredientMatrix;

        private final List<String> errors = new ArrayList<>();

        public Shaped(int width, int height) {
            super(width, height);
            keyMap.put(' ', IIngredient.EMPTY);
        }

        public Shaped mirrored(boolean mirrored) {
            this.mirrored = mirrored;
            return this;
        }

        public Shaped mirrored() {
            return mirrored(true);
        }

        public Shaped matrix(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        public Shaped shape(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        // groovy doesn't have char literals
        public Shaped key(String c, IIngredient ingredient) {
            if (c == null || c.length() != 1) {
                errors.add("key must be a single char, but found '" + c + "'");
                return this;
            }
            this.keyMap.put(c.charAt(0), ingredient);
            return this;
        }

        public Shaped matrix(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        public Shaped shape(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        @Override
        public CraftingRecipe register() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding Minecraft Shapeless Crafting recipe").error()
                    .add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined")
                    .add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");
            if (msg.postIfNotEmpty()) return null;
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");

            ShapedCraftingRecipe recipe = null;
            if (keyBasedMatrix != null) {
                List<IIngredient> ingredients = new ArrayList<>();
                if (keyBasedMatrix.length > height) {
                    msg.add("Defined matrix has %d rows, but should only have %d rows", keyBasedMatrix.length, height);
                }
                for (String error : errors) {
                    msg.add(error);
                }
                boolean logged = false;
                int rowWidth = keyBasedMatrix[0].length();
                CharOpenHashSet checkedChars = new CharOpenHashSet();
                for (int i = 0, n = Math.min(keyBasedMatrix.length, height); i < n; i++) {
                    String row = keyBasedMatrix[i];
                    if (!logged && row.length() != rowWidth) {
                        logged = true;
                        msg.add("All rows must have the same length!");
                    }
                    rowWidth = Math.max(rowWidth, row.length());
                    for (int j = 0; j < row.length(); j++) {
                        char c = row.charAt(j);
                        if (!keyMap.containsKey(c)) {
                            if (!checkedChars.contains(c)) {
                                msg.add("Key '" + c + "' is not defined!");
                                checkedChars.add(c);
                            }
                            continue;
                        }
                        checkedChars.add(c);
                        ingredients.add(keyMap.get(c));
                    }
                }
                int finalRowWidth = rowWidth;
                msg.add(rowWidth > width, () -> "At least one row has a row length of " + finalRowWidth + ", but maximum is " + width);
                if (checkedChars.isEmpty()) {
                    msg.add("Matrix must not be empty");
                } else if (checkedChars.size() == 1) {
                    char c = checkedChars.toCharArray()[0];
                    if (!keyMap.containsKey(c) || IngredientHelper.isEmpty(keyMap.get(c))) {
                        msg.add("Matrix only contains empty ingredients!");
                    }
                }
                if (msg.postIfNotEmpty()) return null;
                recipe = new ShapedCraftingRecipe(output, ingredients, rowWidth, keyBasedMatrix.length, mirrored, recipeFunction);
            } else if (ingredientMatrix != null) {
                List<IIngredient> ingredients = new ArrayList<>();
                if (ingredientMatrix.size() > height) {
                    msg.add("defined matrix has %d rows, but should only have %d rows", ingredientMatrix.size(), height);
                }
                boolean logged = false;
                boolean hasNonEmpty = false;
                int rowWidth = ingredientMatrix.get(0).size();
                for (int i = 0, n = Math.min(ingredientMatrix.size(), height); i < n; i++) {
                    List<IIngredient> row = ingredientMatrix.get(0);
                    if (!logged && row.size() != rowWidth) {
                        logged = true;
                        msg.add("All rows must have the same length!");
                    }
                    rowWidth = Math.max(rowWidth, row.size());
                    for (IIngredient ingredient : row) {
                        hasNonEmpty |= !IngredientHelper.isEmpty(ingredient);
                        ingredients.add(ingredient);
                    }
                }
                msg.add(!hasNonEmpty, () -> "Matrix must not be empty");
                if (msg.postIfNotEmpty()) return null;
                recipe = new ShapedCraftingRecipe(output.copy(), ingredients, rowWidth, ingredientMatrix.size(), mirrored, recipeFunction);
            }

            if (recipe != null) {
                handleReplace();
                ResourceLocation rl = createName(name, ID_PREFIX);
                ReloadableRegistryManager.addRegistryEntry(ForgeRegistries.RECIPES, rl, recipe);
            }

            return recipe;
        }
    }

    public static class Shapeless extends CraftingRecipeBuilder {

        private static final String ID_PREFIX = "shapeless_";

        private final List<IIngredient> ingredients = new ArrayList<>();

        public Shapeless(int width, int height) {
            super(width, height);
        }

        public Shapeless input(IIngredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        public Shapeless input(IIngredient... ingredients) {
            if (ingredients != null && ingredients.length > 0)
                for (IIngredient ingredient : ingredients)
                    input(ingredient);
            return this;
        }

        public Shapeless input(Collection<IIngredient> ingredients) {
            if (ingredients != null && !ingredients.isEmpty())
                for (IIngredient ingredient : ingredients)
                    input(ingredient);
            return this;
        }

        @Override
        public CraftingRecipe register() {
            IngredientHelper.trim(ingredients);
            if (GroovyLog.msg("Error adding Minecraft Shapeless Crafting recipe")
                    .add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty")
                    .add(ingredients.isEmpty(), () -> "inputs must not be empty")
                    .add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size())
                    .error()
                    .postIfNotEmpty()) {
                return null;
            }
            handleReplace();
            ShapelessCraftingRecipe recipe = new ShapelessCraftingRecipe(output.copy(), ingredients, recipeFunction);
            ResourceLocation rl = createName(name, ID_PREFIX);
            ReloadableRegistryManager.addRegistryEntry(ForgeRegistries.RECIPES, rl, recipe);
            return recipe;
        }
    }
}
