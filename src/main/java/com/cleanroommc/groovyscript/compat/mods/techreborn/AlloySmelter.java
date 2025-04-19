package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import org.jetbrains.annotations.Nullable;
import reborncore.api.praescriptum.recipes.Recipe;
import reborncore.api.praescriptum.recipes.RecipeHandler;
import techreborn.api.recipe.Recipes;

import java.util.stream.Collectors;

@RegistryDescription(override = @MethodOverride(method = {
        @MethodDescription(method = "removeByInput", example = @Example("item('techreborn:ingot:4')")),
        @MethodDescription(method = "removeByOutput", example = @Example("item('techreborn:ingot:5')"))
}))
public class AlloySmelter extends AbstractPraescriptumRegistry {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:diamond') * 2).output(item('minecraft:gold_ingot')).time(10).perTick(100)"),
            @Example(".input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2).output(item('minecraft:clay') * 2).time(5).perTick(32)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public RecipeHandler handler() {
        return Recipes.alloySmelter;
    }

    @Property(property = "input", comp = @Comp(eq = 2))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<Recipe> {

        @Property(comp = @Comp(gt = 0))
        private int time;
        @Property(comp = @Comp(gt = 0))
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
            return "Error adding Tech Reborn Alloy Smelter recipe";
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

            Recipe recipe = Recipes.alloySmelter.createRecipe();
            recipe.withInput(input.stream().map(Helper::toInputIngredient).collect(Collectors.toList()));
            output.forEach(recipe::withOutput);
            recipe.withEnergyCostPerTick(perTick);
            recipe.withOperationDuration(time);

            ModSupport.TECH_REBORN.get().alloySmelter.add(recipe);
            return recipe;
        }
    }
}
