package com.cleanroommc.groovyscript.core.mixin.primal_tech;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import primal_tech.recipes.WaterSawRecipes;

import java.util.List;

@Mixin(value = WaterSawRecipes.class, remap = false)
public interface WaterSawRecipesAccessor {

    @Accessor
    static List<WaterSawRecipes> getRecipes() {
        throw new UnsupportedOperationException();
    }

    @Invoker("<init>")
    static WaterSawRecipes createWaterSawRecipes(ItemStack output, ItemStack input, int choppingTime) {
        throw new UnsupportedOperationException();
    }

}
