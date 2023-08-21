package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.inworldcrafting.jei.PistonPushRecipeCategory;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PistonPush extends VirtualizedRegistry<PistonPush.Recipe> {

    private final List<Recipe> recipes = new ArrayList<>();

    @Optional.Method(modid = "jei")
    @GroovyBlacklist
    public List<PistonPushRecipeCategory.RecipeWrapper> getRecipeWrappers() {
        return this.recipes.stream().map(PistonPushRecipeCategory.RecipeWrapper::new).collect(Collectors.toList());
    }

    @Override
    public void onReload() {
        this.recipes.addAll(getBackupRecipes());
        getScriptedRecipes().forEach(this.recipes::remove);
    }

    public void add(Recipe recipe) {
        this.recipes.add(recipe);
        addScripted(recipe);
    }

    public boolean remove(Recipe recipe) {
        if (this.recipes.remove(recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class Recipe {

        private final IIngredient input;
        private final ItemStack output;
        private final int maxConversionsPerPush;
        private final int minHarvestLevel;
        private final Closure<Boolean> beforeRecipe;

        public Recipe(IIngredient input, ItemStack output, int maxConversionsPerPush, int minHarvestLevel, Closure<Boolean> beforeRecipe) {
            this.input = input;
            this.output = output;
            this.maxConversionsPerPush = maxConversionsPerPush;
            this.minHarvestLevel = minHarvestLevel;
            this.beforeRecipe = beforeRecipe;
        }

        public IIngredient getInput() {
            return input;
        }

        public ItemStack getOutput() {
            return output;
        }

        public int getMaxConversionsPerPush() {
            return maxConversionsPerPush;
        }

        public int getMinHarvestLevel() {
            return minHarvestLevel;
        }

        private boolean tryRecipe(Consumer<EntityItem> entitySpawner, EntityItem entityItem, ItemStack itemStack, IBlockState pushingAgainst) {
            if (!this.input.test(itemStack)) return false;
            if (this.beforeRecipe != null && !ClosureHelper.call(true, this.beforeRecipe, entityItem, itemStack, pushingAgainst)) return false;
            if (this.minHarvestLevel >= 0 && this.minHarvestLevel > pushingAgainst.getBlock().getHarvestLevel(pushingAgainst)) return false;
            ItemStack newStack = this.output.copy();
            if (this.maxConversionsPerPush < itemStack.getCount()) {
                itemStack.shrink(this.maxConversionsPerPush);
                newStack.setCount(this.maxConversionsPerPush);
                entityItem.setItem(itemStack);
                entitySpawner.accept(new EntityItem(entityItem.world, entityItem.posX, entityItem.posY, entityItem.posZ, newStack));
            } else {
                newStack.setCount(itemStack.getCount());
                entityItem.setItem(newStack);
            }
            return true;
        }
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<Recipe> {

        private int maxConversionsPerPush = 64;
        private int minHarvestLevel = -1;
        private Closure<Boolean> beforeRecipe;

        public RecipeBuilder maxConversionsPerPush(int maxConversionsPerPush) {
            this.maxConversionsPerPush = maxConversionsPerPush;
            return this;
        }

        public RecipeBuilder minHarvestLevel(int minHarvestLevel) {
            this.minHarvestLevel = minHarvestLevel;
            return this;
        }

        public RecipeBuilder beforeRecipe(Closure<Boolean> beforeRecipe) {
            this.beforeRecipe = beforeRecipe;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding in world explosion recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (this.maxConversionsPerPush < 0 || this.maxConversionsPerPush > 64) {
                GroovyLog.get().warn("Piston push recipe chance should be greater than 0 and equal or less than 64.");
                this.maxConversionsPerPush = MathHelper.clamp(this.maxConversionsPerPush, 1, 64);
            }
        }

        @Override
        public @Nullable Recipe register() {
            if (!validate()) return null;
            Recipe recipe = new Recipe(this.input.get(0), this.output.get(0), this.maxConversionsPerPush, this.minHarvestLevel, this.beforeRecipe);
            VanillaModule.inWorldCrafting.pistonPush.add(recipe);
            return null;
        }
    }

    @GroovyBlacklist
    public void findAndRunRecipe(Consumer<EntityItem> entitySpawner, EntityItem entityItem, IBlockState pushingAgainst) {
        ItemStack itemStack = entityItem.getItem();
        for (Recipe recipe : this.recipes) {
            if (recipe.tryRecipe(entitySpawner, entityItem, itemStack, pushingAgainst)) {
                return;
            }
        }
    }
}
