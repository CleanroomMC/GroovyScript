package com.cleanroommc.groovyscript.core.mixin.extrautilities2;

import com.rwtema.extrautils2.tile.TilePassiveGenerator;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.rwtema.extrautils2.blocks.BlockPassiveGenerator$GeneratorType$9", remap = false)
public class DragonEggMill {

    @Inject(method = "getPowerLevel(Lcom/rwtema/extrautils2/tile/TilePassiveGenerator;Lnet/minecraft/world/World;)F", at = @At("HEAD"), cancellable = true)
    public void getPowerLevel(TilePassiveGenerator var1, World var2, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(10000F);
    }

}
