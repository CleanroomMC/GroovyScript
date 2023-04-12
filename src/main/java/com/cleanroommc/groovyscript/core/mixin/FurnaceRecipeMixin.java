package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.compat.vanilla.FurnaceRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FurnaceRecipes.class)
public abstract class FurnaceRecipeMixin {

    @Inject(method = "addSmeltingRecipe", at = @At("RETURN"))
    public void addRecipe(ItemStack input, ItemStack stack, float experience, CallbackInfo ci) {
        FurnaceRecipeManager.inputMap.add(input);
    }
}
