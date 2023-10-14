package com.cleanroommc.groovyscript.core.mixin.inspirations;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = InspirationsRegistry.class, remap = false)
public interface InspirationsRegistryAccessor {

    @Accessor
    static Map<IBlockState, IBlockState> getAnvilSmashing() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Map<Block, IBlockState> getAnvilSmashingBlocks() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Set<Material> getAnvilBreaking() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static List<ICauldronRecipe> getCauldronRecipes() {
        throw new UnsupportedOperationException();
    }

}

