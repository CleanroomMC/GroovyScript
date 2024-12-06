package com.cleanroommc.groovyscript.compat.mods.betweenlands;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thebetweenlands.SmokingRackRecipeAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.ISmokingRackRecipe;
import thebetweenlands.common.recipe.misc.SmokingRackRecipe;

import java.util.Collection;

@RegistryDescription
public class SmokingRack extends StandardListRegistry<ISmokingRackRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay')).time(50)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ISmokingRackRecipe> getRecipes() {
        return SmokingRackRecipeAccessor.getRecipes();
    }

    @MethodDescription(example = @Example("item('thebetweenlands:anadia')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.getInput()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('thebetweenlands:barnacle_smoked')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput(r.getInput())) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ISmokingRackRecipe> {

        @Property(defaultValue = "1", comp = @Comp(gte = 1))
        private int time = 1;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Betweenlands Smoking Rack recipe";
        }

        @Override
        @GroovyBlacklist
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(time <= 0, "time must be a positive integer greater than 0, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ISmokingRackRecipe register() {
            if (!validate()) return null;
            ISmokingRackRecipe recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new SmokingRackRecipe(output.get(0), time, stack);
                ModSupport.BETWEENLANDS.get().smokingRack.add(recipe);
            }
            return recipe;
        }
    }
}
