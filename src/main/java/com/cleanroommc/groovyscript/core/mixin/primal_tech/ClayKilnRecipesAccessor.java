package com.cleanroommc.groovyscript.core.mixin.primal_tech;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import primal_tech.recipes.ClayKilnRecipes;

import java.util.List;

@Mixin(value = ClayKilnRecipes.class, remap = false)
public interface ClayKilnRecipesAccessor {

    @Accessor
    static List<ClayKilnRecipes> getRecipes() {
        throw new UnsupportedOperationException();
    }

    @Invoker("<init>")
    static ClayKilnRecipes createClayKilnRecipes(ItemStack output, ItemStack input, int itemCookTime) {
        throw new UnsupportedOperationException();
    }

}
