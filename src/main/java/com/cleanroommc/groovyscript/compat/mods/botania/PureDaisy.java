package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePureDaisy;

@RegistryDescription
public class PureDaisy extends VirtualizedRegistry<RecipePureDaisy> {

    @RecipeBuilderDescription(example = @Example(".input(ore('plankWood')).output(blockstate('minecraft:clay')).time(5)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(BotaniaAPI.pureDaisyRecipes::remove);
        BotaniaAPI.pureDaisyRecipes.addAll(restoreFromBackup());
    }

    @MethodDescription(description = "groovyscript.wiki.botania.pure_daisy.add0", type = MethodDescription.Type.ADDITION)
    public RecipePureDaisy add(IBlockState output, IBlockState input, int time) {
        RecipePureDaisy recipe = new RecipePureDaisy(input, output, time);
        add(recipe);
        return recipe;
    }

    @MethodDescription(description = "groovyscript.wiki.botania.pure_daisy.add1", type = MethodDescription.Type.ADDITION)
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

    @MethodDescription(example = @Example("blockstate('botania:livingrock')"))
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

    @MethodDescription
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

    @MethodDescription(example = @Example("ore('logWood')"))
    public boolean removeByInput(OreDictIngredient input) {
        return removeByInput(input.getOreDict());
    }

    @MethodDescription(example = @Example("blockstate('minecraft:water')"))
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

    @MethodDescription
    public boolean removeByInput(Block input) {
        return removeByInput(input.getDefaultState());
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BotaniaAPI.pureDaisyRecipes.forEach(this::addBackup);
        BotaniaAPI.pureDaisyRecipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RecipePureDaisy> streamRecipes() {
        return new SimpleObjectStream<>(BotaniaAPI.pureDaisyRecipes).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<RecipePureDaisy> {

        @Property(defaultValue = "RecipePureDaisy.DEFAULT_TIME (150)", valid = @Comp(value = "0", type = Comp.Type.GTE))
        protected int time = RecipePureDaisy.DEFAULT_TIME;
        @Property(ignoresInheritedMethods = true, valid = @Comp(value = "null", type = Comp.Type.NOT))
        protected IBlockState output;
        @Property(ignoresInheritedMethods = true, requirement = "groovyscript.wiki.botania.pure_daisy.input.required", valid = @Comp(value = "null", type = Comp.Type.NOT))
        protected Object input;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int amount) {
            this.time = amount;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(IBlockState output) {
            this.output = output;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(IBlockState input) {
            this.input = input;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(Block input) {
            return input(input.getDefaultState());
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(String input) {
            this.input = input;
            return this;
        }

        @RecipeBuilderMethodDescription
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
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipePureDaisy register() {
            if (!validate()) return null;
            RecipePureDaisy recipe = new RecipePureDaisy(input, output, time);
            add(recipe);
            return recipe;
        }
    }
}
