package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionAssembler;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription
public class PrecisionAssembler extends BaseRegistry {

    @RecipeBuilderDescription(
            example = @Example(".input(item('minecraft:fishing_rod'), item('minecraft:carrot')).output(item('minecraft:carrot_on_a_stick')).power(50).time(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TilePrecisionAssembler.class;
    }

    @MethodDescription(example = @Example("item('advancedrocketry:atmanalyser')"))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @MethodDescription(example = @Example("item('minecraft:redstone_block')"))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    public class RecipeBuilder extends BaseRegistry.RecipeBuilder {
        @Override
        public void validate(GroovyLog.Msg msg) {
            // Technically Precision Assembler can use 7 hatches,
            // but not really because 1 of them is power
            int hatchesNeeded = getHatchesNeeded();
            msg.add(hatchesNeeded > 6, "Precision Assembler only accepts 6 hatches, {} given", hatchesNeeded);
            msg.add(input.isEmpty() && fluidInput.isEmpty(), "Precision Assembler: No inputs provided!");
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "Precision Assembler: No outputs provided!");
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }

}
