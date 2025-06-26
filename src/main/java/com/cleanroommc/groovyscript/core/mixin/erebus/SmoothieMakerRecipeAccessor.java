package com.cleanroommc.groovyscript.core.mixin.erebus;

import erebus.recipes.SmoothieMakerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(value = SmoothieMakerRecipe.class, remap = false)
public interface SmoothieMakerRecipeAccessor {

    @Invoker("<init>")
    static SmoothieMakerRecipe createSmoothieMakerRecipe(ItemStack output, ItemStack container, FluidStack[] fluids, Object... input) {
        throw new UnsupportedOperationException();
    }

    @Accessor("recipes")
    static List<SmoothieMakerRecipe> getRecipes() {
        throw new UnsupportedOperationException();
    }
}
