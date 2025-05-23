package com.cleanroommc.groovyscript.core.mixin.armorplus;

import com.sofodev.armorplus.api.lavainfuser.LavaInfuserManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = LavaInfuserManager.class, remap = false)
public interface LavaInfuserManagerAccessor {

    @Accessor("experienceList")
    Map<ItemStack, Double> getExperienceList();
}
