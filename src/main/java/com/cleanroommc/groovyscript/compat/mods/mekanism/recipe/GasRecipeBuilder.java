package com.cleanroommc.groovyscript.compat.mods.mekanism.recipe;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import mekanism.api.gas.GasStack;

import java.util.Collection;

public abstract class GasRecipeBuilder<T> extends AbstractRecipeBuilder<T> {

    @Property(value = "groovyscript.wiki.mekanism.gasInput.value", needsOverride = true, priority = 300, hierarchy = 20)
    protected final GasStackList gasInput = new GasStackList();
    @Property(value = "groovyscript.wiki.mekanism.gasOutput.value", needsOverride = true, priority = 800, hierarchy = 20)
    protected final GasStackList gasOutput = new GasStackList();

    @RecipeBuilderMethodDescription
    public GasRecipeBuilder<T> gasInput(GasStack gas) {
        this.gasInput.add(gas);
        return this;
    }

    @RecipeBuilderMethodDescription
    public GasRecipeBuilder<T> gasInput(Collection<GasStack> gases) {
        for (GasStack gas : gases) {
            gasInput(gas);
        }
        return this;
    }

    @RecipeBuilderMethodDescription
    public GasRecipeBuilder<T> gasInput(GasStack... gases) {
        for (GasStack gas : gases) {
            gasInput(gas);
        }
        return this;
    }

    @RecipeBuilderMethodDescription
    public GasRecipeBuilder<T> gasOutput(GasStack gas) {
        this.gasOutput.add(gas);
        return this;
    }

    @RecipeBuilderMethodDescription
    public GasRecipeBuilder<T> gasOutput(Collection<GasStack> gases) {
        for (GasStack gas : gases) {
            gasOutput(gas);
        }
        return this;
    }

    @RecipeBuilderMethodDescription
    public GasRecipeBuilder<T> gasOutput(GasStack... gases) {
        for (GasStack gas : gases) {
            gasOutput(gas);
        }
        return this;
    }


    public void validateGases(GroovyLog.Msg msg, int minInput, int maxInput, int minOutput, int maxOutput) {
        gasInput.trim();
        gasOutput.trim();
        validateCustom(msg, gasInput, minInput, maxInput, "gas input");
        validateCustom(msg, gasOutput, minOutput, maxOutput, "gas output");
    }

    public void validateGases(GroovyLog.Msg msg) {
        validateGases(msg, 0, 0, 0, 0);
    }

}
