package com.cleanroommc.groovyscript.core.mixin.betterwithaddons;

import betterwithaddons.handler.RotHandler;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RotHandler.RotInfo.class, remap = false)
public interface RotInfoAccessor {

    @Accessor("itemStack")
    ItemStack getItemStack();

    @Accessor("rottedStack")
    ItemStack getRottedStack();

    @Accessor("baseName")
    String getBaseName();

    @Accessor("spoilTime")
    long getSpoilTime();
}
