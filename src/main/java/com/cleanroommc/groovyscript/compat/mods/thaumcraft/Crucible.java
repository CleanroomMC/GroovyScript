package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import thaumcraft.api.ThaumcraftApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static thaumcraft.common.config.ConfigRecipes.compileGroups;

public class Crucible extends VirtualizedRegistry<CrucibleRecipe> {

    public Crucible() {
        super("Crucible", "crucible");
    }

    public RecipeBuilder recipeBuilder() { return new RecipeBuilder(); }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> ThaumcraftApi.getCraftingRecipes().values().remove(recipe));
        restoreFromBackup().forEach(recipe -> {
            if (!ThaumcraftApi.getCraftingRecipes().values().contains(recipe))
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

    public CrucibleRecipe add(String researchKey, ItemStack result, Object catalyst, AspectList tags) {
        CrucibleRecipe recipe = new CrucibleRecipe(researchKey, result, catalyst, tags);
        add(recipe);
        return recipe;
    }

    public boolean remove(CrucibleRecipe recipe) {

        Iterator recipeIterator = ThaumcraftApi.getCraftingRecipes().values().iterator();

        Object r;
        do {
            if (!recipeIterator.hasNext()) {
                return false;
            }

            r = recipeIterator.next();
        } while(!(r instanceof CrucibleRecipe) || !((CrucibleRecipe)r).getRecipeOutput().isItemEqual(recipe.getRecipeOutput()));

        recipeIterator.remove();

        addBackup(recipe);
        return true;
    }

    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Thaumcraft Crucible recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
        }
        Object r;
        List<CrucibleRecipe> recipes = new ArrayList<>();
        Iterator recipeIterator = ThaumcraftApi.getCraftingRecipes().values().iterator();
        while (recipeIterator.hasNext()) {
            r = recipeIterator.next();
            if((r instanceof CrucibleRecipe) && ((CrucibleRecipe)r).getRecipeOutput().isItemEqual(output)) {
                recipes.add((CrucibleRecipe)r);
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
        private AspectList aspects = new AspectList();
        private Object catalyst;

        public RecipeBuilder researchKey(String researchKey) {
            this.researchKey = researchKey;
            return this;
        }

        public RecipeBuilder aspect(AspectStack aspectIn) {
            this.aspects.add(aspectIn.getAspect(), aspectIn.getQuantity());
            return this;
        }

        public RecipeBuilder catalyst(Object catalyst) {
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
        }

        @Override
        public @Nullable CrucibleRecipe register() {
            if (!validate()) return null;
            CrucibleRecipe recipe = null;
            for (ItemStack itemStack : output) {
                CrucibleRecipe recipe1 = ModSupport.THAUMCRAFT.get().crucible.add(researchKey, itemStack, catalyst, aspects);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
