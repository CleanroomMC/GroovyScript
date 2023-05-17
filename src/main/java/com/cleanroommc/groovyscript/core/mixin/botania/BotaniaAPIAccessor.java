package com.cleanroommc.groovyscript.core.mixin.botania;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import vazkii.botania.api.BotaniaAPI;

@Mixin(value = BotaniaAPI.class, remap = false)
public interface BotaniaAPIAccessor {

    @Invoker
    static String invokeGetMagnetKey(ItemStack stack) {
        throw new AssertionError();
    }

    @Invoker
    static String invokeGetMagnetKey(Block block, int meta) {
        throw new AssertionError();
    }
}
