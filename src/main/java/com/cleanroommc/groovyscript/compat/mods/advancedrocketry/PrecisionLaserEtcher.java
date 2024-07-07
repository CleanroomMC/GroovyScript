package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionLaserEtcher;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription
public class PrecisionLaserEtcher extends BaseRegistry {
    @RecipeBuilderDescription(
            example = @Example(".input(item('minecraft:blaze_powder') * 4, item('advancedrocketry:wafer')).output(item('advancedrocketry:itemcircuitplate')).power(50).time(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TilePrecisionLaserEtcher.class;
    }

    @MethodDescription(example = @Example("item('advancedrocketry:itemcircuitplate')"))
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
            validateItems(msg, 1, 4, 1, 4);
            validateFluids(msg);
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
