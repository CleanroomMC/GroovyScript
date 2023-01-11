package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkLevelManager;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin( value = PerkLevelManager.class , remap = false )
public abstract class PerkLevelManagerMixin {

    @Shadow
    private static int LEVEL_CAP;

    @Shadow
    private Map<Integer, Long> totalExpLevelRequired;

    @Inject( method = "ensureLevels()V" , at = @At("HEAD") , cancellable = true )
    public void ensureLevels(CallbackInfo ci) {
        if (this.totalExpLevelRequired.isEmpty()) {
            for(int i = 1; i <= LEVEL_CAP; ++i) {
                long prev = this.totalExpLevelRequired.getOrDefault(i - 1, 0L);
                if (ModSupport.ASTRAL_SORCERY.get().perkTreeConfig.xpFunction != null)
                    this.totalExpLevelRequired.put(i, ClosureHelper.call(ModSupport.ASTRAL_SORCERY.get().perkTreeConfig.xpFunction, i, prev));
                else
                    this.totalExpLevelRequired.put(i, prev + 150L + MathHelper.lfloor(Math.pow(2.0, i / 2.0F + 3)));
            }
        }
        ci.cancel();
    }

}