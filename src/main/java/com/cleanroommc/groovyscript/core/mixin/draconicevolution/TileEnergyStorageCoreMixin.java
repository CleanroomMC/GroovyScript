package com.cleanroommc.groovyscript.core.mixin.draconicevolution;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.compat.mods.draconicevolution.helpers.BlockStateEnergyCoreStructure;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEnergyStorageCore.class, remap = false)
public abstract class TileEnergyStorageCoreMixin {

    @Shadow
    private int ticksElapsed;

    @Shadow
    public abstract boolean validateStructure();

    @Inject(method = "update", at = @At("HEAD"))
    public void update(CallbackInfo ci) {
        if (!GroovyScriptConfig.compat.draconicEvolutionEnergyCore) return;
        TileEnergyStorageCore tile = (TileEnergyStorageCore) (Object) this;
        if (!tile.getWorld().isRemote && ticksElapsed % 500 != 0 && ((BlockStateEnergyCoreStructure) tile.coreStructure).checkVersion()) {
            validateStructure();
        }
    }

    @Inject(method = "validateStructure", at = @At("HEAD"), cancellable = true)
    public void validateStructure(CallbackInfoReturnable<Boolean> cir) {
        if (!GroovyScriptConfig.compat.draconicEvolutionEnergyCore) return;
        TileEnergyStorageCore tile = (TileEnergyStorageCore) (Object) this;
        boolean valid = tile.checkStabilizers();
        var helper = ((BlockStateEnergyCoreStructure) tile.coreStructure).getHelper();
        if (!(tile.coreValid.value = tile.coreStructure.checkTier(tile.tier.value))) {
            BlockPos pos = helper.invalidBlock;
            tile.invalidMessage.value = "Error At: x:" + pos.getX() + ", y:" + pos.getY() + ", z:" + pos.getZ() +
                                        " Expected: " + helper.expectedBlockState.getBlock().getRegistryName() + ":" +
                                        helper.expectedBlockState.getBlock().getMetaFromState(helper.expectedBlockState);
            valid = false;
        }

        if (!valid && tile.active.value) {
            tile.active.value = false;
            tile.deactivateCore();
        }

        tile.structureValid.value = valid;
        if (valid) {
            tile.invalidMessage.value = "";
        }
        cir.setReturnValue(valid);
    }
}
