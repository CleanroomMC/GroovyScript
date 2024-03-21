package com.cleanroommc.groovyscript.compat.mods.extrautils2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.extrautils2.GeneratorTypeAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.rwtema.extrautils2.blocks.BlockPassiveGenerator;
import com.rwtema.extrautils2.power.IWorldPowerMultiplier;
import com.rwtema.extrautils2.tile.TilePassiveGenerator;
import groovy.lang.Closure;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Map;

@RegistryDescription
public class GridPowerPassiveGenerator extends VirtualizedRegistry<Pair<BlockPassiveGenerator.GeneratorType, IWorldPowerMultiplier>> {

    public final Map<ResourceLocation, Float> basePowerMap = new Object2FloatOpenHashMap<>();
    public final Map<ResourceLocation, Closure<Float>> powerLevelMap = new Object2ObjectOpenHashMap<>();
    private final Map<ResourceLocation, float[]> scalingMap = new Object2ObjectArrayMap<>();

    @Override
    public void onReload() {
        basePowerMap.clear();
        scalingMap.clear();
        powerLevelMap.clear();
        removeScripted().forEach(x -> ((GeneratorTypeAccessor) x.getKey()).setPowerMultiplier(x.getValue()));
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

    @MethodDescription(type = MethodDescription.Type.VALUE)
    public void setPowerMultiplier(BlockPassiveGenerator.GeneratorType generator, IWorldPowerMultiplier worldPowerMultiplier) {
        addScripted(Pair.of(generator, worldPowerMultiplier));
        ((GeneratorTypeAccessor) generator).setPowerMultiplier(worldPowerMultiplier);
    }

    @MethodDescription(example = @Example(value = "resource('generators:wind'), IWorldPowerMultiplier.CONSTANT", imports = "com.rwtema.extrautils2.power.IWorldPowerMultiplier"), type = MethodDescription.Type.VALUE)
    public void setPowerMultiplier(ResourceLocation generator, IWorldPowerMultiplier worldPowerMultiplier) {
        Arrays.stream(BlockPassiveGenerator.GeneratorType.values())
                .filter(x -> ((GeneratorTypeAccessor) x).getKey().equals(generator))
                .findFirst()
                .ifPresent(x -> setPowerMultiplier(x, worldPowerMultiplier));
    }

    @MethodDescription(type = MethodDescription.Type.VALUE)
    public void setPowerMultiplier(String generator, IWorldPowerMultiplier worldPowerMultiplier) {
        setPowerMultiplier(new ResourceLocation(generator), worldPowerMultiplier);
    }

    @MethodDescription(type = MethodDescription.Type.VALUE,
                       example = @Example(value = "resource('generators:solar'), { TilePassiveGenerator generator, World world -> 100f }", imports = "com.rwtema.extrautils2.tile.TilePassiveGenerator"))
    public void setPowerLevel(ResourceLocation generator, Closure<Float> powerLevel) {
        if (powerLevel == null) {
            GroovyLog.msg("Extra Utilities 2 Grid Power Passive Generator powerLevel closure must be defined")
                    .error()
                    .post();
            return;
        }
        if (!Arrays.equals(powerLevel.getParameterTypes(), new Class[]{TilePassiveGenerator.class, World.class})) {
            GroovyLog.msg("Extra Utilities 2 Grid Power Passive Generator powerLevel closure should be a closure with exactly two parameters:")
                    .add("com.rwtema.extrautils2.tile.TilePassiveGenerator generator, net.minecraft.world.World world in that order.")
                    .add("but had {}, {} instead", (Object[]) powerLevel.getParameterTypes())
                    .debug()
                    .post();
        }
        this.powerLevelMap.put(generator, powerLevel);
    }

    @MethodDescription(type = MethodDescription.Type.VALUE)
    public void setPowerLevel(String generator, Closure<Float> powerLevel) {
        setPowerLevel(new ResourceLocation(generator), powerLevel);
    }

    @MethodDescription(example = {
            @Example("resource('generators:player_wind_up'), 100f"),
            @Example("resource('generators:creative'), 5f")
    }, type = MethodDescription.Type.VALUE)
    public void setBasePower(ResourceLocation generator, float basePower) {
        basePowerMap.put(generator, basePower);
    }

    @MethodDescription(type = MethodDescription.Type.VALUE)
    public void setBasePower(String generator, float basePower) {
        setBasePower(new ResourceLocation(generator), basePower);
    }

    @MethodDescription(example = @Example("resource('generators:creative'), 500.0F, 0.5F, 1000.0F, 0.25F, 1500.0F, 0.05F"), type = MethodDescription.Type.VALUE)
    public void setScaling(ResourceLocation generator, float... scaling) {
        scalingMap.put(generator, scaling);
    }

    @MethodDescription(type = MethodDescription.Type.VALUE)
    public void setScaling(String generator, float... scaling) {
        setScaling(new ResourceLocation(generator), scaling);
    }

}
