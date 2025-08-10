package com.cleanroommc.groovyscript.compat.vanilla;

import net.minecraft.block.state.IBlockState;

public interface BlockMixinExpansion {

    boolean grs$isFallingEnabled(IBlockState state);

    void grs$setFalling(IBlockState state, boolean falling);

}
