package com.cleanroommc.groovyscript.compat.mods.immersivetechnology;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mctmods.immersivetechnology.api.crafting.BoilerRecipe;
import mctmods.immersivetechnology.common.Config;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class BoilerFuel extends VirtualizedRegistry<BoilerRecipe.BoilerFuelRecipe> {

    @Override
    public boolean isEnabled() {
        return Config.ITConfig.Machines.Multiblock.enable_boiler;
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 100).time(100).heat(10)"),
            @Example(".fluidInput(fluid('water') * 50).time(50).heat(0.05)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        BoilerRecipe.fuelList.removeAll(removeScripted());
        BoilerRecipe.fuelList.addAll(restoreFromBackup());
    }

    public void add(BoilerRecipe.BoilerFuelRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            BoilerRecipe.fuelList.add(recipe);
        }
    }

    public boolean remove(BoilerRecipe.BoilerFuelRecipe recipe) {
        if (BoilerRecipe.fuelList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("fluid('biodiesel')"))
    public void removeByInput(IIngredient input) {
        BoilerRecipe.fuelList.removeIf(r -> {
            for (FluidStack fluidStack : r.getFluidInputs()) {
                if (input.test(fluidStack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<BoilerRecipe.BoilerFuelRecipe> streamRecipes() {
        return new SimpleObjectStream<>(BoilerRecipe.fuelList).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BoilerRecipe.fuelList.forEach(this::addBackup);
        BoilerRecipe.fuelList.clear();
    }

    @Property(property = "fluidInput", comp = @Comp(types = Comp.Type.EQ, eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<BoilerRecipe.BoilerFuelRecipe> {

        @Property(comp = @Comp(types = Comp.Type.GTE))
        private int time;
        @Property(comp = @Comp(types = Comp.Type.GTE))
        private double heat;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder heat(double heat) {
            this.heat = heat;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Technology Boiler Fuel entry";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(time <= 0, "time must be greater than or equal to 1, yet it was {}", time);
            msg.add(heat <= 0, "heat must be greater than or equal to 1, yet it was {}", heat);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable BoilerRecipe.BoilerFuelRecipe register() {
            if (!validate()) return null;
            BoilerRecipe.BoilerFuelRecipe recipe = new BoilerRecipe.BoilerFuelRecipe(fluidInput.get(0), time, heat);
            ModSupport.IMMERSIVE_TECHNOLOGY.get().boilerFuel.add(recipe);
            return recipe;
        }

    }

}
