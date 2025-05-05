package com.cleanroommc.groovyscript.compat.mods.thaumcraft.arcane;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.Thaumcraft;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.AbstractCraftingRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.Collection;

public interface ArcaneRecipeBuilder {

    @RecipeBuilderMethodDescription
    ArcaneRecipeBuilder researchKey(String researchKey);

    @RecipeBuilderMethodDescription(field = "aspects")
    ArcaneRecipeBuilder aspect(AspectStack aspect);

    @RecipeBuilderMethodDescription(field = "aspects")
    default ArcaneRecipeBuilder aspect(AspectStack... aspect) {
        for (var entry : aspect) aspect(entry);
        return this;
    }

    @RecipeBuilderMethodDescription(field = "aspects")
    default ArcaneRecipeBuilder aspect(Collection<AspectStack> aspect) {
        for (var entry : aspect) aspect(entry);
        return this;
    }

    @RecipeBuilderMethodDescription(field = "aspects")
    default ArcaneRecipeBuilder aspect(String tag) {
        return aspect(tag, 1);
    }

    @RecipeBuilderMethodDescription(field = "aspects")
    default ArcaneRecipeBuilder aspect(String tag, int amount) {
        Aspect a = Thaumcraft.validateAspect(tag);
        if (a != null) aspect(new AspectStack(a, amount));
        return this;
    }

    @RecipeBuilderMethodDescription
    ArcaneRecipeBuilder vis(int vis);

    @Property(property = "replace")
    class Shaped extends AbstractCraftingRecipeBuilder.AbstractShaped<IRecipe> implements ArcaneRecipeBuilder {

        @Property(comp = @Comp(unique = "groovyscript.wiki.thaumcraft.arcane_workbench.aspects.required"))
        protected final AspectList aspects = new AspectList();
        @Property
        protected String researchKey = "";
        @Property
        protected int vis;

        public Shaped() {
            super(3, 3);
        }

        @Override
        @RecipeBuilderMethodDescription
        public ArcaneRecipeBuilder researchKey(String researchKey) {
            this.researchKey = researchKey;
            return this;
        }

        @Override
        @RecipeBuilderMethodDescription(field = "aspects")
        public ArcaneRecipeBuilder aspect(AspectStack aspect) {
            this.aspects.add(aspect.getAspect(), aspect.getAmount());
            return this;
        }

        @Override
        @RecipeBuilderMethodDescription
        public ArcaneRecipeBuilder vis(int vis) {
            this.vis = vis;
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_shaped_arcane_";
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IRecipe register() {
            GroovyLog.Msg msg = GroovyLog.msg("Error creating Thaumcraft Arcane Workbench recipe")
                    .error()
                    .add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined")
                    .add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");
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

    @Property(property = "replace")
    class Shapeless extends AbstractCraftingRecipeBuilder.AbstractShapeless<IRecipe> implements ArcaneRecipeBuilder {


        @Property(comp = @Comp(unique = "groovyscript.wiki.thaumcraft.arcane_workbench.aspects.required"))
        protected final AspectList aspects = new AspectList();
        @Property
        protected String researchKey = "";
        @Property
        protected int vis;

        public Shapeless() {
            super(3, 3);
        }

        @Override
        @RecipeBuilderMethodDescription
        public ArcaneRecipeBuilder researchKey(String researchKey) {
            this.researchKey = researchKey;
            return this;
        }

        @Override
        @RecipeBuilderMethodDescription(field = "aspects")
        public ArcaneRecipeBuilder aspect(AspectStack aspect) {
            this.aspects.add(aspect.getAspect(), aspect.getAmount());
            return this;
        }

        @Override
        @RecipeBuilderMethodDescription
        public ArcaneRecipeBuilder vis(int vis) {
            this.vis = vis;
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
