package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectrolyser;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription
public class Electrolyser extends BaseRegistry {
    @RecipeBuilderDescription(
            example = @Example(".fluidInput(fluid('lava') * 10).fluidOutput(fluid('nitrogen') * 50).power(50).time(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TileElectrolyser.class;
    }

    @MethodDescription(example = @Example(value = "fluid('oxygen')", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @MethodDescription(example = @Example("fluid('water')"))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    public class RecipeBuilder extends BaseRegistry.RecipeBuilder {
        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 1, 2);
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
