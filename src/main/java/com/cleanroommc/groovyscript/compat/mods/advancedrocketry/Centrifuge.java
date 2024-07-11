package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCentrifuge;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription(admonition = {
    @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.weights", type = Admonition.Type.WARNING),
    @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.output_slots", type = Admonition.Type.WARNING),
})
public class Centrifuge extends BaseRegistry {
    @RecipeBuilderDescription(
            example = @Example(".fluidInput(fluid('lava') * 500)" +
                               ".output(item('minecraft:slime_ball'), 0.1f).output(item('minecraft:stone'), 0.9f)" +
                               ".fluidOutput(fluid('enrichedlava') * 500).power(50).time(100).outputSize(1)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TileCentrifuge.class;
    }

    @MethodDescription(example = @Example(value = "fluid('enrichedlava')"))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(type = Comp.Type.LTE, value = "12")}, value = "groovyscript.wiki.advancedrocketry.output.value")
    @Property(property = "fluidOutput", valid = {@Comp(type = Comp.Type.LTE, value = "4")})
    public static class RecipeBuilder extends BaseRegistry.RecipeBuilder {

        @Override
        protected BaseRegistry getRegistry() {
            return ModSupport.ADVANCED_ROCKETRY.get().centrifuge;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 0, 12);
            validateFluids(msg, 1, 1, 0, 4);
            msg.add(fluidOutput.isEmpty() && output.isEmpty(), "No outputs provided!");
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
