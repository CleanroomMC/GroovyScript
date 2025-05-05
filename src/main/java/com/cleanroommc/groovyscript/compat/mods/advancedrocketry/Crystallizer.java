package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCrystallizer;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription(
        admonition = @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.weights", type = Admonition.Type.WARNING),
        override = @MethodOverride(method = {
                @MethodDescription(method = "removeByOutput", example = @Example("item('libvulpes:productgem')")),
                @MethodDescription(method = "removeByInput", example = @Example("item('libvulpes:productingot', 3)"))
        }))
public class Crystallizer extends BaseRegistry {

    @RecipeBuilderDescription(
            example = @Example(".input(item('minecraft:blaze_powder') * 4).output(item('minecraft:blaze_rod')).power(50).time(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TileCrystallizer.class;
    }

    @Property(property = "input", comp = @Comp(lte = 4, unique = "groovyscript.wiki.advancedrocketry.input.required"))
    @Property(property = "fluidInput", comp = @Comp(lte = 1, unique = "groovyscript.wiki.advancedrocketry.input.required"))
    @Property(property = "output", comp = @Comp(lte = 4, unique = "groovyscript.wiki.advancedrocketry.output.required"), value = "groovyscript.wiki.advancedrocketry.output.value")
    @Property(property = "fluidOutput", comp = @Comp(lte = 1, unique = "groovyscript.wiki.advancedrocketry.output.required"))
    public static class RecipeBuilder extends BaseRegistry.MultiblockRecipeBuilder {

        @Override
        protected BaseRegistry getRegistry() {
            return ModSupport.ADVANCED_ROCKETRY.get().crystallizer;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 4, 0, 4);
            validateFluids(msg, 0, 1, 0, 1);
            msg.add(input.isEmpty() && fluidInput.isEmpty(), "No inputs provided!");
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "No outputs provided!");
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
