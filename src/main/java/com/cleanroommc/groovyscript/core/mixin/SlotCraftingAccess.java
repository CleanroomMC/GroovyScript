package com.cleanroommc.groovyscript.core.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.SlotCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SlotCrafting.class)
public interface SlotCraftingAccess {

    @Accessor
    EntityPlayer getPlayer();
}
