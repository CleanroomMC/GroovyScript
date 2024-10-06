package com.cleanroommc.groovyscript.compat.mods.lazyae2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.threng.recipe.EnergizeRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription
public class Energizer extends StandardListRegistry<EnergizeRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(ore('blockGlass')).energy(50).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot')).energy(10000).output(item('minecraft:diamond'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<EnergizeRecipe> getRecipes() {
        return LibNine.PROXY.getRecipeManager().getRecipeList(EnergizeRecipe.class).recipes();
    }

    @MethodDescription(example = @Example(value = "item('appliedenergistics2:material')", commented = true))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(recipe -> {
            if (Arrays.stream(input.getMatchingStacks()).anyMatch(recipe.input().getMatcher())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('appliedenergistics2:material:1')"))
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
    public static class RecipeBuilder extends AbstractRecipeBuilder<EnergizeRecipe> {

        @Property(comp = @Comp(gt = 0))
        private int energy;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Lazy AE2 Energizer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 3, 1, 1);
            validateFluids(msg);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable EnergizeRecipe register() {
            if (!validate()) return null;

            EnergizeRecipe recipe = new EnergizeRecipe(LazyAE2.matchesIIngredient(input.get(0)), energy, output.get(0));
            ModSupport.LAZYAE2.get().energizer.add(recipe);
            return recipe;
        }

    }
}
