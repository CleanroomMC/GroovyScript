package com.cleanroommc.groovyscript.compat.mods.astralsorcery.perktree;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.PerkLevelManagerAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import groovy.lang.Closure;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkLevelManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Random;

public class PerkTreeConfig extends VirtualizedRegistry<Closure<Long>> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        this.setXpFunction(null);
        this.setLevelCap(30);
    }

    public Closure<Long> xpFunction = null;

    public void setXpFunction(Closure<Long> func) {
        if (!Arrays.equals(func.getParameterTypes(), new Class[]{int.class, long.class})) {
            GroovyLog.msg("Warning: Astral Perk xp closures must take the following parameters (int levelNumber, long previousLevelXp)").debug().post();
        }
        this.xpFunction = func;
        ((PerkLevelManagerAccessor) PerkLevelManager.INSTANCE).getLevelMap().clear();
        ((PerkLevelManagerAccessor) PerkLevelManager.INSTANCE).generateLevelMap();
    }

    public void setLevelCap(int cap) {
        PerkLevelManagerAccessor.setLevelCap(cap);
        ((PerkLevelManagerAccessor) PerkLevelManager.INSTANCE).getLevelMap().clear();
        ((PerkLevelManagerAccessor) PerkLevelManager.INSTANCE).generateLevelMap();
    }

}
