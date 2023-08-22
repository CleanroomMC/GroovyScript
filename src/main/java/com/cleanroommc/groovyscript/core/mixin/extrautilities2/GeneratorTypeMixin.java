package com.cleanroommc.groovyscript.core.mixin.extrautilities2;

import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.rwtema.extrautils2.blocks.BlockPassiveGenerator;
import com.rwtema.extrautils2.tile.TilePassiveGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockPassiveGenerator.GeneratorType.class, targets = "com/rwtema/extrautils2/blocks/BlockPassiveGenerator$GeneratorType", remap = false)
public class GeneratorTypeMixin {

    @Shadow
    @Final
    ResourceLocation key;

    @Inject(method = "basePowerGen", at = @At("HEAD"), cancellable = true)
    public void basePowerGen(CallbackInfoReturnable<Float> cir) {
        Float value = ModSupport.EXTRA_UTILITIES_2.get().gridPowerPassiveGenerator.basePowerMap.get(key);
        if (value != null) {
            cir.setReturnValue(value);
        }
    }


    //@Inject(method = "getPowerLevel(Lcom/rwtema/extrautils2/tile/TilePassiveGenerator;Lnet/minecraft/world/World;)F", at = @At("HEAD"), cancellable = true)
    public void getPowerLevel(TilePassiveGenerator var1, World var2, CallbackInfoReturnable<Float> cir) {
        Float value = ModSupport.EXTRA_UTILITIES_2.get().gridPowerPassiveGenerator.basePowerMap.get(key);
        if (value != null) {
            cir.setReturnValue(value);
        }
    }

}
