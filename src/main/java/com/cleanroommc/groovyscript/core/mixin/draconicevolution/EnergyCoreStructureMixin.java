package com.cleanroommc.groovyscript.core.mixin.draconicevolution;

import com.brandon3055.brandonscore.lib.MultiBlockStorage;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.brandon3055.draconicevolution.world.EnergyCoreStructure;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnergyCoreStructure.class, remap = false)
public abstract class EnergyCoreStructureMixin {

    @Shadow
    private MultiBlockStorage[] structureTiers;
    @Shadow
    private TileEnergyStorageCore core;

    @Shadow
    public abstract BlockPos getCoreOffset(int tier);

    @Unique
    private int groovyScript$version = 0;

    @Inject(method = "checkTier", at = @At("HEAD"), cancellable = true)
    public void checkTier(int tier, CallbackInfoReturnable<Boolean> cir) {
        // check if the structure was edited and update
        if (groovyScript$version != ModSupport.DRACONIC_EVOLUTION.get().energyCore.getVersion()) {
            groovyScript$version = ModSupport.DRACONIC_EVOLUTION.get().energyCore.getVersion();
            ModSupport.DRACONIC_EVOLUTION.get().energyCore.applyEdit(structureTiers);
        }
        // this part is the same as de
        // I just do this because theirs is ugly
        if (tier <= 0) {
            LogHelper.error("[EnergyCoreStructure] Tier value to small. As far as TileEnergyStorageCore is concerned the tiers now start at 1 not 0. This class automatically handles the conversion now");
            cir.setReturnValue(false);
            return;
        }
        if (tier > 8) {
            LogHelper.error("[EnergyCoreStructure#checkTeir] What exactly were you expecting after Tier 8? Infinity.MAX_VALUE?");
            cir.setReturnValue(false);
            return;
        }
        cir.setReturnValue(this.structureTiers[tier - 1].checkStructure(this.core.getWorld(), this.core.getPos().add(getCoreOffset(tier))));
    }

    @Inject(method = "forTier", at = @At("HEAD"))
    private void forTier(int tier, int flag, CallbackInfo ci) {
        // check if the structure was edited and update
        if (groovyScript$version != ModSupport.DRACONIC_EVOLUTION.get().energyCore.getVersion()) {
            groovyScript$version = ModSupport.DRACONIC_EVOLUTION.get().energyCore.getVersion();
            ModSupport.DRACONIC_EVOLUTION.get().energyCore.applyEdit(structureTiers);
        }
    }
}
