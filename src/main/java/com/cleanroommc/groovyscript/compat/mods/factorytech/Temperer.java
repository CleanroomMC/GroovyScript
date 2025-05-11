package com.cleanroommc.groovyscript.compat.mods.factorytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import dalapo.factech.auxiliary.MachineRecipes;
import dalapo.factech.tileentity.specialized.TileEntityTemperer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Temperer extends StandardListRegistry<TileEntityTemperer.TempererRecipe> {

    @Override
    public Collection<TileEntityTemperer.TempererRecipe> getRecipes() {
        return MachineRecipes.TEMPERER;
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay')).time(30)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ingot')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> input.test(r.getInput()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('factorytech:machinepart:4')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> output.test(r.getOutputStack()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<TileEntityTemperer.TempererRecipe> {

        @Property(defaultValue = "5", comp = @Comp(gte = 5))
        private int time = 5;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Factory Tech Temperer recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(time < 5, "time must be an integer greater than or equal to 5, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable TileEntityTemperer.TempererRecipe register() {
            if (!validate()) return null;
            TileEntityTemperer.TempererRecipe recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new TileEntityTemperer.TempererRecipe(stack, output.get(0), time);
                ModSupport.FACTORY_TECH.get().temperer.add(recipe);
            }
            return recipe;
        }
    }
}
