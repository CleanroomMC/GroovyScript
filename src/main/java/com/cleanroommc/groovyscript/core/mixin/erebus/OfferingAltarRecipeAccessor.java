package com.cleanroommc.groovyscript.core.mixin.erebus;

import erebus.recipes.OfferingAltarRecipe;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(value = OfferingAltarRecipe.class, remap = false)
public interface OfferingAltarRecipeAccessor {

    @Invoker("<init>")
    static OfferingAltarRecipe createOfferingAltarRecipe(ItemStack output, Object... inputs) {
        throw new UnsupportedOperationException();
    }

    @Accessor("list")
    static List<OfferingAltarRecipe> getRecipes() {
        throw new UnsupportedOperationException();
    }
}
