package com.cleanroommc.groovyscript.core.mixin.loot;

import net.minecraft.world.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootTable.class)
public abstract class LootTableMixin {

    // dirty loot crash fix
    @Inject(method = "freeze()V", at = @At("HEAD"), cancellable = true, remap = false)
    public void freeze(CallbackInfo ci) {
        ci.cancel();
    }
}
