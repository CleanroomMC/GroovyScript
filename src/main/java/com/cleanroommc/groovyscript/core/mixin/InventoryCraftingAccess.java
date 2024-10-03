package com.cleanroommc.groovyscript.core.mixin;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = InventoryCrafting.class)
public interface InventoryCraftingAccess {

    @Accessor
    Container getEventHandler();

}
