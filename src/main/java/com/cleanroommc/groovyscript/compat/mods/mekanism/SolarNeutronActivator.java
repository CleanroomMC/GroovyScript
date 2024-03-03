package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.Alias;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.GasInput;
import mekanism.common.recipe.machines.SolarNeutronRecipe;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class SolarNeutronActivator extends VirtualizedMekanismRegistry<SolarNeutronRecipe> {

    public SolarNeutronActivator() {
        super(RecipeHandler.Recipe.SOLAR_NEUTRON_ACTIVATOR, Alias.generateOfClass(SolarNeutronActivator.class).and("SNA", "sna"));
    }

    @RecipeBuilderDescription(example = @Example(".gasInput(gas('water')).gasOutput(gas('hydrogen'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "gas('water'), gas('hydrogen')", commented = true))
    public SolarNeutronRecipe add(GasStack input, GasStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Solar Neutron Activator recipe").error();
        msg.add(Mekanism.isEmpty(input), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        SolarNeutronRecipe recipe = new SolarNeutronRecipe(input.copy(), output.copy());
        recipeRegistry.put(recipe);
        addScripted(recipe);
        return recipe;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("gas('lithium')"))
    public boolean removeByInput(GasStack input) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Solar Neutron Activator recipe").error();
        msg.add(Mekanism.isEmpty(input), () -> "input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        SolarNeutronRecipe recipe = recipeRegistry.get().remove(new GasInput(input));
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        removeError("could not find recipe for %", input);
        return false;
    }

    @Property(property = "gasInput", valid = @Comp("1"))
    @Property(property = "gasOutput", valid = @Comp("1"))
    public static class RecipeBuilder extends GasRecipeBuilder<SolarNeutronRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Solar Neutron Activator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg);
            validateGases(msg, 1, 1, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SolarNeutronRecipe register() {
            if (!validate()) return null;
            SolarNeutronRecipe recipe = new SolarNeutronRecipe(gasInput.get(0), gasOutput.get(0));
            ModSupport.MEKANISM.get().solarNeutronActivator.add(recipe);
            return recipe;
        }
    }
}
