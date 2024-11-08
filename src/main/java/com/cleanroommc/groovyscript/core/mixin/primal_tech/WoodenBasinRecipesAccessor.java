package com.cleanroommc.groovyscript.core.mixin.primal_tech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import primal_tech.recipes.WoodenBasinRecipes;

import java.util.List;

@Mixin(value = WoodenBasinRecipes.class, remap = false)
public interface WoodenBasinRecipesAccessor {

    @Accessor
    static List<WoodenBasinRecipes> getRecipes() {
        throw new UnsupportedOperationException();
    }

    @Invoker("<init>")
    static WoodenBasinRecipes createWoodenBasinRecipes(ItemStack output, FluidStack fluidIn, Object... input) {
        throw new UnsupportedOperationException();
    }
}
