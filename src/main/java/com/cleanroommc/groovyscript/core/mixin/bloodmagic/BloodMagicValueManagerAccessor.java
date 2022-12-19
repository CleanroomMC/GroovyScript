package com.cleanroommc.groovyscript.core.mixin.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicValueManager;
import WayofTime.bloodmagic.incense.TranquilityStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = BloodMagicValueManager.class, remap = false)
public interface BloodMagicValueManagerAccessor {

    @Accessor
    Map<IBlockState, TranquilityStack> getTranquility();

    @Accessor
    Map<ResourceLocation, Integer> getSacrificial();
}
