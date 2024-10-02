package com.cleanroommc.groovyscript.compat.mods.immersivetechnology;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import mctmods.immersivetechnology.api.crafting.GasTurbineRecipe;
import mctmods.immersivetechnology.common.Config;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class GasTurbine extends StandardListRegistry<GasTurbineRecipe> {

    @Override
    public boolean isEnabled() {
        return Config.ITConfig.Machines.Multiblock.enable_gasTurbine;
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 100).fluidOutput(fluid('hot_spring_water') * 500).time(100)"),
            @Example(".fluidInput(fluid('water') * 50).fluidOutput(fluid('lava') * 50).time(50)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<GasTurbineRecipe> getRecipes() {
        return GasTurbineRecipe.recipeList;
    }

    @MethodDescription(example = @Example("fluid('biodiesel')"))
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

    @MethodDescription(example = @Example(value = "fluid('fluegas')",commented = true))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> {
            for (FluidStack fluidStack : r.getFluidOutputs()) {
                if (output.test(fluidStack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "fluidOutput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<GasTurbineRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Technology Gas Turbine recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 1, 1);
            msg.add(time <= 0, "time must be greater than or equal to 1, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable GasTurbineRecipe register() {
            if (!validate()) return null;
            GasTurbineRecipe recipe = new GasTurbineRecipe(fluidOutput.get(0), fluidInput.get(0), time);
            ModSupport.IMMERSIVE_TECHNOLOGY.get().gasTurbine.add(recipe);
            return recipe;
        }
    }

}
