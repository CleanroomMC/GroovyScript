package com.cleanroommc.groovyscript;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

public class LateMixin implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return ImmutableList.of("mixin.groovyscript.jei.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return Loader.isModLoaded(mixinConfig.split("\\.")[2]);
    }
}
