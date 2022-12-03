package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static thaumcraft.common.config.ConfigRecipes.compileGroups;

public class InfusionCrafting extends VirtualizedRegistry<InfusionRecipe> {

    public InfusionCrafting() {
        super("InfusionCrafting", "infusion_crafting");
    }

    public RecipeBuilder recipeBuilder() { return new RecipeBuilder(); }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> ThaumcraftApi.getCraftingRecipes().values().remove(recipe));
        restoreFromBackup().forEach(recipe -> {
            if (!ThaumcraftApi.getCraftingRecipes().values().contains(recipe))
                ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(recipe.getRecipeOutput().toString()), recipe);
        });
        compileGroups();
    }

    public void add(InfusionRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(recipe.getRecipeOutput().toString()), recipe);
        }
    }

    public InfusionRecipe add(String research, ItemStack outputResult, int inst, AspectList aspects2, ItemStack centralItem, Object... recipe) {
        InfusionRecipe infusionRecipe = new InfusionRecipe(research, outputResult, inst, aspects2, centralItem, recipe);
        add(infusionRecipe);
        return infusionRecipe;
    }

    public boolean remove(InfusionRecipe recipe) {

        Iterator recipeIterator = ThaumcraftApi.getCraftingRecipes().values().iterator();

        Object r;
        do {
            if (!recipeIterator.hasNext()) {
                return false;
            }

            r = recipeIterator.next();
        } while(!(r instanceof InfusionRecipe) || !((InfusionRecipe)r).getRecipeOutput().equals(recipe.getRecipeOutput()));

        recipeIterator.remove();

        addBackup(recipe);
        return true;
    }

    public void removeByOutput(Object output) {
        if (output == null) {
            GroovyLog.msg("Error removing Thaumcraft Infusion Crafting recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
        }
        Object r;
        List<InfusionRecipe> recipes = new ArrayList<>();
        Iterator recipeIterator = ThaumcraftApi.getCraftingRecipes().values().iterator();
        while (recipeIterator.hasNext()) {
            r = recipeIterator.next();
            if((r instanceof InfusionRecipe) && ((InfusionRecipe)r).getRecipeOutput() != null
                    && ((InfusionRecipe)r).getRecipeOutput() instanceof ItemStack
                    && ((ItemStack)((InfusionRecipe)r).getRecipeOutput()).isItemEqual(((ItemStack)output))) {
                recipes.add((InfusionRecipe)r);
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Thaumcraft Infusion Crafting recipe")
                    .add("no recipes found for %s", output.toString())
                    .error()
                    .post();
            return;
        }
        recipes.forEach(recipe -> {
            this.addBackup(recipe);
            ThaumcraftApi.getCraftingRecipes().values().remove(recipe);
        });
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<InfusionRecipe> {

        private String researchKey;
        private AspectList aspects = new AspectList();
        private int instability;
        private ArrayList<Object> components = new ArrayList<Object>();

        public RecipeBuilder researchKey(String researchKey) {
            this.researchKey = researchKey;
            return this;
        }

        public RecipeBuilder aspect(AspectStack aspect) {
            this.aspects.add(aspect.getAspect(), aspect.getQuantity());
            return this;
        }

        public RecipeBuilder component(Object comp) {
            this.components.add(comp);
            return this;
        }

        public RecipeBuilder instability(int instability) {
            this.instability = instability;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thaumcraft Infusion Crafting recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            if (researchKey == null) {
                GroovyLog.msg("Warning: null researchKey provided for Thaumcraft Infusion Crafting recipe, defaulting to \"\"")
                        .warn()
                        .post();
                researchKey = "";
            }
            if (instability < 0) {
                GroovyLog.msg("Warning: negative instability provided for Thaumcraft Infusion Crafting recipe, defaulting to 0")
                        .warn()
                        .post();
                instability = 0;
            }
        }

        @Override
        public @Nullable InfusionRecipe register() {
            if (!validate()) return null;
            InfusionRecipe recipe = null;
            for (ItemStack itemStack : output) {
                InfusionRecipe recipe1 = ModSupport.THAUMCRAFT.get().infusionCrafting.add(researchKey, itemStack, instability, aspects, input.get(0).getMatchingStacks()[0], components.toArray());
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
