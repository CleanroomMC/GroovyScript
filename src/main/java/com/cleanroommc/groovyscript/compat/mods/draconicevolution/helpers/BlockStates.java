package com.cleanroommc.groovyscript.compat.mods.draconicevolution.helpers;

import com.brandon3055.draconicevolution.DEFeatures;
import com.cleanroommc.groovyscript.GroovyScript;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

/**
 * Class allowing matching multiple block states.
 */
public class BlockStates {

    public static final BlockStates ANY = new BlockStates(null) {
        @Override
        public boolean matches(IBlockState state, boolean wildCardAir) {
            if (wildCardAir) return state.getBlock().equals(Blocks.AIR);
            return true;
        }

        @Override
        public ItemStack[] transformToStack() {
            return new ItemStack[0];
        }
    };

    private static BlockStates redstone, draconium, draconic;

    public static BlockStates redstone() {
        if (redstone == null) redstone = of(Blocks.REDSTONE_BLOCK);
        return redstone;
    }

    public static BlockStates draconium() {
        if (draconium == null) draconium = of(DEFeatures.draconiumBlock);
        return draconium;
    }

    public static BlockStates draconic() {
        if (draconic == null) draconic = of(DEFeatures.draconicBlock);
        return draconic;
    }

    private final IBlockState[] blockStates;

    private BlockStates(IBlockState[] blockStates) {
        if (blockStates != null && blockStates.length == 0) throw new IllegalArgumentException("BlockStates must have at least one value");
        this.blockStates = blockStates;
    }

    @SuppressWarnings("unused")
    public static BlockStates of(IBlockState... blockStates) {
        if (blockStates == null) return ANY;
        return new BlockStates(blockStates);
    }

    @SuppressWarnings("unused")
    public static BlockStates of(ItemStack... blockStates) {
        if (blockStates == null) return ANY;
        return new BlockStates(Arrays.stream(blockStates).map(BlockStates::transformStackToState).toArray(IBlockState[]::new));
    }

    @SuppressWarnings("unused")
    public static BlockStates of(Block... blockStates) {
        if (blockStates == null) return ANY;
        return new BlockStates(Arrays.stream(blockStates).map(Block::getDefaultState).toArray(IBlockState[]::new));
    }

    public IBlockState getDefault() {
        return blockStates[0];
    }

    public final boolean isWildcard() {
        return this == ANY;
    }

    /**
     * Returns an itemstack list, with the first item being the default, and then the substitutes.
     * Returns empty list if wildcard.
     */
    public ItemStack[] transformToStack() {
        var stacks = new ItemStack[blockStates.length]; // Substitues amount + default
        for (int i = 0; i < blockStates.length; i++)
            stacks[i] = transformStateToStack(blockStates[i]);
        return stacks;
    }

    public boolean matches(IBlockState state, boolean wildCardAir) {
        for (var substitute : blockStates) {
            if (statesEqual(substitute, state)) return true;
        }
        return false;
    }

    public static ItemStack transformStateToStack(IBlockState state) {
        return new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
    }

    @SuppressWarnings("deprecation")
    public static IBlockState transformStackToState(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock block) {
            return block.getBlock().getStateFromMeta(stack.getMetadata());
        }
        GroovyScript.LOGGER.throwing(new IllegalArgumentException("Stack's Item must extend ItemBlock!"));
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockStates states))
            return false;
        return (states.isWildcard() && this.isWildcard()) || statesEqual(states.getDefault(), this.getDefault());
    }

    public static boolean statesEqual(IBlockState a, IBlockState b) {
        return a.getBlock() == b.getBlock() && a.getBlock().getMetaFromState(a) == b.getBlock().getMetaFromState(b);
    }
}
