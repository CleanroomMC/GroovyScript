package com.cleanroommc.groovyscript.compat.mods.magneticraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.oilheater.IOilHeaterRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class OilHeater extends StandardListRegistry<IOilHeaterRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water')).fluidOutput(fluid('lava')).duration(50).minTemperature(200)"),
            @Example(".fluidInput(fluid('lava')).fluidOutput(fluid('water')).duration(100).minTemperature(50)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IOilHeaterRecipe> getRecipes() {
        return MagneticraftApi.getOilHeaterRecipeManager().getRecipes();
    }

    @MethodDescription(example = @Example("fluid('oil')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.getInput()) && addBackup(r));
    }

    @MethodDescription(example = @Example("fluid('steam')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && addBackup(r));
    }

    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IOilHeaterRecipe> {

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
            return "Error adding Magneticraft Oil Heater recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 1, 1);
            msg.add(duration <= 0, "duration must be a float greater than 0, yet it was {}", duration);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IOilHeaterRecipe register() {
            if (!validate()) return null;
            IOilHeaterRecipe recipe = MagneticraftApi.getOilHeaterRecipeManager().createRecipe(fluidInput.get(0), fluidOutput.get(0), duration, minTemperature);
            ModSupport.MAGNETICRAFT.get().oilHeater.add(recipe);
            return recipe;
        }

    }
}
