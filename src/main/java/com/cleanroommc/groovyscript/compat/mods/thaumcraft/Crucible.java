package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.brackets.AspectBracketHandler;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static thaumcraft.common.config.ConfigRecipes.compileGroups;

public class Crucible extends VirtualizedRegistry<CrucibleRecipe> {

    public Crucible() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> ThaumcraftApi.getCraftingRecipes().values().remove(recipe));
        restoreFromBackup().forEach(recipe -> {
            if (!ThaumcraftApi.getCraftingRecipes().containsValue(recipe))
                ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(recipe.getRecipeOutput().toString()), recipe);
        });
        compileGroups();
    }

    public void add(CrucibleRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(recipe.getRecipeOutput().getDisplayName()), recipe);
            compileGroups();
        }
    }

    public CrucibleRecipe add(String researchKey, ItemStack result, IIngredient catalyst, AspectList tags) {
        CrucibleRecipe recipe = new CrucibleRecipe(researchKey, result, catalyst.toMcIngredient(), tags);
        add(recipe);
        return recipe;
    }

    public boolean remove(CrucibleRecipe recipe) {

        Iterator<IThaumcraftRecipe> recipeIterator = ThaumcraftApi.getCraftingRecipes().values().iterator();

        Object r;
        do {
            if (!recipeIterator.hasNext()) {
                return false;
            }

            r = recipeIterator.next();
        } while (!(r instanceof CrucibleRecipe) || !((CrucibleRecipe) r).getRecipeOutput().isItemEqual(recipe.getRecipeOutput()));

        recipeIterator.remove();

        addBackup(recipe);
        return true;
    }

    public void removeByOutput(IIngredient output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Thaumcraft Crucible recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
        }
        Object r;
        List<CrucibleRecipe> recipes = new ArrayList<>();
        for (IThaumcraftRecipe iThaumcraftRecipe : ThaumcraftApi.getCraftingRecipes().values()) {
            r = iThaumcraftRecipe;
            if ((r instanceof CrucibleRecipe) && output.test(((CrucibleRecipe) r).getRecipeOutput())) {
                recipes.add((CrucibleRecipe) r);
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Thaumcraft Crucible recipe")
                    .add("no recipes found for %s", output)
                    .error()
                    .post();
            return;
        }
        recipes.forEach(recipe -> {
            this.addBackup(recipe);
            ThaumcraftApi.getCraftingRecipes().values().remove(recipe);
        });
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CrucibleRecipe> {

        private String researchKey;
        private final AspectList aspects = new AspectList();
        private IIngredient catalyst;

        public RecipeBuilder researchKey(String researchKey) {
            this.researchKey = researchKey;
            return this;
        }

        public RecipeBuilder aspect(AspectStack aspectIn) {
            this.aspects.add(aspectIn.getAspect(), aspectIn.getAmount());
            return this;
        }

        public RecipeBuilder aspect(String tag, int amount) {
            Aspect a = AspectBracketHandler.validateAspect(tag);
            if (a != null) this.aspects.add(a, amount);
            return this;
        }

        public RecipeBuilder catalyst(IIngredient catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thaumcraft Crucible recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            msg.add(IngredientHelper.isEmpty(catalyst), () -> "Catalyst must not be empty");
            msg.add(aspects.size() == 0, () -> "Aspects must not be empty");
            if (researchKey == null) researchKey = "";
        }

        @Override
        public @Nullable CrucibleRecipe register() {
            if (!validate()) return null;
            return ModSupport.THAUMCRAFT.get().crucible.add(researchKey, this.output.get(0), catalyst, aspects);
        }
    }
}
