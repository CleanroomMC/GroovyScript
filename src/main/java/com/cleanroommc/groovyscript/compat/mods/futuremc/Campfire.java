package com.cleanroommc.groovyscript.compat.mods.futuremc;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import thedarkcolour.futuremc.recipe.campfire.CampfireRecipe;
import thedarkcolour.futuremc.recipe.campfire.CampfireRecipes;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription
public class Campfire extends StandardListRegistry<CampfireRecipe> {

    @Override
    public Collection<CampfireRecipe> getRecipes() {
        return CampfireRecipes.INSTANCE.getRecipes();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).duration(10)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay')).duration(1)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:fish')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> Arrays.stream(r.getInput().getMatchingStacks()).anyMatch(input) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:cooked_mutton')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CampfireRecipe> {

        @Property(comp = @Comp(gte = 1))
        private int duration;

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding FutureMC Campfire recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(duration <= 0, "duration must be greater than or equal to 1, yet it was {}", duration);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CampfireRecipe register() {
            if (!validate()) return null;
            CampfireRecipe recipe = new CampfireRecipe(input.get(0).toMcIngredient(), output.get(0), duration);
            ModSupport.FUTURE_MC.get().campfire.add(recipe);
            return recipe;
        }
    }
}
