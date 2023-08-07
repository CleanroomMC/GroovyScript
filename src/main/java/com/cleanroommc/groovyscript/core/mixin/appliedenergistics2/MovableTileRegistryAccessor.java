package com.cleanroommc.groovyscript.core.mixin.appliedenergistics2;

import appeng.api.movable.IMovableHandler;
import appeng.core.features.registries.MovableTileRegistry;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;
import java.util.List;

@Mixin(value = MovableTileRegistry.class, remap = false)
public interface MovableTileRegistryAccessor {

    @Accessor
    List<Class<? extends TileEntity>> getTest();

    @Accessor("Valid")
    HashMap<Class<? extends TileEntity>, IMovableHandler> getValid();

}
