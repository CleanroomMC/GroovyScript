package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Forge is stupid (again)
@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "getForgeRarity", at = @At("HEAD"), cancellable = true, remap = false)
    private void prioritzeGSRarities(ItemStack stack, CallbackInfoReturnable<IRarity> cir) {
        cir.setReturnValue(VanillaModule.rarity.check(stack));
    }

}
