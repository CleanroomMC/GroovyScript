package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.compat.vanilla.BlockMixinExpansion;
import com.cleanroommc.groovyscript.compat.vanilla.Falling;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

@Mixin(WorldServer.class)
public class WorldServerMixin {

    @WrapOperation(method = { "updateBlockTick", "tickUpdates" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;updateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V"))
    private void onUpdateTick(Block block, World world, BlockPos pos, IBlockState state, Random rand, Operation<Void> original) {
        original.call(block, world, pos, state, rand);
        if (((BlockMixinExpansion) state.getBlock()).grs$isFallingEnabled(state)) {
            Falling.fall((World) (Object) this, pos);
        }
    }

}
