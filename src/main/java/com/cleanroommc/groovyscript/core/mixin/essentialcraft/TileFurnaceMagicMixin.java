package com.cleanroommc.groovyscript.core.mixin.essentialcraft;

import essentialcraft.api.OreSmeltingRecipe;
import essentialcraft.common.item.ItemsCore;
import essentialcraft.common.tile.TileFurnaceMagic;
import essentialcraft.common.tile.TileMRUGeneric;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileFurnaceMagic.class, remap = false)
public abstract class TileFurnaceMagicMixin extends TileMRUGeneric {

    @Shadow
    public int smeltingLevel;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    public void onUpdate(CallbackInfo ci) {
        ItemStack alloy = this.getStackInSlot(1);
        if (alloy.getItem() == ItemsCore.magicalAlloy && OreSmeltingRecipe.getIndex(alloy) == -1) {
            this.smeltingLevel = 0;
            ci.cancel();
        }
    }
}
