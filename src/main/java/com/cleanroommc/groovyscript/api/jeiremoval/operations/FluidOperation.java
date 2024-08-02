package com.cleanroommc.groovyscript.api.jeiremoval.operations;

import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FluidOperation extends IngredientSlotOperation<FluidStack> {

    public FluidOperation() {
        super(VanillaTypes.FLUID, true, (stack, all) -> GroovyScriptCodeConverter.getSingleFluidStack(stack, true, false));
    }

    /**
     * @return the base {@link FluidOperation}
     */
    public static FluidOperation defaultOperation() {
        return new FluidOperation();
    }

}
