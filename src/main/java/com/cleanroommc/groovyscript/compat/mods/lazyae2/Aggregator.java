package com.cleanroommc.groovyscript.compat.mods.lazyae2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.threng.recipe.AggRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RegistryDescription
public class Aggregator extends StandardListRegistry<AggRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(ore('blockGlass'), item('minecraft:diamond')).output(item('minecraft:diamond') * 4)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<AggRecipe> getRegistry() {
        return LibNine.PROXY.getRecipeManager().getRecipeList(AggRecipe.class).recipes();
    }

    @MethodDescription(example = @Example("item('appliedenergistics2:material:45')"))
    public void removeByInput(IIngredient input) {
        getRegistry().removeIf(recipe -> {
            if (recipe.input().getInputs().stream().anyMatch(x -> Arrays.stream(input.getMatchingStacks()).anyMatch(x))) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('appliedenergistics2:material:7')"))
    public void removeByOutput(IIngredient output) {
        getRegistry().removeIf(recipe -> {
            if (output.test(recipe.getOutput().getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "3")})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AggRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Lazy AE2 Aggregator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 3, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AggRecipe register() {
            if (!validate()) return null;

            AggRecipe recipe = new AggRecipe(input.stream().map(LazyAE2::matchesIIngredient).collect(Collectors.toList()), output.get(0));
            ModSupport.LAZYAE2.get().aggregator.add(recipe);
            return recipe;
        }
    }

}
