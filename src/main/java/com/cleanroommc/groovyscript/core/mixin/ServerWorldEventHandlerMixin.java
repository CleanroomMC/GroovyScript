package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.compat.vanilla.BlockMixinExpansion;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldEventHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorldEventHandler.class)
public class ServerWorldEventHandlerMixin {

    @Inject(method = "notifyBlockUpdate", at = @At("RETURN"))
    private void onNotifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags, CallbackInfo ci) {
        if (((BlockMixinExpansion) newState.getBlock()).grs$isFallingEnabled(newState)) {
            world.scheduleUpdate(pos, newState.getBlock(), 2);
        }
    }

}
