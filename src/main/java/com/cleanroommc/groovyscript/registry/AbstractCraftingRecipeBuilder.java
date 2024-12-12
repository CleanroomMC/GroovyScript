package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.compat.vanilla.Crafting;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import groovy.lang.Closure;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractCraftingRecipeBuilder<R> {

    @Property(
            value = "groovyscript.wiki.craftingrecipe.output.value",
            comp = @Comp(not = "null"),
            priority = 700,
            hierarchy = 20)
    protected ItemStack output;
    @Property(value = "groovyscript.wiki.name.value", priority = 100, hierarchy = 20)
    protected ResourceLocation name;
    @Property(
            value = "groovyscript.wiki.craftingrecipe.recipeFunction.value",
            priority = 1500,
            hierarchy = 20)
    protected Closure<ItemStack> recipeFunction;
    @Property(
            value = "groovyscript.wiki.craftingrecipe.recipeAction.value",
            priority = 1550,
            hierarchy = 20)
    protected Closure<Void> recipeAction;
    @Property(value = "groovyscript.wiki.craftingrecipe.replace.value", needsOverride = true, hierarchy = 20)
    protected byte replace;

    protected int width;
    protected int height;

    public AbstractCraftingRecipeBuilder(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private static IIngredient getIngredient(Char2ObjectMap<IIngredient> keyMap, char c) {
        if (keyMap.containsKey(c)) {
            return keyMap.get(c);
        }
        return Crafting.getFallback(c);
    }

    @RecipeBuilderMethodDescription
    public AbstractCraftingRecipeBuilder<R> name(String name) {
        if (name.contains(":")) {
            this.name = new ResourceLocation(name);
        } else {
            this.name = new ResourceLocation(GroovyScript.getRunConfig().getPackId(), name);
        }
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractCraftingRecipeBuilder<R> name(ResourceLocation name) {
        this.name = name;
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractCraftingRecipeBuilder<R> output(ItemStack item) {
        this.output = item;
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractCraftingRecipeBuilder<R> recipeFunction(Closure<ItemStack> recipeFunction) {
        this.recipeFunction = recipeFunction;
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractCraftingRecipeBuilder<R> recipeAction(Closure<Void> recipeAction) {
        this.recipeAction = recipeAction;
        return this;
    }

    @RecipeBuilderMethodDescription
    public AbstractCraftingRecipeBuilder<R> replace() {
        this.replace = 1;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "replace")
    public AbstractCraftingRecipeBuilder<R> replaceByName() {
        this.replace = 2;
        return this;
    }

    @RecipeBuilderRegistrationMethod
    public abstract R register();

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
    public String getRecipeNamePrefix() {
        return "groovyscript_";
    }

    @GroovyBlacklist
    public void validateName() {
        if (name == null) {
            name = RecipeName.generateRl(getRecipeNamePrefix());
        }
    }

    @GroovyBlacklist
    protected @Nullable <T> T validateShape(GroovyLog.Msg msg,
                                            List<String> errors,
                                            String[] keyBasedMatrix,
                                            Char2ObjectOpenHashMap<IIngredient> keyMap,
                                            IRecipeCreator<T> recipeCreator) {
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
                IIngredient ingredient = getIngredient(keyMap, c);
                if (ingredient == null) {
                    if (!checkedChars.contains(c)) {
                        msg.add("Key '" + c + "' is not defined!");
                        checkedChars.add(c);
                    }
                    continue;
                }
                checkedChars.add(c);
                ingredients.add(ingredient);
            }
        }
        int finalRowWidth = rowWidth;
        msg.add(rowWidth > width, () -> "At least one row has a row length of " + finalRowWidth + ", but maximum is " + width);
        checkStackSizes(msg, ingredients);

        if (checkedChars.isEmpty()) {
            msg.add("Matrix must not be empty");
        } else if (checkedChars.size() == 1) {
            char c = checkedChars.toCharArray()[0];
            if (!keyMap.containsKey(c) || IngredientHelper.isEmpty(keyMap.get(c))) {
                msg.add("Matrix only contains empty ingredients!");
            }
        }
        if (msg.postIfNotEmpty()) return null;
        return recipeCreator.createRecipe(rowWidth, keyBasedMatrix.length, ingredients);
    }

    protected void checkStackSizes(GroovyLog.Msg msg, Collection<IIngredient> ingredients) {
        if (GroovyScriptConfig.compat.checkInputStackCounts) {
            for (IIngredient ingredient : ingredients) {
                if (!(ingredient instanceof FluidStack)) {
                    msg.add(IngredientHelper.overMaxSize(ingredient, 1), "Expected stack size 1 for {}, got {}", ingredient.toString(), ingredient.getAmount());
                }
            }
        }
    }

    @GroovyBlacklist
    protected @Nullable <T> T validateShape(GroovyLog.Msg msg, List<List<IIngredient>> ingredientMatrix, IRecipeCreator<T> recipeCreator) {
        List<IIngredient> ingredients = new ArrayList<>();
        if (ingredientMatrix.size() > height) {
            msg.add("defined matrix has %d rows, but should only have %d rows", ingredientMatrix.size(), height);
        }
        boolean logged = false;
        boolean hasNonEmpty = false;
        int rowWidth = ingredientMatrix.get(0).size();
        for (int i = 0, n = Math.min(ingredientMatrix.size(), height); i < n; i++) {
            List<IIngredient> row = ingredientMatrix.get(i);
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
        return recipeCreator.createRecipe(rowWidth, ingredientMatrix.size(), ingredients);
    }

    public interface IRecipeCreator<T> {

        T createRecipe(int width, int height, List<IIngredient> ingredients);
    }

    public abstract static class AbstractShaped<T> extends AbstractCraftingRecipeBuilder<T> {

        @Property(
                value = "groovyscript.wiki.craftingrecipe.keyMap.value",
                defaultValue = "' ' = IIngredient.EMPTY",
                priority = 210,
                hierarchy = 20)
        protected final Char2ObjectOpenHashMap<IIngredient> keyMap = new Char2ObjectOpenHashMap<>();
        protected final List<String> errors = new ArrayList<>();
        @Property(value = "groovyscript.wiki.craftingrecipe.mirrored.value", hierarchy = 20)
        protected boolean mirrored;
        @Property(
                value = "groovyscript.wiki.craftingrecipe.keyBasedMatrix.value",
                comp = @Comp(unique = "groovyscript.wiki.craftingrecipe.matrix.required"),
                priority = 200,
                hierarchy = 20)
        protected String[] keyBasedMatrix;
        @Property(
                value = "groovyscript.wiki.craftingrecipe.ingredientMatrix.value",
                comp = @Comp(gte = 1, lte = 9, unique = "groovyscript.wiki.craftingrecipe.matrix.required"),
                priority = 200,
                hierarchy = 20)
        protected List<List<IIngredient>> ingredientMatrix;

        public AbstractShaped(int width, int height) {
            super(width, height);
            keyMap.put(' ', IIngredient.EMPTY);
        }

        @RecipeBuilderMethodDescription
        public AbstractShaped<T> mirrored(boolean mirrored) {
            this.mirrored = mirrored;
            return this;
        }

        @RecipeBuilderMethodDescription
        public AbstractShaped<T> mirrored() {
            return mirrored(true);
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public AbstractShaped<T> matrix(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public AbstractShaped<T> shape(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public AbstractShaped<T> row(String row) {
            if (this.keyBasedMatrix == null) {
                this.keyBasedMatrix = new String[]{
                        row
                };
            } else {
                this.keyBasedMatrix = ArrayUtils.add(this.keyBasedMatrix, row);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public AbstractShaped<T> key(char c, IIngredient ingredient) {
            this.keyMap.put(c, ingredient);
            return this;
        }

        // groovy doesn't have char literals
        @RecipeBuilderMethodDescription(field = "keyMap")
        public AbstractShaped<T> key(String c, IIngredient ingredient) {
            if (c == null || c.length() != 1) {
                errors.add("key must be a single char, but found '" + c + "'");
                return this;
            }
            this.keyMap.put(c.charAt(0), ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public AbstractShaped<T> key(Map<String, IIngredient> map) {
            for (Map.Entry<String, IIngredient> x : map.entrySet()) {
                key(x.getKey(), x.getValue());
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredientMatrix")
        public AbstractShaped<T> matrix(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredientMatrix")
        public AbstractShaped<T> shape(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }
    }

    public abstract static class AbstractShapeless<T> extends AbstractCraftingRecipeBuilder<T> {

        @Property(
                value = "groovyscript.wiki.craftingrecipe.ingredients.value",
                comp = @Comp(gte = 1, lte = 9),
                priority = 250,
                hierarchy = 20)
        protected final List<IIngredient> ingredients = new ArrayList<>();

        public AbstractShapeless(int width, int height) {
            super(width, height);
        }

        @RecipeBuilderMethodDescription(field = "ingredients")
        public AbstractShapeless<T> input(IIngredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredients")
        public AbstractShapeless<T> input(IIngredient... ingredients) {
            if (ingredients != null) {
                for (IIngredient ingredient : ingredients) {
                    input(ingredient);
                }
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredients")
        public AbstractShapeless<T> input(Collection<IIngredient> ingredients) {
            if (ingredients != null && !ingredients.isEmpty()) {
                for (IIngredient ingredient : ingredients) {
                    input(ingredient);
                }
            }
            return this;
        }
    }
}
