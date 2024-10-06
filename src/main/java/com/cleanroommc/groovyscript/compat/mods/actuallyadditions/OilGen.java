package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.OilGenRecipe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class OilGen extends StandardListRegistry<OilGenRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water')).amount(1000).time(50)"),
            @Example(".fluidInput(fluid('lava')).amount(50).time(100)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<OilGenRecipe> getRecipes() {
        return ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES;
    }

    public OilGenRecipe add(Fluid input, int amount, int time) {
        return add(input.getName(), amount, time);
    }

    public OilGenRecipe add(String input, int amount, int time) {
        OilGenRecipe recipe = new OilGenRecipe(input, amount, time);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("fluid('canolaoil')"))
    public boolean removeByInput(FluidStack fluid) {
        return this.removeByInput(fluid.getFluid());
    }

    @MethodDescription(example = @Example("fluid('canolaoil').getFluid()"))
    public boolean removeByInput(Fluid fluid) {
        return this.removeByInput(fluid.getName());
    }

    @MethodDescription(example = @Example("'refinedcanolaoil'"))
    public boolean removeByInput(String fluid) {
        return getRecipes().removeIf(recipe -> {
            boolean found = fluid.equals(recipe.fluidName);
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<OilGenRecipe> {

        @Property(comp = @Comp(gte = 0))
        private int amount;
        @Property(comp = @Comp(gte = 0))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Actually Additions Oil Gen recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(amount < 0, "amount must be a non negative integer, yet it was {}", amount);
            msg.add(time < 0, "time must be a non negative integer, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable OilGenRecipe register() {
            if (!validate()) return null;
            OilGenRecipe recipe = new OilGenRecipe(fluidInput.get(0).getFluid().getName(), amount, time);
            ModSupport.ACTUALLY_ADDITIONS.get().oilGen.add(recipe);
            return recipe;
        }

    }
}
