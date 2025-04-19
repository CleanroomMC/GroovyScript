package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionLaserEtcher;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription(
        admonition = @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.weights", type = Admonition.Type.WARNING),
        override = @MethodOverride(method = {
                @MethodDescription(method = "removeByOutput", example = @Example("item('advancedrocketry:itemcircuitplate')")),
                @MethodDescription(method = "removeByInput", example = @Example("item('minecraft:redstone_block')"))
        }))
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

    @Property(property = "input", comp = @Comp(gte = 1, lte = 4))
    @Property(
            property = "output",
            comp = @Comp(gte = 1, lte = 4),
            value = "groovyscript.wiki.advancedrocketry.output.value")
    public static class RecipeBuilder extends BaseRegistry.MultiblockRecipeBuilder {

        @Override
        protected BaseRegistry getRegistry() {
            return ModSupport.ADVANCED_ROCKETRY.get().laserEtcher;
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
