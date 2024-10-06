package com.cleanroommc.groovyscript.compat.mods.magneticraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.refinery.IRefineryRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Refinery extends StandardListRegistry<IRefineryRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water')).fluidOutput(fluid('lava')).duration(50)"),
            @Example(".fluidInput(fluid('lava')).fluidOutput(fluid('water')).duration(100)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IRefineryRecipe> getRecipes() {
        return MagneticraftApi.getRefineryRecipeManager().getRecipes();
    }

    @MethodDescription(example = @Example("fluid('steam')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.getInput()) && addBackup(r));
    }

    @MethodDescription(example = @Example("fluid('fuel')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> (output.test(r.getOutput0()) || output.test(r.getOutput1()) || output.test(r.getOutput2())) && addBackup(r));
    }

    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(gte = 1, lte = 3))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IRefineryRecipe> {

        @Property(comp = @Comp(gt = 0))
        private float duration;

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(float duration) {
            this.duration = duration;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Magneticraft Refinery recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 1, 3);
            msg.add(duration <= 0, "duration must be a float greater than 0, yet it was {}", duration);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IRefineryRecipe register() {
            if (!validate()) return null;
            IRefineryRecipe recipe = MagneticraftApi.getRefineryRecipeManager().createRecipe(fluidInput.get(0), fluidOutput.get(0), fluidOutput.getOrEmpty(1), fluidOutput.getOrEmpty(2), duration);
            ModSupport.MAGNETICRAFT.get().refinery.add(recipe);
            return recipe;
        }
    }
}
