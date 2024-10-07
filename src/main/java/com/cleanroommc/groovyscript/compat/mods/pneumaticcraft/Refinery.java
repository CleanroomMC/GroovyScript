package com.cleanroommc.groovyscript.compat.mods.pneumaticcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import me.desht.pneumaticcraft.common.recipes.RefineryRecipe;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription
public class Refinery extends StandardListRegistry<RefineryRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water') * 1000).fluidOutput(fluid('lava') * 750, fluid('lava') * 250, fluid('lava') * 100, fluid('lava') * 50)"),
            @Example(".fluidInput(fluid('lava') * 100).fluidOutput(fluid('water') * 50, fluid('kerosene') * 25)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<RefineryRecipe> getRecipes() {
        return RefineryRecipe.recipes;
    }

    @MethodDescription(example = @Example("fluid('kerosene')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(entry -> {
            if (Arrays.stream(entry.outputs).anyMatch(output::test)) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('oil')", commented = true))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(entry -> {
            if (input.test(entry.input)) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(gte = 2, lte = 4))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RefineryRecipe> {

        @Property(defaultValue = "373")
        private int requiredTemperature = 373;

        @RecipeBuilderMethodDescription
        public RecipeBuilder requiredTemperature(int requiredTemperature) {
            this.requiredTemperature = requiredTemperature;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding PneumaticCraft Refinery recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 2, 4);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RefineryRecipe register() {
            if (!validate()) return null;
            RefineryRecipe recipe = new RefineryRecipe(requiredTemperature, fluidInput.get(0), fluidOutput.toArray(new FluidStack[0]));
            ModSupport.PNEUMATIC_CRAFT.get().refinery.add(recipe);
            return recipe;
        }
    }
}
