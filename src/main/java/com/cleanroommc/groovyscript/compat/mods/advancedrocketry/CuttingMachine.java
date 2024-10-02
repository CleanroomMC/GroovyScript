package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCuttingMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.weights", type = Admonition.Type.WARNING))
public class CuttingMachine extends BaseRegistry {
    @RecipeBuilderDescription(
            example = @Example(".input(item('minecraft:blaze_rod')).output(item('minecraft:blaze_powder') * 4).power(50).time(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TileCuttingMachine.class;
    }

    @Override
    @MethodDescription(example = @Example("item('minecraft:planks', 1)"))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @Override
    @MethodDescription(example = @Example("item('advancedrocketry:alienwood')"))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.LTE, value = "4"), @Comp(type = Comp.Type.GTE, value = "1")})
    @Property(property = "output", valid = {@Comp(type = Comp.Type.LTE, value = "4"), @Comp(type = Comp.Type.GTE, value = "1")},
              value = "groovyscript.wiki.advancedrocketry.output.value")
    public static class RecipeBuilder extends BaseRegistry.MultiblockRecipeBuilder {

        @Override
        protected BaseRegistry getRegistry() {
            return ModSupport.ADVANCED_ROCKETRY.get().cutting;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 4, 1, 4);
            validateFluids(msg);
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
