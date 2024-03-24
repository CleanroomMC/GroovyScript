package com.cleanroommc.groovyscript.core.mixin.draconicevolution;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.compat.mods.draconicevolution.helpers.TileEnergyCoreStabilizerLogic;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEnergyCoreStabilizer.class, remap = false)
public abstract class TileEnergyCoreStabilizerMixin {

    @Inject(method = "getBlocksForFrameMove", at = @At("HEAD"), cancellable = true)
    private void getBlocksForFrameMove(CallbackInfoReturnable<Iterable<BlockPos>> cir) {
        if (GroovyScriptConfig.compat.draconicEvolutionEnergyCore) {
            cir.setReturnValue(TileEnergyCoreStabilizerLogic.getBlocksForFrameMove((TileEnergyCoreStabilizer) (Object) this));
        }
    }
}
