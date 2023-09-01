package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.inworldcrafting.jei.ExplosionRecipeCategory;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Explosion extends VirtualizedRegistry<Explosion.ExplosionRecipe> {

    private final List<ExplosionRecipe> explosionRecipes = new ArrayList<>();

    @Optional.Method(modid = "jei")
    @GroovyBlacklist
    public List<ExplosionRecipeCategory.RecipeWrapper> getRecipeWrappers() {
        return this.explosionRecipes.stream().map(ExplosionRecipeCategory.RecipeWrapper::new).collect(Collectors.toList());
    }

    @Override
    public void onReload() {
        this.explosionRecipes.addAll(getBackupRecipes());
        getScriptedRecipes().forEach(this.explosionRecipes::remove);
    }

    public void add(ExplosionRecipe explosionRecipe) {
        this.explosionRecipes.add(explosionRecipe);
        addScripted(explosionRecipe);
    }

    public boolean remove(ExplosionRecipe explosionRecipe) {
        if (this.explosionRecipes.remove(explosionRecipe)) {
            addBackup(explosionRecipe);
            return true;
        }
        return false;
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class ExplosionRecipe {

        private final IIngredient input;
        private final ItemStack output;
        private final float chance;
        // modifier to approximate normal distribution
        private final float statisticalModifier;
        private final Closure<Boolean> startCondition;

        public ExplosionRecipe(IIngredient input, ItemStack output, float chance, Closure<Boolean> startCondition) {
            this.input = input;
            this.output = output;
            this.chance = chance;
            // const value based on e^(-x^2)
            this.statisticalModifier = (float) (Math.pow(1_000_000, (chance - 0.5f) * (0.5f - chance)) * 0.3f);
            this.startCondition = startCondition;
        }

        public IIngredient getInput() {
            return input;
        }

        public ItemStack getOutput() {
            return output;
        }

        public float getChance() {
            return chance;
        }

        private boolean tryRecipe(EntityItem entityItem, ItemStack itemStack) {
            if (!this.input.test(itemStack)) return false;
            if (this.startCondition != null && !ClosureHelper.call(true, this.startCondition, entityItem, itemStack)) return false;
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

    public static class RecipeBuilder extends AbstractRecipeBuilder<ExplosionRecipe> {

        private float chance = 1f;
        private Closure<Boolean> startCondition;

        public RecipeBuilder chance(float chance) {
            this.chance = chance;
            return this;
        }

        public RecipeBuilder startCondition(Closure<Boolean> startCondition) {
            this.startCondition = startCondition;
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
        public @Nullable Explosion.ExplosionRecipe register() {
            if (!validate()) return null;
            ExplosionRecipe explosionRecipe = new ExplosionRecipe(this.input.get(0), this.output.get(0), this.chance, this.startCondition);
            VanillaModule.inWorldCrafting.explosion.add(explosionRecipe);
            return explosionRecipe;
        }
    }

    @GroovyBlacklist
    public void findAndRunRecipe(EntityItem entityItem) {
        ItemStack itemStack = entityItem.getItem();
        for (ExplosionRecipe explosionRecipe : this.explosionRecipes) {
            if (explosionRecipe.tryRecipe(entityItem, itemStack)) {
                return;
            }
        }
    }
}
