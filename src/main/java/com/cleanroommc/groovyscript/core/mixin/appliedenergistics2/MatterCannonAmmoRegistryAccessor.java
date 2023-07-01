package com.cleanroommc.groovyscript.core.mixin.appliedenergistics2;

import appeng.core.features.registries.MatterCannonAmmoRegistry;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(value = MatterCannonAmmoRegistry.class, remap = false)
public interface MatterCannonAmmoRegistryAccessor {

    @Accessor("DamageModifiers")
    HashMap<ItemStack, Double> getDamageModifiers();

}
