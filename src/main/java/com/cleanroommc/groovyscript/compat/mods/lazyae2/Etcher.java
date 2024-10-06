package com.cleanroommc.groovyscript.compat.mods.lazyae2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.threng.recipe.EtchRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription
public class Etcher extends StandardListRegistry<EtchRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(ore('blockGlass')).top(item('minecraft:diamond')).bottom(item('minecraft:clay')).output(item('minecraft:diamond') * 5)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<EtchRecipe> getRecipes() {
        return LibNine.PROXY.getRecipeManager().getRecipeList(EtchRecipe.class).recipes();
    }

    @MethodDescription(example = @Example("item('minecraft:diamond')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(recipe -> {
            if (recipe.input().getInputs().stream().anyMatch(x -> Arrays.stream(input.getMatchingStacks()).anyMatch(x))) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('appliedenergistics2:material:22')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getOutput().getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<EtchRecipe> {

        @Property(defaultValue = "IIngredient.EMPTY")
        private IIngredient top = IIngredient.EMPTY;
        @Property(defaultValue = "IIngredient.EMPTY")
        private IIngredient bottom = IIngredient.EMPTY;

        @RecipeBuilderMethodDescription
        public RecipeBuilder top(IIngredient top) {
            this.top = top;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder bottom(IIngredient bottom) {
            this.bottom = bottom;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Lazy AE2 Etcher recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable EtchRecipe register() {
            if (!validate()) return null;

            EtchRecipe recipe = new EtchRecipe(LazyAE2.matchesIIngredient(top), LazyAE2.matchesIIngredient(bottom), LazyAE2.matchesIIngredient(input.get(0)), output.get(0));
            ModSupport.LAZYAE2.get().etcher.add(recipe);
            return recipe;
        }

    }

}
