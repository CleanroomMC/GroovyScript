package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import org.jetbrains.annotations.Nullable;
import reborncore.api.praescriptum.fuels.Fuel;
import reborncore.api.praescriptum.fuels.FuelHandler;
import techreborn.api.recipe.Fuels;

@RegistryDescription
public class ThermalGenerator extends AbstractGeneratorRegistry {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water')).energy(10000).perTick(500)"),
            @Example(".fluidInput(fluid('lava')).energy(200).perTick(10)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public FuelHandler handler() {
        return Fuels.thermalGenerator;
    }

    @Override
    @MethodDescription(example = @Example("fluid('lava')"))
    public void removeByInput(IIngredient input) {
        super.removeByInput(input);
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<Fuel> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GT))
        private double energy;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GT))
        private double perTick;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(double energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder perTick(double perTick) {
            this.perTick = perTick;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Tech Reborn Thermal Generator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
            msg.add(perTick <= 0, "perTick must be greater than 0, yet it was {}", perTick);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Fuel register() {
            if (!validate()) return null;

            Fuel recipe = Fuels.thermalGenerator.addFuel();
            recipe.addFluidSource(fluidInput.get(0));
            recipe.withEnergyPerTick(perTick);
            recipe.withEnergyOutput(energy);

            ModSupport.TECH_REBORN.get().thermalGenerator.add(recipe);
            return recipe;
        }
    }

}
