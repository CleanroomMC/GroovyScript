package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileRollingMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription(
        admonition = @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.weights", type = Admonition.Type.WARNING),
        override = @MethodOverride(method = {
                @MethodDescription(method = "removeByOutput", example = @Example("item('libvulpes:productsheet', 1)")),
                @MethodDescription(method = "removeByInput", example = @Example("item('libvulpes:productplate')"))
        }))
public class RollingMachine extends BaseRegistry {

    @RecipeBuilderDescription(
            example = @Example(".input(item('minecraft:snow'), fluid('water') * 300).output(item('minecraft:snow_layer') * 2).power(50).time(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TileRollingMachine.class;
    }

    @Property(property = "input", comp = @Comp(lte = 4, unique = "groovyscript.wiki.advancedrocketry.input.required"))
    @Property(property = "fluidInput", comp = @Comp(lte = 1, unique = "groovyscript.wiki.advancedrocketry.input.required"))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 4), value = "groovyscript.wiki.advancedrocketry.output.value")
    public static class RecipeBuilder extends BaseRegistry.MultiblockRecipeBuilder {

        @Override
        protected BaseRegistry getRegistry() {
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
