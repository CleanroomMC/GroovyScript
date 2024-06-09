package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import org.jetbrains.annotations.Nullable;
import reborncore.api.praescriptum.recipes.Recipe;
import reborncore.api.praescriptum.recipes.RecipeHandler;
import techreborn.api.recipe.Recipes;

import java.util.stream.Collectors;

@RegistryDescription
public class SolidCanningMachine extends AbstractPraescriptumRegistry {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:diamond') * 2).output(item('minecraft:gold_ingot')).time(10).perTick(100)"),
            @Example(".input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2).output(item('minecraft:clay') * 2).time(5).perTick(32)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public RecipeHandler handler() {
        return Recipes.solidCanningMachine;
    }

    @Override
    @MethodDescription(example = @Example("item('techreborn:ingot:23')"))
    public void removeByInput(IIngredient input) {
        super.removeByInput(input);
    }

    @Override
    @MethodDescription(example = @Example("item('techreborn:part:46')"))
    public void removeByOutput(IIngredient output) {
        super.removeByOutput(output);
    }

    @Property(property = "input", valid = @Comp("2"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<Recipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GT))
        private int time;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GT))
        private int perTick;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder perTick(int perTick) {
            this.perTick = perTick;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Tech Reborn Solid Canning Machine recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
            msg.add(time <= 0, "time must be greater than 0, yet it was {}", time);
            msg.add(perTick <= 0, "perTick must be greater than 0, yet it was {}", perTick);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Recipe register() {
            if (!validate()) return null;

            Recipe recipe = Recipes.solidCanningMachine.createRecipe();
            recipe.withInput(input.stream().map(TechReborn::toInputIngredient).collect(Collectors.toList()));
            output.forEach(recipe::withOutput);
            recipe.withEnergyCostPerTick(perTick);
            recipe.withOperationDuration(time);

            ModSupport.TECH_REBORN.get().solidCanningMachine.add(recipe);
            return recipe;
        }
    }

}
