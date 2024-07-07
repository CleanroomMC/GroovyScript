package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectricArcFurnace;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription
public class ElectricArcFurnace extends BaseRegistry {
    @RecipeBuilderDescription(
            example = @Example(".input(item('minecraft:blaze_powder') * 4).output(item('minecraft:blaze_rod')).power(50).time(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TileElectricArcFurnace.class;
    }

    @MethodDescription(example = @Example("item('libvulpes:productingot', 3)"))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ingot')"))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    public class RecipeBuilder extends BaseRegistry.RecipeBuilder {
        @Override
        public void validate(GroovyLog.Msg msg) {
            int hatchesNeeded = getHatchesNeeded();
            msg.add(hatchesNeeded > 11, "Arc Furnace only accepts 11 hatches, {} given", hatchesNeeded);
            msg.add(input.isEmpty() && fluidInput.isEmpty(), "Arc Furnace: No inputs provided!");
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "Arc Furnace: No outputs provided!");
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
