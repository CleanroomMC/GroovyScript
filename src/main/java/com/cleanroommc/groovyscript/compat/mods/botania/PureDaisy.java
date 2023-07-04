package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePureDaisy;

public class PureDaisy extends VirtualizedRegistry<RecipePureDaisy> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(BotaniaAPI.pureDaisyRecipes::remove);
        BotaniaAPI.pureDaisyRecipes.addAll(restoreFromBackup());
    }

    public RecipePureDaisy add(IBlockState output, IBlockState input, int time) {
        RecipePureDaisy recipe = new RecipePureDaisy(input, output, time);
        add(recipe);
        return recipe;
    }

    public RecipePureDaisy add(IBlockState output, IBlockState input) {
        return add(output, input, RecipePureDaisy.DEFAULT_TIME);
    }

    public void add(RecipePureDaisy recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        BotaniaAPI.pureDaisyRecipes.add(recipe);
    }

    public boolean remove(RecipePureDaisy recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return BotaniaAPI.pureDaisyRecipes.remove(recipe);
    }

    public boolean removeByOutput(IBlockState output) {
        if (BotaniaAPI.pureDaisyRecipes.removeIf(recipe -> {
            boolean found = recipe.getOutputState().equals(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Pure Daisy recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(String input) {
        if (BotaniaAPI.pureDaisyRecipes.removeIf(recipe -> {
            boolean found = recipe.getInput() instanceof String && recipe.getInput().equals(input);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Pure Daisy recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(OreDictIngredient input) {
        return removeByInput(input.getOreDict());
    }

    public boolean removeByInput(IBlockState input) {
        if (BotaniaAPI.pureDaisyRecipes.removeIf(recipe -> {
            boolean found = (recipe.getInput() instanceof IBlockState && recipe.getInput().equals(input)) || (recipe.getInput() instanceof Block && recipe.getInput() == input.getBlock());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Pure Daisy recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(Block input) {
        return removeByInput(input.getDefaultState());
    }

    public void removeAll() {
        BotaniaAPI.pureDaisyRecipes.forEach(this::addBackup);
        BotaniaAPI.pureDaisyRecipes.clear();
    }

    public SimpleObjectStream<RecipePureDaisy> streamRecipes() {
        return new SimpleObjectStream<>(BotaniaAPI.pureDaisyRecipes).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<RecipePureDaisy> {

        protected int time = RecipePureDaisy.DEFAULT_TIME;
        protected IBlockState output;
        protected Object input;

        public RecipeBuilder time(int amount) {
            this.time = amount;
            return this;
        }

        public RecipeBuilder output(IBlockState output) {
            this.output = output;
            return this;
        }

        public RecipeBuilder input(IBlockState input) {
            this.input = input;
            return this;
        }

        public RecipeBuilder input(Block input) {
            return input(input.getDefaultState());
        }

        public RecipeBuilder input(String input) {
            this.input = input;
            return this;
        }

        public RecipeBuilder input(OreDictIngredient input) {
            return input(input.getOreDict());
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Botania Pure Daisy recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 0, 0);
            validateFluids(msg, 0, 0, 0, 0);
            msg.add(time < 0, "time must be at least 1, got " + time);
            msg.add(output == null, "output must be defined");
            msg.add(input == null || !(input instanceof String || input instanceof IBlockState), "expected IBlockState or String input, got {}", input);
        }

        @Override
        public @Nullable RecipePureDaisy register() {
            if (!validate()) return null;
            RecipePureDaisy recipe = new RecipePureDaisy(input, output, time);
            add(recipe);
            return recipe;
        }
    }
}
