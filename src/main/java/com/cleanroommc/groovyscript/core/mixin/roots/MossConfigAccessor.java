package com.cleanroommc.groovyscript.core.mixin.roots;

import com.google.common.collect.BiMap;
import epicsquid.roots.config.MossConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = MossConfig.class, remap = false)
public interface MossConfigAccessor {

    @Accessor
    static Map<ItemStack, ItemStack> getMossyCobblestones() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static BiMap<Block, Block> getMossyBlocks() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static BiMap<IBlockState, IBlockState> getMossyStates() {
        throw new UnsupportedOperationException();
    }
}
