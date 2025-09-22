package com.cleanroommc.groovyscript.core.mixin.pyrotech;

import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.BloomeryRecipeBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BloomeryRecipeBase.class, remap = false)
public interface BloomeryRecipeBaseAccessor {

    @Accessor("outputBloom")
    ItemStack grs$getOutputBloom();

    @Accessor("outputBloom")
    void grs$setOutputBloom(ItemStack bloom);
}
