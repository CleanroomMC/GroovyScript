package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.*;

import java.util.*;

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
//        removeScripted().forEach(recipe -> remove(recipe));
//        restoreFromBackup().forEach(recipe -> add(recipe));
        compileGroups();
    }

    public void add(IArcaneRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            try {
                recipe.setRegistryName(new ResourceLocation(recipe.getRecipeOutput().getItem().getRegistryName().toString()));
            } catch (IllegalStateException e) {}
            try {
                GameData.register_impl(recipe);
            } catch (IllegalArgumentException e) {}
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

        List<ResourceLocation> removed = new ArrayList<>();

        for(Map.Entry<ResourceLocation, IRecipe> entry : ForgeRegistries.RECIPES.getEntries()) {
            if(entry.getValue() instanceof IArcaneRecipe) {
                if(output.isItemEqual(entry.getValue().getRecipeOutput())) {
                    removed.add(entry.getKey());
                    this.addBackup((IArcaneRecipe)entry.getValue());
                } else if (entry.getKey().toString().equals(output.getItem().getRegistryName().toString())) {
                    removed.add(entry.getKey());
                    this.addBackup((IArcaneRecipe)entry.getValue());
                }
            }
        }

        if (removed.isEmpty()) {
            GroovyLog.msg("Error removing Thaumcraft Arcane Workbench recipe")
                    .add("no recipes found for %s", output)
                    .error()
                    .post();
            return;
        }

        removed.forEach(RegistryManager.ACTIVE.getRegistry(GameData.RECIPES)::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<IArcaneRecipe> {

        private String researchKey;
        private AspectList aspects = new AspectList();
        private int vis;

        private ArrayList<Object> recipe = new ArrayList<Object>();
        private ArrayList<String> matrix = new ArrayList<String>();

        private ArrayList<String> keyChars = new ArrayList<String>();
        private ArrayList<ItemStack> keyItems = new ArrayList<ItemStack>();
        private ArrayList<ItemStack> ingList = new ArrayList<ItemStack>();

        private boolean shaped = true;

        public RecipeBuilder researchKey(String researchKey) {
            this.researchKey = researchKey;
            return this;
        }

        public RecipeBuilder aspect(thaumcraft.api.aspects.Aspect aspectIn, int amount) {
            this.aspects.add(aspectIn, amount);
            return this;
        }

        public RecipeBuilder vis(int vis) {
            this.vis = vis;
            return this;
        }

        public RecipeBuilder matrixRow(String row) {
            this.matrix.add(row);
            return this;
        }

        public RecipeBuilder key(String c, ItemStack item) {
            this.keyChars.add(c);
            this.keyItems.add(item);
            return this;
        }

        public RecipeBuilder shapeless() {
            this.shaped = false;
            return this;
        }

        public RecipeBuilder input(ItemStack item) {
            this.ingList.add(item);
            return this;
        }

        public RecipeBuilder input(ItemStack item, int quantity) {
            for (int i = 0; i < quantity; i++)
                this.ingList.add(item);
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
                    for (String row : matrix) {
                        this.recipe.add(row);
                    }
                    for (int i = 0; i < keyChars.size(); i++) {
                        this.recipe.add(keyChars.get(i).charAt(0));
                        this.recipe.add(keyItems.get(i));
                    }
                    recipe1 = ModSupport.THAUMCRAFT.get().arcaneWorkbench.addShaped(researchKey, vis, aspects, itemStack.getItem(), this.recipe.toArray());
                } else {
                    recipe1 = ModSupport.THAUMCRAFT.get().arcaneWorkbench.addShapeless(researchKey, vis, aspects, itemStack.getItem(), this.ingList.toArray());
                }
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
