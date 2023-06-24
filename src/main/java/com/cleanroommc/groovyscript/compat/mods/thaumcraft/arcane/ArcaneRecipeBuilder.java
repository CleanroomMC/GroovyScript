package com.cleanroommc.groovyscript.compat.mods.thaumcraft.arcane;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.brackets.AspectBracketHandler;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.compat.vanilla.CraftingRecipeBuilder;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ArcaneRecipeBuilder extends CraftingRecipeBuilder {

    protected String researchKey;
    protected final AspectList aspects = new AspectList();
    protected int vis;

    public ArcaneRecipeBuilder() {
        super(3, 3);
    }

    public ArcaneRecipeBuilder researchKey(String researchKey) {
        this.researchKey = researchKey;
        return this;
    }

    public ArcaneRecipeBuilder aspect(AspectStack aspect) {
        this.aspects.add(aspect.getAspect(), aspect.getAmount());
        return this;
    }

    public ArcaneRecipeBuilder aspect(String tag, int amount) {
        Aspect a = AspectBracketHandler.validateAspect(tag);
        if (a != null) this.aspects.add(a, amount);
        return this;
    }

    public ArcaneRecipeBuilder vis(int vis) {
        this.vis = vis;
        return this;
    }

    protected void validateArcane(GroovyLog.Msg msg) {
        if (researchKey == null) researchKey = "";
    }

    public static class Shaped extends ArcaneRecipeBuilder {

        private static final String ID_PREFIX = "shaped_arcane_";

        protected boolean mirrored = false;
        private String[] keyBasedMatrix;
        private final Char2ObjectOpenHashMap<IIngredient> keyMap = new Char2ObjectOpenHashMap<>();

        private List<List<IIngredient>> ingredientMatrix;

        private final List<String> errors = new ArrayList<>();

        public Shaped() {
            keyMap.put(' ', IIngredient.EMPTY);
        }

        public ArcaneRecipeBuilder.Shaped mirrored(boolean mirrored) {
            this.mirrored = mirrored;
            return this;
        }

        public ArcaneRecipeBuilder.Shaped mirrored() {
            return mirrored(true);
        }

        public ArcaneRecipeBuilder.Shaped matrix(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        public ArcaneRecipeBuilder.Shaped shape(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        public ArcaneRecipeBuilder.Shaped row(String row) {
            if (this.keyBasedMatrix == null) {
                this.keyBasedMatrix = new String[]{row};
            } else {
                this.keyBasedMatrix = ArrayUtils.add(this.keyBasedMatrix, row);
            }
            return this;
        }

        // groovy doesn't have char literals
        public ArcaneRecipeBuilder.Shaped key(String c, IIngredient ingredient) {
            if (c == null || c.length() != 1) {
                errors.add("key must be a single char, but found '" + c + "'");
                return this;
            }
            this.keyMap.put(c.charAt(0), ingredient);
            return this;
        }

        public ArcaneRecipeBuilder.Shaped matrix(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        public ArcaneRecipeBuilder.Shaped shape(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        @Override
        public IRecipe register() {
            GroovyLog.Msg msg = GroovyLog.msg("Error creating Thaumcraft Arcane Workbench recipe").error()
                    .add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined")
                    .add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");
            validateArcane(msg);
            if (msg.postIfNotEmpty()) return null;
            msg.add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty");

            ShapedArcaneCR recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, ((width1, height1, ingredients) -> new ShapedArcaneCR(output, ingredients, width1, height1, mirrored, recipeFunction, recipeAction, researchKey, vis, aspects)));
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, ((width1, height1, ingredients) -> new ShapedArcaneCR(output.copy(), ingredients, width1, height1, mirrored, recipeFunction, recipeAction, researchKey, vis, aspects)));
            }

            if (recipe != null) {
                handleReplace();
                ResourceLocation rl = createName(name, ID_PREFIX);
                ReloadableRegistryManager.addRegistryEntry(ForgeRegistries.RECIPES, rl, recipe);
            }

            return recipe;
        }
    }

    public static class Shapeless extends ArcaneRecipeBuilder {

        private static final String ID_PREFIX = "shapeless_arcane_";

        private final List<IIngredient> ingredients = new ArrayList<>();

        public ArcaneRecipeBuilder.Shapeless input(IIngredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        public ArcaneRecipeBuilder.Shapeless input(IIngredient... ingredients) {
            if (ingredients != null)
                for (IIngredient ingredient : ingredients)
                    input(ingredient);
            return this;
        }

        public ArcaneRecipeBuilder.Shapeless input(Collection<IIngredient> ingredients) {
            if (ingredients != null && !ingredients.isEmpty())
                for (IIngredient ingredient : ingredients)
                    input(ingredient);
            return this;
        }

        @Override
        public IRecipe register() {
            IngredientHelper.trim(ingredients);
            GroovyLog.Msg msg = GroovyLog.msg("Error adding Minecraft Shapeless Crafting recipe")
                    .add(IngredientHelper.isEmpty(this.output), () -> "Output must not be empty")
                    .add(ingredients.isEmpty(), () -> "inputs must not be empty")
                    .add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size())
                    .error();
            validateArcane(msg);
            if (msg.postIfNotEmpty()) {
                return null;
            }
            handleReplace();
            ShapelessArcaneCR recipe = new ShapelessArcaneCR(output.copy(), ingredients, recipeFunction, recipeAction, researchKey, vis, aspects);
            ResourceLocation rl = createName(name, ID_PREFIX);
            ReloadableRegistryManager.addRegistryEntry(ForgeRegistries.RECIPES, rl, recipe);
            return recipe;
        }
    }
}
