package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.inworldcrafting.jei.BurningRecipeCategory;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Burning extends VirtualizedRegistry<Burning.Recipe> {

    private static final Map<EntityItem, Recipe> runningRecipes = new Object2ObjectOpenHashMap<>();

    private final List<Recipe> recipes = new ArrayList<>();

    @Optional.Method(modid = "jei")
    @GroovyBlacklist
    public List<BurningRecipeCategory.RecipeWrapper> getRecipeWrappers() {
        return this.recipes.stream().map(BurningRecipeCategory.RecipeWrapper::new).collect(Collectors.toList());
    }

    @Override
    public void onReload() {
        this.recipes.addAll(getBackupRecipes());
        getScriptedRecipes().forEach(this.recipes::remove);
    }

    @Override
    public void afterScriptLoad() {
        super.afterScriptLoad();
        this.recipes.sort(Comparator.comparingInt(Recipe::getTicks));
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
        private final int ticks;
        private final Closure<Boolean> beforeRecipe;

        public Recipe(IIngredient input, ItemStack output, int ticks, Closure<Boolean> beforeRecipe) {
            this.input = input;
            this.output = output;
            this.ticks = ticks;
            this.beforeRecipe = beforeRecipe;
        }

        public IIngredient getInput() {
            return input;
        }

        public ItemStack getOutput() {
            return output;
        }

        public int getTicks() {
            return ticks;
        }

        public boolean isValidInput(EntityItem entityItem, ItemStack itemStack) {
            return this.input.test(itemStack) && (this.beforeRecipe == null || ClosureHelper.call(true, this.beforeRecipe, entityItem));
        }
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<Recipe> {

        private int ticks = 40;
        private Closure<Boolean> beforeRecipe;

        public RecipeBuilder ticks(int ticks) {
            this.ticks = ticks;
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
            if (this.ticks < 0) {
                GroovyLog.get().warn("Burning recipe chance should be greater than 0.");
                this.ticks = 40;
            }
        }

        @Override
        public @Nullable Recipe register() {
            if (!validate()) return null;
            Recipe recipe = new Recipe(this.input.get(0), this.output.get(0), this.ticks, this.beforeRecipe);
            VanillaModule.inWorldCrafting.burning.add(recipe);
            return recipe;
        }
    }

    @GroovyBlacklist
    public Recipe findRecipe(EntityItem entityItem) {
        Recipe recipe = runningRecipes.get(entityItem);
        if (recipe != null) return recipe;
        ItemStack itemStack = entityItem.getItem();
        for (Recipe recipe1 : this.recipes) {
            if (recipe1.isValidInput(entityItem, itemStack)) {
                runningRecipes.put(entityItem, recipe1);
                return recipe1;
            }
        }
        return null;
    }

    @GroovyBlacklist
    public void updateRecipeProgress(EntityItem entityItem) {
        Recipe recipe = findRecipe(entityItem);
        if (recipe == null) return;
        int prog = entityItem.getEntityData().getInteger("burn_time") + 1;
        entityItem.getEntityData().setInteger("burn_time", prog);
        entityItem.setEntityInvulnerable(true);
        if (prog >= recipe.ticks) {
            ItemStack newStack = recipe.output.copy();
            newStack.setCount(entityItem.getItem().getCount());
            entityItem.setItem(newStack);
            removeBurningItem(entityItem);
        }
    }

    @GroovyBlacklist
    public static boolean removeBurningItem(EntityItem entityItem) {
        entityItem.getEntityData().removeTag("burn_item");
        return runningRecipes.remove(entityItem) != null;
    }

    public static boolean isRunningRecipe(EntityItem entityItem) {
        return runningRecipes.containsKey(entityItem) && entityItem.getEntityData().getInteger("burn_time") > 1;
    }
}
