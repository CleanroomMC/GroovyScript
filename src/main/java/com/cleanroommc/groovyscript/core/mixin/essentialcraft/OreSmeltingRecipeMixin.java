package com.cleanroommc.groovyscript.core.mixin.essentialcraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import essentialcraft.api.OreSmeltingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = OreSmeltingRecipe.class, remap = false)
public abstract class OreSmeltingRecipeMixin {

    @WrapOperation(method = "getColorFromItemStack", at = @At(value = "FIELD", target = "Lessentialcraft/api/OreSmeltingRecipe;color:I"))
    private static int getColorFromItemStack(OreSmeltingRecipe recipe, Operation<Integer> original) {
        if (recipe == null) return 16777215;
        return original.call(recipe);
    }

    @WrapOperation(method = "getLocalizedOreName", at = @At(value = "FIELD", target = "Lessentialcraft/api/OreSmeltingRecipe;oreName:Ljava/lang/String;"))
    private static String getLocalizedOreName(OreSmeltingRecipe recipe, Operation<String> original) {
        if (recipe == null) return "";
        return original.call(recipe);
    }

}
