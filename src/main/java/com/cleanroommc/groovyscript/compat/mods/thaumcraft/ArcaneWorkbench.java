package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.ingredient.ItemsIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;

import java.util.ArrayList;

import static thaumcraft.common.config.ConfigRecipes.compileGroups;

public class ArcaneWorkbench extends VirtualizedRegistry<IArcaneRecipe> {

    public ArcaneWorkbench() {
        super("ArcaneWorkbench", "arcane_workbench");
    }

    public RecipeBuilder recipeBuilder() { return new RecipeBuilder(); }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> ThaumcraftApi.getCraftingRecipes().values().remove(recipe));
        restoreFromBackup().forEach(recipe -> {
            if (!ThaumcraftApi.getCraftingRecipes().values().contains(recipe))
                ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(recipe.getRecipeOutput().toString()), recipe);
        });
        compileGroups();
    }

    public void add(IArcaneRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(recipe.getRecipeOutput().toString()), recipe);
        }
    }

    public IArcaneRecipe addShaped(String researchKey, int vis, AspectList crystals, Block result, Object[] recipe) {
        IArcaneRecipe arcaneRecipe = new ShapedArcaneRecipe(new ResourceLocation(""), researchKey, vis, crystals, result, recipe);
        add(arcaneRecipe);
        return arcaneRecipe;
    }

    public IArcaneRecipe addShaped(String researchKey, int vis, AspectList crystals, Item result, Object[] recipe) {
        IArcaneRecipe arcaneRecipe = new ShapedArcaneRecipe(new ResourceLocation(""), researchKey, vis, crystals, result, recipe);
        add(arcaneRecipe);
        return arcaneRecipe;
    }

    public IArcaneRecipe addShapeless(String researchKey, int vis, AspectList crystals, Block result, Object[] recipe) {
        IArcaneRecipe arcaneRecipe = new ShapelessArcaneRecipe(new ResourceLocation(""), researchKey, vis, crystals, result, recipe);
        add(arcaneRecipe);
        return arcaneRecipe;
    }

    public IArcaneRecipe addShapeless(String researchKey, int vis, AspectList crystals, Item result, Object[] recipe) {
        IArcaneRecipe arcaneRecipe = new ShapelessArcaneRecipe(new ResourceLocation(""), researchKey, vis, crystals, result, recipe);
        add(arcaneRecipe);
        return arcaneRecipe;
    }

    public boolean remove(IArcaneRecipe recipe) {
        addBackup(recipe);
        removeByOutput(recipe.getRecipeOutput());
        return true;
    }

    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Thaumcraft Arcane Workbench recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
        }
        VanillaModule.crafting.removeByOutput(new ItemsIngredient(output));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<IArcaneRecipe> {

        private String researchKey;
        private AspectList aspects;
        private int vis;

        private Object[] recipe;
        private ArrayList<ItemStack> ingList = new ArrayList<ItemStack>();

        private boolean shaped = true;

        public ArcaneWorkbench.RecipeBuilder researchKey(String researchKey) {
            this.researchKey = researchKey;
            return this;
        }

        public ArcaneWorkbench.RecipeBuilder aspects(AspectList aspects) {
            this.aspects = aspects;
            return this;
        }

        public ArcaneWorkbench.RecipeBuilder vis(int vis) {
            this.vis = vis;
            return this;
        }

        public ArcaneWorkbench.RecipeBuilder recipe(Object[] recipe) {
            this.recipe = recipe;
            return this;
        }

        public ArcaneWorkbench.RecipeBuilder shapeless() {
            this.shaped = false;
            return this;
        }

        public ArcaneWorkbench.RecipeBuilder input(ItemStack items, int quantity) {
            items.setCount(quantity);
            this.ingList.add(items);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thaumcraft Arcane Workbench recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
        }

        @Override
        public @Nullable IArcaneRecipe register() {
            if (!validate()) return null;
            IArcaneRecipe recipe = null;
            IArcaneRecipe recipe1;
            for (ItemStack itemStack : output) {
                if (this.shaped) {
                    recipe1 = ModSupport.THAUMCRAFT.get().arcaneWorkbench.addShaped(researchKey, vis, aspects, itemStack.getItem(), this.recipe);
                } else {
                    recipe1 = ModSupport.THAUMCRAFT.get().arcaneWorkbench.addShapeless(researchKey, vis, aspects, itemStack.getItem(), this.ingList.toArray());
                }
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
