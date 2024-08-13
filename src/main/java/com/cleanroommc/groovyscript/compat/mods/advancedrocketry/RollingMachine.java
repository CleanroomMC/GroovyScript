package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileRollingMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.weights", type = Admonition.Type.WARNING))
public class RollingMachine extends BaseMultiblockRegistry {
    @RecipeBuilderDescription(
            example = @Example(".input(item('minecraft:snow'), fluid('water') * 300).output(item('minecraft:snow_layer') * 2).power(50).time(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TileRollingMachine.class;
    }

    @Override
    @MethodDescription(example = @Example("item('libvulpes:productsheet', 1)"))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @Override
    @MethodDescription(example = @Example("item('libvulpes:productplate')"))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    @Property(property = "input", valid = @Comp(type = Comp.Type.LTE, value = "4"), requirement = "groovyscript.wiki.advancedrocketry.input.required")
    @Property(property = "fluidInput", valid = @Comp(type = Comp.Type.LTE, value = "1"), requirement = "groovyscript.wiki.advancedrocketry.input.required")
    @Property(property = "output", valid = {@Comp(type = Comp.Type.LTE, value = "4"), @Comp(type = Comp.Type.GTE, value = "1")},
              value = "groovyscript.wiki.advancedrocketry.output.value")
    public static class RecipeBuilder extends BaseMultiblockRegistry.RecipeBuilder {

        @Override
        protected BaseMultiblockRegistry getRegistry() {
            return ModSupport.ADVANCED_ROCKETRY.get().rolling;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 4, 1, 4);
            validateFluids(msg, 0, 1, 0, 0);
            msg.add(input.isEmpty() && fluidInput.isEmpty(), "No inputs provided!");
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
