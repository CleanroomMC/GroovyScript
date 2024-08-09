package com.cleanroommc.groovyscript.compat.mods.immersivetechnology;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mctmods.immersivetechnology.api.crafting.ElectrolyticCrucibleBatteryRecipe;
import mctmods.immersivetechnology.common.Config;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class ElectrolyticCrucibleBattery extends VirtualizedRegistry<ElectrolyticCrucibleBatteryRecipe> {

    @Override
    public boolean isEnabled() {
        return Config.ITConfig.Machines.Multiblock.enable_electrolyticCrucibleBattery;
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 100).fluidOutput(fluid('hot_spring_water') * 500).output(item('minecraft:clay')).time(100)"),
            @Example(".fluidInput(fluid('water') * 500).fluidOutput(fluid('lava') * 50, fluid('hot_spring_water') * 50, fluid('water') * 400).output(item('minecraft:diamond')).time(50).energy(5000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        ElectrolyticCrucibleBatteryRecipe.recipeList.removeAll(removeScripted());
        ElectrolyticCrucibleBatteryRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(ElectrolyticCrucibleBatteryRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ElectrolyticCrucibleBatteryRecipe.recipeList.add(recipe);
        }
    }

    public boolean remove(ElectrolyticCrucibleBatteryRecipe recipe) {
        if (ElectrolyticCrucibleBatteryRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("fluid('moltensalt')"))
    public void removeByInput(IIngredient input) {
        ElectrolyticCrucibleBatteryRecipe.recipeList.removeIf(r -> {
            for (FluidStack fluidStack : r.getFluidInputs()) {
                if (input.test(fluidStack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('chlorine')", commented = true))
    public void removeByOutput(IIngredient output) {
        ElectrolyticCrucibleBatteryRecipe.recipeList.removeIf(r -> {
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
    public SimpleObjectStream<ElectrolyticCrucibleBatteryRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ElectrolyticCrucibleBatteryRecipe.recipeList).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ElectrolyticCrucibleBatteryRecipe.recipeList.forEach(this::addBackup);
        ElectrolyticCrucibleBatteryRecipe.recipeList.clear();
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "fluidOutput", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "3")})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ElectrolyticCrucibleBatteryRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int time;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
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
            return "Error adding Immersive Technology Electrolytic Crucible Battery recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg, 1, 1, 1, 3);
            msg.add(time <= 0, "time must be greater than or equal to 1, yet it was {}", time);
            msg.add(energy < 0, "energy must be a non negative integer, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ElectrolyticCrucibleBatteryRecipe register() {
            if (!validate()) return null;
            ElectrolyticCrucibleBatteryRecipe recipe = new ElectrolyticCrucibleBatteryRecipe(fluidOutput.get(0), fluidOutput.getOrEmpty(1), fluidOutput.getOrEmpty(2), output.get(0), fluidInput.get(0), energy, time);
            ModSupport.IMMERSIVE_TECHNOLOGY.get().electrolyticCrucibleBattery.add(recipe);
            return recipe;
        }
    }

}
