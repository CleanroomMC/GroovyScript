package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(value = WellLiquefaction.class, remap = false)
public interface WellLiquefactionAccessor {

    @Accessor
    static Map<ItemStack, WellLiquefaction.LiquefactionEntry> getRegisteredLiquefactions() {
        throw new UnsupportedOperationException();
    }

}
