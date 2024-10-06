package com.cleanroommc.groovyscript.compat.mods.magneticraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.magneticraft.GasificationUnitRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.gasificationunit.IGasificationUnitRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class GasificationUnit extends StandardListRegistry<IGasificationUnitRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).duration(50).minTemperature(700)"),
            @Example(".input(item('minecraft:diamond')).fluidOutput(fluid('lava')).duration(100).minTemperature(500)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    /**
     * Internally, the API method uses {@link kotlin.collections.CollectionsKt#toMutableList},
     * so a mixin is required to access the actual list.
     *
     * @see com.cout970.magneticraft.api.internal.registries.machines.gasificationunit.GasificationUnitRecipeManager#getRecipes() MagneticraftApi.getGasificationUnitRecipeManager().getRecipes()
     */
    @Override
    public Collection<IGasificationUnitRecipe> getRecipes() {
        return GasificationUnitRecipeManagerAccessor.getRecipes();
    }

    @MethodDescription
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.getInput()) && addBackup(r));
    }

    @MethodDescription
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> (output.test(r.getItemOutput()) || output.test(r.getFluidOutput())) && addBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "fluidOutput", comp = @Comp(gte = 0, lte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IGasificationUnitRecipe> {

        @Property(comp = @Comp(gt = 0))
        private float duration;
        @Property
        private float minTemperature;

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(float duration) {
            this.duration = duration;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder minTemperature(float minTemperature) {
            this.minTemperature = minTemperature;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Magneticraft Gasification Unit recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 1);
            validateFluids(msg, 0, 0, 0, 1);
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "either output or fluidOutput must have an entry, yet both were empty");
            msg.add(duration <= 0, "duration must be a float greater than 0, yet it was {}", duration);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IGasificationUnitRecipe register() {
            if (!validate()) return null;
            IGasificationUnitRecipe recipe = null;
            if (input.get(0) instanceof OreDictIngredient ore) {
                recipe = MagneticraftApi.getGasificationUnitRecipeManager().createRecipe(ore.getMatchingStacks()[0], output.getOrEmpty(0), fluidOutput.getOrEmpty(0), duration, minTemperature, true);
                ModSupport.MAGNETICRAFT.get().gasificationUnit.add(recipe);
            } else {
                for (var stack : input.get(0).getMatchingStacks()) {
                    recipe = MagneticraftApi.getGasificationUnitRecipeManager().createRecipe(stack, output.getOrEmpty(0), fluidOutput.getOrEmpty(0), duration, minTemperature, false);
                    ModSupport.MAGNETICRAFT.get().gasificationUnit.add(recipe);
                }
            }
            return recipe;
        }

    }
}
