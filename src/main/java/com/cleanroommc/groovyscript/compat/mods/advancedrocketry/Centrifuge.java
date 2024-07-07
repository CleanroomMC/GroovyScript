package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCentrifuge;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription
public class Centrifuge extends BaseRegistry {
    @RecipeBuilderDescription(
            example = @Example(".fluidInput(fluid('lava') * 500)" +
                               ".output(item('minecraft:slime_ball'), 0.1f).output(item('minecraft:stone'), 0.9f)" +
                               ".fluidOutput(fluid('enrichedlava') * 500, 0.5f).power(50).time(100).outputSize(1)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TileCentrifuge.class;
    }

    @MethodDescription(example = @Example("item('minecraft:gold_nugget')"))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @MethodDescription(example = @Example(value = "fluid('enrichedlava')", commented = true))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    public class RecipeBuilder extends BaseRegistry.RecipeBuilder {
        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 0, 12);
            validateFluids(msg, 1, 1, 0, 4);
            msg.add(fluidOutput.isEmpty() && output.isEmpty(), "Centrifuge: no outputs provided!");
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
