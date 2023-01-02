package com.cleanroommc.groovyscript.compat.mods.astralsorcery.perktree;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.PerkLevelManagerAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import groovy.lang.Closure;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkLevelManager;
import org.jetbrains.annotations.ApiStatus;

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
        if (func != null) {
            if (func.getParameterTypes().length != 2 || func.getParameterTypes()[0] != int.class || func.getParameterTypes()[1] != long.class) {
                GroovyLog.msg("Astral Perk xp function requires a closure with exactly two parameters: int levelNumber, long previousLevelXp in that order.").error().post();
                return;
            }
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
