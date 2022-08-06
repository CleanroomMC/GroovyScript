package com.cleanroommc.groovyscript;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;
import java.util.stream.Collectors;

public class LateMixin implements ILateMixinLoader {

    public static final List<String> modMixins = ImmutableList.of("jei", "mekanism", "enderio", "thermalexpansion");

    @Override
    public List<String> getMixinConfigs() {
        return modMixins.stream().map(mod -> "mixin.groovyscript." + mod + ".json").collect(Collectors.toList());
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        String[] parts = mixinConfig.split("\\.");
        return parts.length != 4 || shouldEnableModMixin(parts[2]);
    }

    public boolean shouldEnableModMixin(String mod) {
        return Loader.isModLoaded(mod);
    }
}
