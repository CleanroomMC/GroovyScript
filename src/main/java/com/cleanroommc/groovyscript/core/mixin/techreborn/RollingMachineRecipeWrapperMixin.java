package com.cleanroommc.groovyscript.core.mixin.techreborn;

import com.cleanroommc.groovyscript.compat.mods.jei.ShapedRecipeWrapper;
import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.item.crafting.IRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import techreborn.compat.jei.rollingMachine.RollingMachineRecipeWrapper;

@Mixin(value = RollingMachineRecipeWrapper.class, remap = false)
public abstract class RollingMachineRecipeWrapperMixin {

    @Inject(method = "create", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private static void useCustomGroovyScriptRecipe(IJeiHelpers jeiHelpers, IRecipe baseRecipe, CallbackInfoReturnable<RollingMachineRecipeWrapper> cir) {
        if (baseRecipe instanceof ShapelessCraftingRecipe r) cir.setReturnValue(new RollingMachineRecipeWrapper(new ShapelessRecipeWrapper<>(jeiHelpers, r)));
        if (baseRecipe instanceof ShapedCraftingRecipe r) cir.setReturnValue(new RollingMachineRecipeWrapper(new ShapedRecipeWrapper(jeiHelpers, r)));
    }
}
