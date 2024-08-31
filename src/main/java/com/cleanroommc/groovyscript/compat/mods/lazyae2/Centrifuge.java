package com.cleanroommc.groovyscript.compat.mods.lazyae2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.threng.recipe.PurifyRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription
public class Centrifuge extends VirtualizedRegistry<PurifyRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(ore('blockGlass')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    private static Collection<PurifyRecipe> recipes() {
        return LibNine.PROXY.getRecipeManager().getRecipeList(PurifyRecipe.class).recipes();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipes()::remove);
        restoreFromBackup().forEach(recipes()::add);
    }

    public void add(PurifyRecipe recipe) {
        recipes().add(recipe);
        addScripted(recipe);
    }

    public void remove(PurifyRecipe recipe) {
        recipes().remove(recipe);
        addBackup(recipe);
    }

    @MethodDescription(example = @Example("item('appliedenergistics2:material')"))
    public void removeByInput(IIngredient input) {
        recipes().removeIf(recipe -> {
            if (Arrays.stream(input.getMatchingStacks()).anyMatch(recipe.input().getMatcher())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('appliedenergistics2:material:4')"))
    public void removeByOutput(IIngredient output) {
        recipes().removeIf(recipe -> {
            if (output.test(recipe.getOutput().getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        recipes().forEach(this::addBackup);
        recipes().clear();
    }

    @Property(property = "input", comp = @Comp(types = Comp.Type.EQ, eq = 1))
    @Property(property = "output", comp = @Comp(types = Comp.Type.EQ, eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<PurifyRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Lazy AE2 Centrifuge recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 3, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable PurifyRecipe register() {
            if (!validate()) return null;

            PurifyRecipe recipe = new PurifyRecipe(LazyAE2.matchesIIngredient(input.get(0)), output.get(0));
            ModSupport.LAZYAE2.get().centrifuge.add(recipe);
            return recipe;
        }
    }

}
