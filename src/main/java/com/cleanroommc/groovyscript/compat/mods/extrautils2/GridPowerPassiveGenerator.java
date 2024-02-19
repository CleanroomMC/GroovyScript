package com.cleanroommc.groovyscript.compat.mods.extrautils2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.extrautils2.GeneratorTypeAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.rwtema.extrautils2.blocks.BlockPassiveGenerator;
import com.rwtema.extrautils2.power.IWorldPowerMultiplier;
import com.rwtema.extrautils2.tile.TilePassiveGenerator;
import groovy.lang.Closure;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GridPowerPassiveGenerator extends VirtualizedRegistry<ResourceLocation> {

    public final Map<ResourceLocation, Float> basePowerMap = new HashMap<>();
    public final Map<ResourceLocation, float[]> scalingMap = new HashMap<>();
    public final Map<ResourceLocation, Closure<Float>> powerLevelMap = new HashMap<>();

    @Override
    public void onReload() {
        basePowerMap.clear();
        scalingMap.clear();
        powerLevelMap.clear();
    }

    public void afterScriptLoad() {
        for (BlockPassiveGenerator.GeneratorType value : BlockPassiveGenerator.GeneratorType.values()) {
            GeneratorTypeAccessor accessor = (GeneratorTypeAccessor) value;
            float[] capsInput = scalingMap.get(accessor.getKey());
            if (capsInput == null) return;
            float[][] caps = new float[capsInput.length / 2][2];

            for (int i = 0; i < capsInput.length; i += 2) {
                caps[i / 2][0] = capsInput[i];
                caps[i / 2][1] = capsInput[i + 1];
            }
            accessor.setCaps(IWorldPowerMultiplier.createCapsTree(caps));
        }
    }

    public void setPowerLevel(ResourceLocation generator, Closure<Float> powerLevel) {
        if (powerLevel == null) {
            GroovyLog.msg("Extra Utilities 2 Grid Power Passive Generator powerLevel closure must be defined")
                    .error()
                    .post();
            return;
        }
        if (Arrays.equals(powerLevel.getParameterTypes(), new Class[]{TilePassiveGenerator.class, World.class})) {
            this.powerLevelMap.put(generator, powerLevel);
            return;
        }
        GroovyLog.msg("Extra Utilities 2 Grid Power Passive Generator powerLevel closure requires a closure with exactly two parameters:")
                .add("com.rwtema.extrautils2.tile.TilePassiveGenerator generator, net.minecraft.world.World world in that order.")
                .add("but had {}, {} instead", (Object[]) powerLevel.getParameterTypes())
                .error()
                .post();
    }

    public void setBasePower(ResourceLocation generator, float basePower) {
        basePowerMap.put(generator, basePower);
    }

    public void setScaling(ResourceLocation generator, float... scaling) {
        scalingMap.put(generator, scaling);
    }

}
