package com.cleanroommc.groovyscript.core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = Minecraft.class)
public interface DefaultResourcePackAccessor {

    @Accessor("defaultResourcePacks")
    List<IResourcePack> get();

}
