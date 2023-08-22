package com.cleanroommc.groovyscript.compat.mods.extrautilities2;

import com.cleanroommc.groovyscript.core.mixin.extrautilities2.GeneratorTypeAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.rwtema.extrautils2.api.machine.IMachineRecipe;
import com.rwtema.extrautils2.blocks.BlockPassiveGenerator;
import com.rwtema.extrautils2.power.IWorldPowerMultiplier;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class GridPowerPassiveGenerator extends VirtualizedRegistry<IMachineRecipe> {

    public final Map<ResourceLocation, Float> basePowerMap = new HashMap<>();
    public final Map<ResourceLocation, float[]> scalingMap = new HashMap<>();

    public GridPowerPassiveGenerator() {
        super();
    }

    @Override
    public void onReload() {
        for (BlockPassiveGenerator.GeneratorType value : BlockPassiveGenerator.GeneratorType.values()) {
            GeneratorTypeAccessor accessor = (GeneratorTypeAccessor) value;
            //TODO testing
            //float[] capsInput = scalingMap.get(accessor.getKey());
            float[] capsInput = new float[]{500.0F, 0.5F, 1000.0F, 0.25F, 1500.0F, 0.05F};
            float[][] caps = new float[capsInput.length / 2][2];

            for (int i = 0; i < capsInput.length; i += 2) {
                caps[i / 2][0] = capsInput[i];
                caps[i / 2][1] = capsInput[i + 1];
            }
            accessor.setCaps(IWorldPowerMultiplier.createCapsTree(caps));
            basePowerMap.put(accessor.getKey(), 500f);
        }
    }

    // dragon egg example:
    // 500.0F, 0.5F, 1000.0F, 0.25F, 1500.0F, 0.05F

    //no nerf until 500
    //50% nerf until 1000
    //75% nerf until 1500
    //95% nerf





}
