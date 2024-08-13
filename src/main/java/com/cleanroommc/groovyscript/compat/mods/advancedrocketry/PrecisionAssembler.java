package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionAssembler;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription(admonition = {
        @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.weights", type = Admonition.Type.WARNING),
        @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.input_slots", type = Admonition.Type.WARNING),
        @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.output_slots", type = Admonition.Type.WARNING),
        @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.hatch_count_6", type = Admonition.Type.INFO),
})
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

    @Override
    @MethodDescription(example = @Example("item('advancedrocketry:atmanalyser')"))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @Override
    @MethodDescription(example = @Example("item('minecraft:redstone_block')"))
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
            return ModSupport.ADVANCED_ROCKETRY.get().assembler;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            // Technically Precision Assembler can use 7 hatches,
            // but not really because 1 of them is power
            int hatchesNeeded = getHatchesNeeded();
            msg.add(hatchesNeeded > 6, "Precision Assembler only accepts 6 hatches, {} required for the recipe", hatchesNeeded);
            msg.add(input.isEmpty() && fluidInput.isEmpty(), "No inputs provided!");
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "No outputs provided!");
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }

}
