package com.cleanroommc.groovyscript.core.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.SlotCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SlotCrafting.class, remap = false)
public interface SlotCraftingAccess {

    @Accessor
    EntityPlayer getPlayer();
}
