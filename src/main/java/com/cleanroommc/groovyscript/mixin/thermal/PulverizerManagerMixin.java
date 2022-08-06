package com.cleanroommc.groovyscript.mixin.thermal;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import com.cleanroommc.groovyscript.compat.thermal.Pulverizer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PulverizerManager.class, remap = false)
public abstract class PulverizerManagerMixin {

    @Shadow
    public static ComparableItemStackValidated convertInput(ItemStack stack) {
        return null;
    }

    @Inject(method = "addRecipe(ILnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;I)Lcofh/thermalexpansion/util/managers/machine/PulverizerManager$PulverizerRecipe;", at = @At("RETURN"))
    private static void addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, CallbackInfoReturnable<PulverizerManager.PulverizerRecipe> cir) {
        PulverizerManager.PulverizerRecipe recipe = cir.getReturnValue();
        if (recipe != null) {
            Pulverizer.addRecipe(convertInput(input), recipe);
        }
    }
}
