package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectricArcFurnace;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription(admonition = {
        @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.weights", type = Admonition.Type.WARNING),
        @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.input_slots", type = Admonition.Type.WARNING),
        @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.output_slots", type = Admonition.Type.WARNING),
        @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.hatch_count_11", type = Admonition.Type.INFO),
})
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

    @Override
    @MethodDescription(example = @Example("item('libvulpes:productingot', 3)"))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @Override
    @MethodDescription(example = @Example("item('minecraft:iron_ingot')"))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    @Property(property = "input", requirement = "groovyscript.wiki.advancedrocketry.input.required")
    @Property(property = "fluidInput", requirement = "groovyscript.wiki.advancedrocketry.input.required")
    @Property(property = "output", value = "groovyscript.wiki.advancedrocketry.output.value", requirement = "groovyscript.wiki.advancedrocketry.output.required")
    @Property(property = "fluidOutput", requirement = "groovyscript.wiki.advancedrocketry.output.required")
    public static class RecipeBuilder extends BaseRegistry.MultiblockRecipeBuilder {

        @Override
        protected BaseRegistry getRegistry() {
            return ModSupport.ADVANCED_ROCKETRY.get().arcFurnace;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            int hatchesNeeded = getHatchesNeeded();
            msg.add(hatchesNeeded > 11, "Arc Furnace only accepts 11 hatches, {} required for the recipe", hatchesNeeded);
            msg.add(input.isEmpty() && fluidInput.isEmpty(), "No inputs provided!");
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "No outputs provided!");
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
