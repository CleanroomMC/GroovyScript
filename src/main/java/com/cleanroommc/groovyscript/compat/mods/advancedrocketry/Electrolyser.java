package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectrolyser;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.advancedrocketry.admonition.weights", type = Admonition.Type.WARNING))
public class Electrolyser extends BaseRegistry {
    @RecipeBuilderDescription(
            example = @Example(".fluidInput(fluid('lava') * 10).fluidOutput(fluid('nitrogen') * 50).power(50).time(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<? extends TileMultiblockMachine> getMachineClass() {
        return TileElectrolyser.class;
    }

    @Override
    @MethodDescription(example = @Example(value = "fluid('oxygen')", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @Override
    @MethodDescription(example = @Example("fluid('water')"))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "fluidOutput", valid = {@Comp(type = Comp.Type.LTE, value = "2"), @Comp(type = Comp.Type.GTE, value = "1")})
    public static class RecipeBuilder extends BaseRegistry.MultiblockRecipeBuilder {

        @Override
        protected BaseRegistry getRegistry() {
            return ModSupport.ADVANCED_ROCKETRY.get().electrolyser;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 1, 2);
            msg.add(power < 1, "Power must be 1 or greater, got {}", power);
            msg.add(time < 1, "Time must be 1 or greater, got {}", time);
        }
    }
}
