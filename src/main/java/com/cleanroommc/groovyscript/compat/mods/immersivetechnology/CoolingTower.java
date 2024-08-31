package com.cleanroommc.groovyscript.compat.mods.immersivetechnology;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mctmods.immersivetechnology.api.crafting.CoolingTowerRecipe;
import mctmods.immersivetechnology.common.Config;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class CoolingTower extends VirtualizedRegistry<CoolingTowerRecipe> {

    @Override
    public boolean isEnabled() {
        return Config.ITConfig.Machines.Multiblock.enable_coolingTower;
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 100).fluidOutput(fluid('hot_spring_water') * 500).time(100)"),
            @Example(".fluidInput(fluid('water') * 50, fluid('hot_spring_water') * 50).fluidOutput(fluid('lava') * 50, fluid('water') * 50, fluid('lava') * 50).time(50)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        CoolingTowerRecipe.recipeList.removeAll(removeScripted());
        CoolingTowerRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(CoolingTowerRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            CoolingTowerRecipe.recipeList.add(recipe);
        }
    }

    public boolean remove(CoolingTowerRecipe recipe) {
        if (CoolingTowerRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("fluid('hot_spring_water')"))
    public void removeByInput(IIngredient input) {
        CoolingTowerRecipe.recipeList.removeIf(r -> {
            for (FluidStack fluidStack : r.getFluidInputs()) {
                if (input.test(fluidStack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('water')", commented = true))
    public void removeByOutput(IIngredient output) {
        CoolingTowerRecipe.recipeList.removeIf(r -> {
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
    public SimpleObjectStream<CoolingTowerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(CoolingTowerRecipe.recipeList).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        CoolingTowerRecipe.recipeList.forEach(this::addBackup);
        CoolingTowerRecipe.recipeList.clear();
    }

    @Property(property = "fluidInput", comp = @Comp(gte = 1, lte = 2))
    @Property(property = "fluidOutput", comp = @Comp(gte = 1, lte = 3))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CoolingTowerRecipe> {

        @Property(comp = @Comp(types = Comp.Type.GTE))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Technology Cooling Tower recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 2, 1, 3);
            msg.add(time <= 0, "time must be greater than or equal to 1, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CoolingTowerRecipe register() {
            if (!validate()) return null;
            CoolingTowerRecipe recipe = new CoolingTowerRecipe(fluidOutput.get(0), fluidOutput.getOrEmpty(1), fluidOutput.getOrEmpty(2), fluidInput.get(0), fluidInput.getOrEmpty(1), time);
            ModSupport.IMMERSIVE_TECHNOLOGY.get().coolingTower.add(recipe);
            return recipe;
        }
    }

}
