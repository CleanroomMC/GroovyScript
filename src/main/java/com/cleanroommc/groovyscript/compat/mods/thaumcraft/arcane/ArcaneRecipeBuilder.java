package com.cleanroommc.groovyscript.compat.mods.thaumcraft.arcane;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.Thaumcraft;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.compat.vanilla.CraftingRecipeBuilder;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class ArcaneRecipeBuilder extends CraftingRecipeBuilder {

    @Property
    protected String researchKey;
    @Property(requirement = "groovyscript.wiki.thaumcraft.arcaneworkbench.aspects.required")
    protected final AspectList aspects = new AspectList();
    @Property
    protected int vis;

    public ArcaneRecipeBuilder() {
        super(3, 3);
    }

    @RecipeBuilderMethodDescription
    public ArcaneRecipeBuilder researchKey(String researchKey) {
        this.researchKey = researchKey;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "aspects")
    public ArcaneRecipeBuilder aspect(AspectStack aspect) {
        this.aspects.add(aspect.getAspect(), aspect.getAmount());
        return this;
    }

    @RecipeBuilderMethodDescription(field = "aspects")
    public ArcaneRecipeBuilder aspect(String tag) {
        return aspect(tag, 1);
    }

    @RecipeBuilderMethodDescription(field = "aspects")
    public ArcaneRecipeBuilder aspect(String tag, int amount) {
        Aspect a = Thaumcraft.validateAspect(tag);
        if (a != null) this.aspects.add(a, amount);
        return this;
    }

    @RecipeBuilderMethodDescription
    public ArcaneRecipeBuilder vis(int vis) {
        this.vis = vis;
        return this;
    }

    protected void validateArcane(GroovyLog.Msg msg) {
        if (researchKey == null) researchKey = "";
    }

    public static class Shaped extends ArcaneRecipeBuilder {

        @Property("groovyscript.wiki.craftingrecipe.mirrored.value")
        protected boolean mirrored = false;
        @Property(value = "groovyscript.wiki.craftingrecipe.keyBasedMatrix.value", requirement = "groovyscript.wiki.craftingrecipe.matrix.required", priority = 200)
        private String[] keyBasedMatrix;
        @Property(value = "groovyscript.wiki.craftingrecipe.keyMap.value", defaultValue = "' ' = IIngredient.EMPTY", priority = 210)
        private final Char2ObjectOpenHashMap<IIngredient> keyMap = new Char2ObjectOpenHashMap<>();

        @Property(value = "groovyscript.wiki.craftingrecipe.ingredientMatrix.value", requirement = "groovyscript.wiki.craftingrecipe.matrix.required", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "9", type = Comp.Type.LTE)}, priority = 200)
        private List<List<IIngredient>> ingredientMatrix;

        private final List<String> errors = new ArrayList<>();

        public Shaped() {
            keyMap.put(' ', IIngredient.EMPTY);
        }

        @RecipeBuilderMethodDescription
        public ArcaneRecipeBuilder.Shaped mirrored(boolean mirrored) {
            this.mirrored = mirrored;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArcaneRecipeBuilder.Shaped mirrored() {
            return mirrored(true);
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public ArcaneRecipeBuilder.Shaped matrix(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public ArcaneRecipeBuilder.Shaped shape(String... matrix) {
            this.keyBasedMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
        public ArcaneRecipeBuilder.Shaped row(String row) {
            if (this.keyBasedMatrix == null) {
                this.keyBasedMatrix = new String[]{row};
            } else {
                this.keyBasedMatrix = ArrayUtils.add(this.keyBasedMatrix, row);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public ArcaneRecipeBuilder.Shaped key(char c, IIngredient ingredient) {
            this.keyMap.put(c, ingredient);
            return this;
        }

        // groovy doesn't have char literals
        @RecipeBuilderMethodDescription(field = "keyMap")
        public ArcaneRecipeBuilder.Shaped key(String c, IIngredient ingredient) {
            if (c == null || c.length() != 1) {
                errors.add("key must be a single char, but found '" + c + "'");
                return this;
            }
            this.keyMap.put(c.charAt(0), ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "keyMap")
        public ArcaneRecipeBuilder.Shaped key(Map<String, IIngredient> map) {
            for (Map.Entry<String, IIngredient> x : map.entrySet()) {
                key(x.getKey(), x.getValue());
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredientMatrix")
        public ArcaneRecipeBuilder.Shaped matrix(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredientMatrix")
        public ArcaneRecipeBuilder.Shaped shape(List<List<IIngredient>> matrix) {
            this.ingredientMatrix = matrix;
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_shaped_arcane_";
        }

        @Override
        @RecipeBuilderRegistrationMethod
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
                validateName();
                ReloadableRegistryManager.addRegistryEntry(ForgeRegistries.RECIPES, name, recipe);
            }

            return recipe;
        }
    }

    public static class Shapeless extends ArcaneRecipeBuilder {

        @Property(value = "groovyscript.wiki.craftingrecipe.ingredients.value", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "9", type = Comp.Type.LTE)}, priority = 250, hierarchy = 20)
        private final List<IIngredient> ingredients = new ArrayList<>();

        @RecipeBuilderMethodDescription(field = "ingredients")
        public ArcaneRecipeBuilder.Shapeless input(IIngredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredients")
        public ArcaneRecipeBuilder.Shapeless input(IIngredient... ingredients) {
            if (ingredients != null)
                for (IIngredient ingredient : ingredients)
                    input(ingredient);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ingredients")
        public ArcaneRecipeBuilder.Shapeless input(Collection<IIngredient> ingredients) {
            if (ingredients != null && !ingredients.isEmpty())
                for (IIngredient ingredient : ingredients)
                    input(ingredient);
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_shapeless_arcane_";
        }

        @Override
        @RecipeBuilderRegistrationMethod
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
            validateName();
            ReloadableRegistryManager.addRegistryEntry(ForgeRegistries.RECIPES, name, recipe);
            return recipe;
        }
    }
}
