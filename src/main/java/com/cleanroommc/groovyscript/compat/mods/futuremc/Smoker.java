package com.cleanroommc.groovyscript.compat.mods.futuremc;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import thedarkcolour.futuremc.recipe.SimpleRecipe;
import thedarkcolour.futuremc.recipe.furnace.SmokerRecipes;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription
public class Smoker extends StandardListRegistry<SimpleRecipe> {

    @Override
    public Collection<SimpleRecipe> getRecipes() {
        return SmokerRecipes.INSTANCE.getRecipes();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:porkchop')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> Arrays.stream(r.getInput().getMatchingStacks()).anyMatch(input) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:baked_potato')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<SimpleRecipe> {

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding FutureMC Smoker recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SimpleRecipe register() {
            if (!validate()) return null;
            SimpleRecipe recipe = new SimpleRecipe(input.get(0).toMcIngredient(), output.get(0));
            ModSupport.FUTURE_MC.get().smoker.add(recipe);
            return recipe;
        }
    }
}
