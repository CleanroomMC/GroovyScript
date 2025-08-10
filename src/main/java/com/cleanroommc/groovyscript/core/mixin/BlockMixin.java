package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.compat.vanilla.BlockMixinExpansion;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public abstract class BlockMixin implements BlockMixinExpansion {

    @Shadow
    public abstract int getMetaFromState(IBlockState state);

    @Unique private int grs$fallingEnabled = 0;

    @Override
    public boolean grs$isFallingEnabled(IBlockState state) {
        return (this.grs$fallingEnabled & (1 << this.getMetaFromState(state))) != 0;
    }

    @Override
    public void grs$setFalling(IBlockState state, boolean falling) {
        if (falling) {
            this.grs$fallingEnabled |= (1 << this.getMetaFromState(state));
        } else {
            this.grs$fallingEnabled &= ~(1 << this.getMetaFromState(state));
        }
    }

}
