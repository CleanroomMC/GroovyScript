package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkLevelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(value = PerkLevelManager.class, remap = false)
public interface PerkLevelManagerAccessor {

    @Accessor("LEVEL_CAP")
    static void setLevelCap(int cap) {}

    @Accessor("totalExpLevelRequired")
    Map<Integer, Long> getLevelMap();

    @Invoker("ensureLevels")
    void generateLevelMap();

}
