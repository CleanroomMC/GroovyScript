package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.forestry.MoistenerRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.factory.recipes.MoistenerRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Moistener extends ForestryRegistry<IMoistenerRecipe> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        if (!isEnabled()) return;
        removeScripted().forEach(MoistenerRecipeManagerAccessor.getRecipes()::remove);
        restoreFromBackup().forEach(MoistenerRecipeManagerAccessor.getRecipes()::add);
    }

    public IMoistenerRecipe add(ItemStack output, IIngredient input, int time) {
        IMoistenerRecipe recipe = new MoistenerRecipe(input.getMatchingStacks()[0], output, time);
        add(recipe);
        return recipe;
    }

    public void add(IMoistenerRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        MoistenerRecipeManagerAccessor.getRecipes().add(recipe);
    }

    public boolean remove(IMoistenerRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return MoistenerRecipeManagerAccessor.getRecipes().remove(recipe);
    }

    public boolean removeByInput(IIngredient input) {
        if (MoistenerRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = input.test(recipe.getResource());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Moistener recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(IIngredient output) {
        if (MoistenerRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = output.test(recipe.getProduct());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Moistener recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        MoistenerRecipeManagerAccessor.getRecipes().forEach(this::addBackup);
        MoistenerRecipeManagerAccessor.getRecipes().clear();
    }

    public SimpleObjectStream<IMoistenerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(MoistenerRecipeManagerAccessor.getRecipes()).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<IMoistenerRecipe> {

        protected int time = 20;

        public RecipeBuilder time(int time) {
            this.time = Math.max(time, 1);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Forestry Moistener recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg, 0, 0, 0, 0);
            validateItems(msg, 1, 1, 1, 1);
        }

        @Override
        public @Nullable IMoistenerRecipe register() {
            if (!validate()) return null;
            IMoistenerRecipe recipe = new MoistenerRecipe(input.get(0).getMatchingStacks()[0], output.get(0), time);
            add(recipe);
            return recipe;
        }
    }
}
