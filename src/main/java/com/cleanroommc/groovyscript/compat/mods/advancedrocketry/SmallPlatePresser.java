package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import net.minecraft.item.ItemBlock;
import zmaster587.advancedRocketry.block.BlockSmallPlatePress;

import java.util.Arrays;

@RegistryDescription
public class SmallPlatePresser extends BaseRegistry {

    @RecipeBuilderDescription(
            example = @Example(".input(item('minecraft:cobblestone')).output(item('minecraft:diamond'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    protected Class<?> getMachineClass() {
        return BlockSmallPlatePress.class;
    }

    @Override
    @MethodDescription(example = @Example("item('libvulpes:productplate', 2)"))
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }

    @Override
    @MethodDescription(example = @Example("item('minecraft:iron_block')"))
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    @Property(property = "input", comp = @Comp(eq = 1, unique = "groovyscript.wiki.advancedrocketry.input.block"))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends BaseRegistry.RecipeBuilder {

        @Override
        protected BaseRegistry getRegistry() {
            return ModSupport.ADVANCED_ROCKETRY.get().platePress;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            boolean allBlocks = Arrays.stream(input.get(0).getMatchingStacks()).allMatch(s -> s.getItem() instanceof ItemBlock);
            msg.add(!allBlocks, "All inputs to Small Plate Press recipes must be blocks!");
        }

    }

}
