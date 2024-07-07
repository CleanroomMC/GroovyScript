package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription
public class ChemicalReactor extends BaseRegistry {
    @RecipeBuilderDescription(
            example = @Example(".input(item('minecraft:chorus_fruit_popped')).fluidInput(fluid('lava') * 500)" +
                               ".output(item('minecraft:end_rod') * 4).fluidOutput(fluid('water') * 500).power(50).time(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TileChemicalReactor.class;
    }

    @MethodDescription(example = @Example("item('minecraft:leather_helmet')"))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @MethodDescription(example = @Example("item('minecraft:bone')"))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    public class RecipeBuilder extends BaseRegistry.RecipeBuilder {
        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 8, 0, 4);
            validateFluids(msg, 0, 2, 0, 1);
            msg.add(input.isEmpty() && fluidInput.isEmpty(), "Chemical Reactor: No inputs provided!");
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "Chemical Reactor: No outputs provided!");
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
