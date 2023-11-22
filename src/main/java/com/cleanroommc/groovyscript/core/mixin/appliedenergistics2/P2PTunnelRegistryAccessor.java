package com.cleanroommc.groovyscript.core.mixin.appliedenergistics2;

import appeng.api.config.TunnelType;
import appeng.core.features.registries.P2PTunnelRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = P2PTunnelRegistry.class, remap = false)
public interface P2PTunnelRegistryAccessor {

    @Accessor
    Map<ItemStack, TunnelType> getTunnels();

    @Accessor
    Map<String, TunnelType> getModIdTunnels();

    @Accessor
    Map<Capability<?>, TunnelType> getCapTunnels();

}
