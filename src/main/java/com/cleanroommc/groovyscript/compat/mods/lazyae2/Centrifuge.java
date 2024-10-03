package com.cleanroommc.groovyscript.compat.mods.lazyae2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.threng.recipe.PurifyRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription
public class Centrifuge extends StandardListRegistry<PurifyRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(ore('blockGlass')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<PurifyRecipe> getRecipes() {
        return LibNine.PROXY.getRecipeManager().getRecipeList(PurifyRecipe.class).recipes();
    }

    @MethodDescription(example = @Example("item('appliedenergistics2:material')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(recipe -> {
            if (Arrays.stream(input.getMatchingStacks()).anyMatch(recipe.input().getMatcher())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('appliedenergistics2:material:4')"))
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
