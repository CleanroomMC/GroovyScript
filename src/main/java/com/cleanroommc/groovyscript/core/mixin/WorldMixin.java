package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.compat.vanilla.BlockMixinExpansion;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow
    public abstract void scheduleUpdate(BlockPos pos, Block blockIn, int delay);

    @WrapOperation(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;neighborChanged(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"))
    private void onNeighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, Operation<Void> original) {
        original.call(state, world, pos, block, fromPos);
        if (((BlockMixinExpansion) state.getBlock()).grs$isFallingEnabled(state)) {
            this.scheduleUpdate(pos, state.getBlock(), 2);
        }
    }

}
