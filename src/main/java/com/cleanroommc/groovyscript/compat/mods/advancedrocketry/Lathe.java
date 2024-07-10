package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileLathe;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.weights", type = Admonition.Type.WARNING))
public class Lathe extends BaseRegistry {
    @RecipeBuilderDescription(
            example = @Example(".input(ore('plankWood')).output(item('minecraft:stick') * 2).power(50).time(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TileLathe.class;
    }

    @MethodDescription(example = @Example("item('libvulpes:productrod', 4)"))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @MethodDescription(example = @Example("item('libvulpes:productingot', 6)"))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.LTE, value = "4"), @Comp(type = Comp.Type.GTE, value = "1")})
    @Property(property = "output", valid = {@Comp(type = Comp.Type.LTE, value = "4"), @Comp(type = Comp.Type.GTE, value = "1")},
              value = "groovyscript.wiki.advancedrocketry.output.value")
    public class RecipeBuilder extends BaseRegistry.RecipeBuilder {
        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 4, 1, 4);
            validateFluids(msg);
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
