package com.cleanroommc.groovyscript.core.mixin.jei;

import com.cleanroommc.groovyscript.compat.mods.jei.JeiPlugin;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.recipes.RecipeRegistry;
import mezz.jei.startup.ModRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ModRegistry.class, remap = false)
public abstract class ModRegistryMixin {

    @Shadow
    @Final
    private List<IRecipeCategory<?>> recipeCategories;

    /**
     * @reason Sort recipe categories according to a list created and managed by GroovyScript
     * to allow a custom order for JEI category tabs.
     * @see JeiPlugin#getCategoryComparator()
     */
    @Inject(method = "createRecipeRegistry", at = @At("HEAD"))
    private void grs$sortRecipeCategories(CallbackInfoReturnable<RecipeRegistry> ci) {
        recipeCategories.sort(JeiPlugin.getCategoryComparator());
    }
}
