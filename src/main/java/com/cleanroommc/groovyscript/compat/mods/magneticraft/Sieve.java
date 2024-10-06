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
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Sieve extends StandardListRegistry<ISieveRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'), 0.5).duration(50)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond'), 0.05).output(item('minecraft:clay')).duration(50)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'), 0.5).output(item('minecraft:clay'), 0.5).output(item('minecraft:clay'), 0.5).duration(1)")
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

        @Property(comp = @Comp(gt = 0, lte = 100, unique = "groovyscript.wiki.magneticraft.sieve.chances.required"))
        private final FloatArrayList chances = new FloatArrayList();
        @Property(comp = @Comp(gt = 0))
        private float duration;

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(float duration) {
            this.duration = duration;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {
                "output", "chances"
        })
        public RecipeBuilder output(ItemStack item, float chance) {
            this.output.add(item);
            this.chances.add(chance);
            return this;
        }

        @Override
        @RecipeBuilderMethodDescription(field = {
                "output", "chances"
        })
        public RecipeBuilder output(ItemStack item) {
            return output(item, 1.0f);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Magneticraft Sieve recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 3);
            validateFluids(msg);
            chances.trim();
            msg.add(output.size() != chances.size(), "output and chances must be the same length, yet output was {} and chances was {}", output.size(), chances.size());
            for (float chance : chances.elements()) {
                msg.add(chance <= 0 || chance > 1, "each chance value must be a float greater than 0 and less than or equal to 1, yet a chance value was {}", chance);
            }
            msg.add(duration <= 0, "duration must be a float greater than 0, yet it was {}", duration);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ISieveRecipe register() {
            if (!validate()) return null;
            ISieveRecipe recipe = null;
            float primaryChance = chances.size() >= 1 ? chances.getFloat(0) : 0;
            float secondaryChance = chances.size() >= 2 ? chances.getFloat(1) : 0;
            float tertiaryChance = chances.size() >= 3 ? chances.getFloat(2) : 0;
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
