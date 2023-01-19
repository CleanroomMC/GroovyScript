package com.cleanroommc.groovyscript.core.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin( value = MinecraftServer.class , remap = false )
public interface MinecraftServerAccessor {

    @Accessor
    public WorldServer[] getWorlds();

}
