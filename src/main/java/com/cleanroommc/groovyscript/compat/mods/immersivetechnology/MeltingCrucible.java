package com.cleanroommc.groovyscript.compat.mods.immersivetechnology;

import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.immersiveengineering.ImmersiveEngineering;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import mctmods.immersivetechnology.api.crafting.MeltingCrucibleRecipe;
import mctmods.immersivetechnology.common.Config;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class MeltingCrucible extends StandardListRegistry<MeltingCrucibleRecipe> {

    @Override
    public boolean isEnabled() {
        return Config.ITConfig.Machines.Multiblock.enable_meltingCrucible;
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).fluidOutput(fluid('hot_spring_water')).time(100)"),
            @Example(".input(item('minecraft:clay') * 8).fluidOutput(fluid('lava') * 50).time(50).energy(5000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<MeltingCrucibleRecipe> getRecipes() {
        return MeltingCrucibleRecipe.recipeList;
    }

    @MethodDescription(example = @Example("item('minecraft:cobblestone')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> {
            for (IngredientStack ingredientStack : r.getItemInputs()) {
                if (ImmersiveEngineering.areIngredientsEquals(ingredientStack, input)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('moltensalt')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> {
            // would iterate through r.getFluidOutputs() as with the other IE compats, but they forgot to define it so its null.
            if (output.test(r.fluidOutput)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<MeltingCrucibleRecipe> {

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
            return "Error adding Immersive Technology Melting Crucible recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
            msg.add(time <= 0, "time must be greater than or equal to 1, yet it was {}", time);
            msg.add(energy < 0, "energy must be a non negative integer, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable MeltingCrucibleRecipe register() {
            if (!validate()) return null;
            MeltingCrucibleRecipe recipe = new MeltingCrucibleRecipe(fluidOutput.get(0), ImmersiveEngineering.toIngredientStack(input.get(0)), energy, time);
            ModSupport.IMMERSIVE_TECHNOLOGY.get().meltingCrucible.add(recipe);
            return recipe;
        }

    }

}
