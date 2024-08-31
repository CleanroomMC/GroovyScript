package com.cleanroommc.groovyscript.compat.mods.immersivetechnology;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mctmods.immersivetechnology.api.crafting.HighPressureSteamTurbineRecipe;
import mctmods.immersivetechnology.common.Config;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class HighPressureSteamTurbine extends VirtualizedRegistry<HighPressureSteamTurbineRecipe> {

    @Override
    public boolean isEnabled() {
        return Config.ITConfig.Machines.Multiblock.enable_highPressureSteamTurbine;
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 100).fluidOutput(fluid('hot_spring_water') * 500).time(100)"),
            @Example(".fluidInput(fluid('water') * 50).fluidOutput(fluid('lava') * 50).time(50)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        HighPressureSteamTurbineRecipe.recipeList.removeAll(removeScripted());
        HighPressureSteamTurbineRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(HighPressureSteamTurbineRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            HighPressureSteamTurbineRecipe.recipeList.add(recipe);
        }
    }

    public boolean remove(HighPressureSteamTurbineRecipe recipe) {
        if (HighPressureSteamTurbineRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("fluid('highpressuresteam')"))
    public void removeByInput(IIngredient input) {
        HighPressureSteamTurbineRecipe.recipeList.removeIf(r -> {
            for (FluidStack fluidStack : r.getFluidInputs()) {
                if (input.test(fluidStack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('steam')",commented = true))
    public void removeByOutput(IIngredient output) {
        HighPressureSteamTurbineRecipe.recipeList.removeIf(r -> {
            for (FluidStack fluidStack : r.getFluidOutputs()) {
                if (output.test(fluidStack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<HighPressureSteamTurbineRecipe> streamRecipes() {
        return new SimpleObjectStream<>(HighPressureSteamTurbineRecipe.recipeList).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        HighPressureSteamTurbineRecipe.recipeList.forEach(this::addBackup);
        HighPressureSteamTurbineRecipe.recipeList.clear();
    }

    @Property(property = "fluidInput", comp = @Comp(types = Comp.Type.EQ, eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(types = Comp.Type.EQ, eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<HighPressureSteamTurbineRecipe> {

        @Property(comp = @Comp(types = Comp.Type.GTE))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Technology High Pressure Steam Turbine recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 1, 1);
            msg.add(time <= 0, "time must be greater than or equal to 1, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable HighPressureSteamTurbineRecipe register() {
            if (!validate()) return null;
            HighPressureSteamTurbineRecipe recipe = new HighPressureSteamTurbineRecipe(fluidOutput.get(0), fluidInput.get(0), time);
            ModSupport.IMMERSIVE_TECHNOLOGY.get().highPressureSteamTurbine.add(recipe);
            return recipe;
        }
    }

}
