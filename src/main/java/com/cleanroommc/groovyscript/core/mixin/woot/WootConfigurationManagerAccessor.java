package com.cleanroommc.groovyscript.core.mixin.woot;

import ipsis.woot.configuration.EnumConfigKey;
import ipsis.woot.configuration.WootConfigurationManager;
import ipsis.woot.util.WootMobName;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(value = WootConfigurationManager.class, remap = false)
public interface WootConfigurationManagerAccessor {

    @Accessor
    Map<EnumConfigKey, Integer> getIntegerMap();

    @Accessor
    Map<EnumConfigKey, Boolean> getBooleanMap();

    @Accessor
    Map<String, Integer> getIntegerMobMap();

    @Accessor
    Map<String, Boolean> getBooleanMobMap();

    @Invoker
    String callMakeKey(WootMobName wootMobName, EnumConfigKey configKey);

}
