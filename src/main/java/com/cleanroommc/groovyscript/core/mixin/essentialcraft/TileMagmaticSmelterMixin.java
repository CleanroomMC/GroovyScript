package com.cleanroommc.groovyscript.core.mixin.essentialcraft;

import essentialcraft.api.OreSmeltingRecipe;
import essentialcraft.common.item.ItemsCore;
import essentialcraft.common.tile.TileMRUGeneric;
import essentialcraft.common.tile.TileMagmaticSmelter;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileMagmaticSmelter.class, remap = false)
public abstract class TileMagmaticSmelterMixin extends TileMRUGeneric {

    @Shadow
    public int smeltingLevel;

    /**
     * @reason This mixin fixes a server crash when an invalid Magmatic Ore is put into the Magmatic Smeltery's input slot.
     * Normally this causes an ArrayOutOfBoundsException when the recipe with index -1 (invalid input) is being read from the registry.
     * The indices of its slots are as follows: Bound Gem (0), Lava Bucket (1), Empty Bucket output (2),
     * Ore input (3), Magmatic Alloy output (4), Magmatic Alloy input (5), Resource output (6), Magical Slag (7).
     */
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    public void onUpdate(CallbackInfo ci) {
        ItemStack ore = this.getStackInSlot(3);
        ItemStack alloy = this.getStackInSlot(5);
        if (ore.isEmpty() && alloy.getItem() == ItemsCore.magicalAlloy && OreSmeltingRecipe.getIndex(alloy) == -1) {
            this.smeltingLevel = 0;
            ci.cancel();
        }
    }
}
