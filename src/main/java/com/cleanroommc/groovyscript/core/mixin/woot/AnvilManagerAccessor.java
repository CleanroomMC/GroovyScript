package com.cleanroommc.groovyscript.core.mixin.woot;

import ipsis.woot.crafting.AnvilManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = AnvilManager.class, remap = false)
public interface AnvilManagerAccessor {

    @Accessor
    List<ItemStack> getValidBaseItems();
}
