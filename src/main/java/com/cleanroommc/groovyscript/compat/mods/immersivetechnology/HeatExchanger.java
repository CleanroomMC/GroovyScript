package com.cleanroommc.groovyscript.compat.mods.immersivetechnology;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import mctmods.immersivetechnology.api.crafting.HeatExchangerRecipe;
import mctmods.immersivetechnology.common.Config;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class HeatExchanger extends StandardListRegistry<HeatExchangerRecipe> {

    @Override
    public boolean isEnabled() {
        return Config.ITConfig.Machines.Multiblock.enable_heatExchanger;
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 100, fluid('lava') * 50).fluidOutput(fluid('hot_spring_water') * 500).time(100)"),
            @Example(".fluidInput(fluid('water') * 50, fluid('hot_spring_water') * 50).fluidOutput(fluid('lava') * 50, fluid('water') * 10).time(50).energy(5000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<HeatExchangerRecipe> getRecipes() {
        return HeatExchangerRecipe.recipeList;
    }

    @MethodDescription(example = @Example("fluid('fluegas')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> {
            for (FluidStack fluidStack : r.getFluidInputs()) {
                if (input.test(fluidStack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('hot_spring_water')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> {
            // would iterate through r.getFluidOutputs() as with the other IE compats, but they forgot to define it so its null.
            if (output.test(r.fluidOutput0) || output.test(r.fluidOutput1)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @Property(property = "fluidInput", comp = @Comp(eq = 2))
    @Property(property = "fluidOutput", comp = @Comp(gte = 1, lte = 2))
    public static class RecipeBuilder extends AbstractRecipeBuilder<HeatExchangerRecipe> {

        @Property(comp = @Comp(gte = 0))
        private int time;
        @Property(comp = @Comp(gte = 0))
        private int energy;


        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Technology Heat Exchanger recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 2, 2, 1, 2);
            msg.add(time <= 0, "time must be greater than or equal to 1, yet it was {}", time);
            msg.add(energy < 0, "energy must be a non negative integer, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable HeatExchangerRecipe register() {
            if (!validate()) return null;
            HeatExchangerRecipe recipe = new HeatExchangerRecipe(fluidOutput.get(0), fluidOutput.getOrEmpty(1), fluidInput.get(0), fluidInput.get(1), energy, time);
            ModSupport.IMMERSIVE_TECHNOLOGY.get().heatExchanger.add(recipe);
            return recipe;
        }

    }

}
