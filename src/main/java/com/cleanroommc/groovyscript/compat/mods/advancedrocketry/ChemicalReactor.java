package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription(admonition = {
        @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.weights", type = Admonition.Type.WARNING),
        @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.input_slots", type = Admonition.Type.WARNING),
})
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

    @Override
    @MethodDescription(example = @Example("item('minecraft:leather_helmet')"))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @Override
    @MethodDescription(example = @Example("item('minecraft:bone')"))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    @Property(property = "input", valid = @Comp(type = Comp.Type.LTE, value = "8"), requirement = "groovyscript.wiki.advancedrocketry.input.required")
    @Property(property = "fluidInput", valid = @Comp(type = Comp.Type.LTE, value = "2"), requirement = "groovyscript.wiki.advancedrocketry.input.required")
    @Property(property = "output", valid = {@Comp(type = Comp.Type.LTE, value = "4")}, value = "groovyscript.wiki.advancedrocketry.output.value", requirement = "groovyscript.wiki.advancedrocketry.output.required")
    @Property(property = "fluidOutput", valid = {@Comp(type = Comp.Type.LTE, value = "1")}, requirement = "groovyscript.wiki.advancedrocketry.output.required")
    public static class RecipeBuilder extends BaseRegistry.MultiblockRecipeBuilder {

        @Override
        protected BaseRegistry getRegistry() {
            return ModSupport.ADVANCED_ROCKETRY.get().reactor;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 8, 0, 4);
            validateFluids(msg, 0, 2, 0, 1);
            msg.add(input.isEmpty() && fluidInput.isEmpty(), "No inputs provided!");
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "No outputs provided!");
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
