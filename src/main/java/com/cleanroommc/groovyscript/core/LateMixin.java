package com.cleanroommc.groovyscript.core;

import com.cleanroommc.groovyscript.compat.mods.ic2.IC2;
import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;
import java.util.stream.Collectors;

public class LateMixin implements ILateMixinLoader {

    public static final List<String> modMixins = ImmutableList.of(
            "advancedmortars",
            "appliedenergistics2",
            "astralsorcery",
            "bloodmagic",
            "botania",
            "draconicevolution",
            "enderio",
            "extendedcrafting",
            "forestry",
            "ic2_classic",
            "ic2_exp",
            "inspirations",
            "jei",
            "mekanism",
            "roots",
            "tcomplement",
            "tconstruct",
            "thermalexpansion",
            "woot"
    );

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
        if (mod.startsWith("ic2")) {
            return Loader.isModLoaded("ic2") && mod.endsWith("exp") == IC2.isExp();
        }
        return Loader.isModLoaded(mod);
    }
}
