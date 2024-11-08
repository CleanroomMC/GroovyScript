package com.cleanroommc.groovyscript.core.mixin.primal_tech;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import primal_tech.recipes.StoneAnvilRecipes;

import java.util.List;

@Mixin(value = StoneAnvilRecipes.class, remap = false)
public interface StoneAnvilRecipesAccessor {

    @Accessor
    static List<StoneAnvilRecipes> getRecipes() {
        throw new UnsupportedOperationException();
    }

    @Invoker("<init>")
    static StoneAnvilRecipes createStoneAnvilRecipes(ItemStack output, ItemStack input) {
        throw new UnsupportedOperationException();
    }
}
