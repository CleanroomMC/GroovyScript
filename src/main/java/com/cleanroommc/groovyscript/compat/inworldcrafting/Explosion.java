package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Explosion extends VirtualizedRegistry<Explosion.Recipe> {

    private final List<Recipe> recipes = new ArrayList<>();

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
        private final float chance;
        // modifier to approximate normal distribution
        private final float statisticalModifier;
        private final Closure<Boolean> beforeRecipe;

        public Recipe(IIngredient input, ItemStack output, float chance, Closure<Boolean> beforeRecipe) {
            this.input = input;
            this.output = output;
            this.chance = chance;
            // const value based on e^(-x^2)
            this.statisticalModifier = (float) (Math.pow(1_000_000, (chance - 0.5f) * (0.5f - chance)) * 0.3f);
            this.beforeRecipe = beforeRecipe;
        }

        private boolean tryRecipe(EntityItem entityItem, ItemStack itemStack) {
            if (!this.input.test(itemStack)) return false;
            if (this.beforeRecipe != null && !ClosureHelper.call(true, this.beforeRecipe, entityItem, itemStack)) return false;
            int count = itemStack.getCount();
            int amountToReplace;
            if (this.chance >= 1f) {
                amountToReplace = count;
            } else {
                // only get 1 random value and approximate a normal distribution (instead of for each item in the stack)
                // technically count * chance would also work, but that's boring
                float c = this.chance - this.statisticalModifier / 2;
                amountToReplace = (int) (count * (c + GroovyScript.RND.nextFloat() * this.statisticalModifier) + 0.5f);
                amountToReplace = MathHelper.clamp(amountToReplace, 0, count);
            }
            if (amountToReplace == 0) return true;
            ItemStack newStack = this.output.copy();
            newStack.setCount(amountToReplace);
            if (amountToReplace == count) {
                entityItem.setItem(newStack);
            } else {
                itemStack.shrink(amountToReplace);
                entityItem.setItem(itemStack);
                EntityItem newEntityItem = new EntityItem(entityItem.world, entityItem.posX, entityItem.posY, entityItem.posZ, newStack);
                entityItem.world.spawnEntity(newEntityItem);
            }
            return true;
        }
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<Recipe> {

        private float chance = 1f;
        private Closure<Boolean> beforeRecipe;

        public RecipeBuilder chance(float chance) {
            this.chance = chance;
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
            if (this.chance < 0 || this.chance > 1) {
                GroovyLog.get().warn("Explosion recipe chance should be greater than 0 and equal or less than 1.");
                this.chance = 1f;
            }
        }

        @Override
        public @Nullable Recipe register() {
            if (!validate()) return null;
            Recipe recipe = new Recipe(this.input.get(0), this.output.get(0), this.chance, this.beforeRecipe);
            VanillaModule.inWorldCrafting.explosion.add(recipe);
            return recipe;
        }
    }

    @GroovyBlacklist
    public void findAndRunRecipe(EntityItem entityItem) {
        ItemStack itemStack = entityItem.getItem();
        for (Recipe recipe : this.recipes) {
            if (recipe.tryRecipe(entityItem, itemStack)) {
                return;
            }
        }
    }
}
