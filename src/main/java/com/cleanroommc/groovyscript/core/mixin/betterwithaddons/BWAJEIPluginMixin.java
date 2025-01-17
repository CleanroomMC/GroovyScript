package com.cleanroommc.groovyscript.core.mixin.betterwithaddons;

import betterwithaddons.interaction.jei.BWAJEIPlugin;
import com.cleanroommc.groovyscript.compat.mods.jei.ShapedRecipeWrapper;
import com.cleanroommc.groovyscript.compat.vanilla.ShapedCraftingRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.ShapelessCraftingRecipe;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.item.crafting.IRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BWAJEIPlugin.class, remap = false)
public abstract class BWAJEIPluginMixin {

    /**
     * @reason This method only handles specific JEI Recipe Wrappers. This means that if there is an unknown IRecipe
     * it cannot be handled. Thus, if the recipe is not a Vanilla, Forge, or CraftTweaker, it will return null
     * and not display anything.
     * <p>
     * Furthermore, due to the reference to CraftTweaker recipes,
     * if CraftTweaker (an optional dependancy) is not installed,
     * calling this method will generate a {@link ClassNotFoundException}.
     * <p>
     * To resolve this we need to Inject before they are referenced,
     * and the HEAD is the most logical place for it.
     * @author WaitingIdly
     */
    @Inject(method = "getCraftingRecipeWrapper", at = @At(value = "HEAD", ordinal = 0), cancellable = true)
    private void useCustomGroovyScriptRecipe(IJeiHelpers jeiHelpers, IRecipe baseRecipe, CallbackInfoReturnable<IRecipeWrapper> cir) {
        if (baseRecipe instanceof ShapelessCraftingRecipe r) cir.setReturnValue(new ShapelessRecipeWrapper<>(jeiHelpers, r));
        if (baseRecipe instanceof ShapedCraftingRecipe r) cir.setReturnValue(new ShapedRecipeWrapper(jeiHelpers, r));
    }
}
