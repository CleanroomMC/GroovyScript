package com.cleanroommc.groovyscript.compat.mods.magneticraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.sifter.ISieveRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Sieve extends StandardListRegistry<ISieveRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).duration(50).primaryChance(0.5)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond'), item('minecraft:clay')).duration(50).primaryChance(0.05).secondaryChance(0.5)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).duration(1).chance(0.5, 0.5, 0.5)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ISieveRecipe> getRecipes() {
        return MagneticraftApi.getSieveRecipeManager().getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:sand')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.getInput()) && addBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:quartz')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> (output.test(r.getPrimary()) || output.test(r.getSecondary()) || output.test(r.getTertiary())) && addBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 3))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ISieveRecipe> {

        @Property(comp = @Comp(gt = 0))
        private float duration;
        @Property(comp = @Comp(gt = 0, lte = 1))
        private float primaryChance;
        @Property(comp = @Comp(gt = 0, lte = 1))
        private float secondaryChance;
        @Property(comp = @Comp(gt = 0, lte = 1))
        private float tertiaryChance;

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(float duration) {
            this.duration = duration;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"primaryChance", "secondaryChance", "tertiaryChance"})
        public RecipeBuilder chance(float primaryChance, float secondaryChance, float tertiaryChance) {
            this.primaryChance = primaryChance;
            this.secondaryChance = secondaryChance;
            this.tertiaryChance = tertiaryChance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder primaryChance(float primaryChance) {
            this.primaryChance = primaryChance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder secondaryChance(float secondaryChance) {
            this.secondaryChance = secondaryChance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder tertiaryChance(float tertiaryChance) {
            this.tertiaryChance = tertiaryChance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Magneticraft Sieve recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 3);
            validateFluids(msg);
            msg.add(output.size() >= 1 && primaryChance <= 0 && primaryChance > 1, "primaryChance must be a float greater than 0 and less than or equal to 1, yet it was {}", primaryChance);
            msg.add(output.size() >= 2 && secondaryChance <= 0 && secondaryChance > 1, "secondaryChance must be a float greater than 0 and less than or equal to 1, yet it was {}", secondaryChance);
            msg.add(output.size() >= 3 && tertiaryChance <= 0 && tertiaryChance > 1, "tertiaryChance must be a float greater than 0 and less than or equal to 1, yet it was {}", tertiaryChance);
            msg.add(duration <= 0, "duration must be a float greater than 0, yet it was {}", duration);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ISieveRecipe register() {
            if (!validate()) return null;
            ISieveRecipe recipe = null;
            if (input.get(0) instanceof OreDictIngredient ore) {
                recipe = MagneticraftApi.getSieveRecipeManager().createRecipe(ore.getMatchingStacks()[0], output.get(0), primaryChance, output.getOrEmpty(1), secondaryChance, output.getOrEmpty(2), tertiaryChance, duration, true);
                ModSupport.MAGNETICRAFT.get().sieve.add(recipe);
            } else {
                for (var stack : input.get(0).getMatchingStacks()) {
                    recipe = MagneticraftApi.getSieveRecipeManager().createRecipe(stack, output.get(0), primaryChance, output.getOrEmpty(1), secondaryChance, output.getOrEmpty(2), tertiaryChance, duration, false);
                    ModSupport.MAGNETICRAFT.get().sieve.add(recipe);
                }
            }
            return recipe;
        }

    }

}
