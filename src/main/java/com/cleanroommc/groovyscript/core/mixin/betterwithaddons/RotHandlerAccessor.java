package com.cleanroommc.groovyscript.core.mixin.betterwithaddons;

import betterwithaddons.handler.RotHandler;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(value = RotHandler.class, remap = false)
public interface RotHandlerAccessor {

    @Accessor("rottingItems")
    static Multimap<Item, RotHandler.RotInfo> getRottingItems() {
        throw new UnsupportedOperationException();
    }
}
