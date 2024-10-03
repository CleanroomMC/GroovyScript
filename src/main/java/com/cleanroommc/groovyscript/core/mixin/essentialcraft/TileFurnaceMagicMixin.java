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

    /**
     * @reason This mixin fixes a server crash when an invalid Magmatic Ore is put into the Magmatic Furnace's input slot.
     * Normally this causes an ArrayOutOfBoundsException when the recipe with index -1 (invalid input) is being read from the registry.
     * The indices of its slots are as follows: Bound Gem (0), Ore/Magmatic Alloy input (1), Magmatic Alloy/Resource output (2).
     */
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    public void onUpdate(CallbackInfo ci) {
        ItemStack alloy = this.getStackInSlot(1);
        if (alloy.getItem() == ItemsCore.magicalAlloy && OreSmeltingRecipe.getIndex(alloy) == -1) {
            this.smeltingLevel = 0;
            ci.cancel();
        }
    }

}
