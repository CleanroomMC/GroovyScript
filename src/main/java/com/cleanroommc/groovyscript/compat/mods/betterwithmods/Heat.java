package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.betterwithmods.BWMHeatRegistryAccessor;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;

@RegistryDescription(
        admonition = @Admonition("groovyscript.wiki.betterwithmods.heat.note0")
)
public class Heat extends StandardListRegistry<BWMHeatRegistry.HeatSource> {

    @Override
    public Collection<BWMHeatRegistry.HeatSource> getRegistry() {
        return BWMHeatRegistryAccessor.getHEAT_SOURCES();
    }

    @MethodDescription(type = MethodDescription.Type.VALUE)
    public void add(int heat, BlockIngredient ingredient) {
        add(new BWMHeatRegistry.HeatSource(ingredient, heat));
    }

    @MethodDescription(type = MethodDescription.Type.VALUE, example = @Example("3, 'torch'"))
    public void add(int heat, String input) {
        add(heat, new BlockIngredient(input));
    }

    @MethodDescription(type = MethodDescription.Type.VALUE)
    public void add(int heat, List<ItemStack> input) {
        add(heat, new BlockIngredient(input));
    }

    @MethodDescription(type = MethodDescription.Type.VALUE, example = @Example("4, item('minecraft:redstone_block'), item('minecraft:redstone_torch')"))
    public void add(int heat, ItemStack... input) {
        add(heat, new BlockIngredient(input));
    }

    @MethodDescription(type = MethodDescription.Type.VALUE)
    public void add(int heat, IIngredient input) {
        add(heat, new BlockIngredient(input.toMcIngredient()));
    }
}
