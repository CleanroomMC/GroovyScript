package com.cleanroommc.groovyscript.core.mixin.extrautils2;

import com.rwtema.extrautils2.blocks.BlockPassiveGenerator;
import com.rwtema.extrautils2.power.IWorldPowerMultiplier;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.TreeMap;

@Mixin(value = BlockPassiveGenerator.GeneratorType.class, remap = false)
public interface GeneratorTypeAccessor {

    @Mutable
    @Accessor
    void setPowerMultiplier(IWorldPowerMultiplier powerMultiplier);

    @Accessor
    ResourceLocation getKey();

    @Accessor
    TreeMap<Float, Pair<Float, Float>> getCaps();

    @Mutable
    @Accessor
    void setCaps(TreeMap<Float, Pair<Float, Float>> caps);

}
