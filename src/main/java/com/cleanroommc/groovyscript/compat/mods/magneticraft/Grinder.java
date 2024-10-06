package com.cleanroommc.groovyscript.compat.mods.magneticraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Grinder extends StandardListRegistry<IGrinderRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).ticks(50)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'), item('minecraft:gold_ingot')).chance(10).ticks(50)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IGrinderRecipe> getRecipes() {
        return MagneticraftApi.getGrinderRecipeManager().getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ore')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.getInput()) && addBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:gravel')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> (output.test(r.getPrimaryOutput()) || output.test(r.getSecondaryOutput())) && addBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 2))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IGrinderRecipe> {

        @Property(comp = @Comp(gt = 0, lte = 1))
        private float chance;
        @Property(comp = @Comp(gt = 0))
        private float ticks;

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(float chance) {
            this.chance = chance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder ticks(float ticks) {
            this.ticks = ticks;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Magneticraft Grinder recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 2);
            validateFluids(msg);
            msg.add(output.size() == 2 && chance <= 0 && chance > 1, "chance must be a float greater than 0 and less than or equal to 1, yet it was {}", chance);
            msg.add(ticks <= 0, "ticks must be a float greater than 0, yet it was {}", ticks);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IGrinderRecipe register() {
            if (!validate()) return null;
            IGrinderRecipe recipe = null;
            if (input.get(0) instanceof OreDictIngredient ore) {
                recipe = MagneticraftApi.getGrinderRecipeManager().createRecipe(ore.getMatchingStacks()[0], output.get(0), output.getOrEmpty(1), chance, ticks, true);
                ModSupport.MAGNETICRAFT.get().grinder.add(recipe);
            } else {
                for (var stack : input.get(0).getMatchingStacks()) {
                    recipe = MagneticraftApi.getGrinderRecipeManager().createRecipe(stack, output.get(0), output.getOrEmpty(1), chance, ticks, false);
                    ModSupport.MAGNETICRAFT.get().grinder.add(recipe);
                }
            }
            return recipe;
        }

    }
}
