package com.cleanroommc.groovyscript.compat.mods.astralsorcery.perktree;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.PerkLevelManagerAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import groovy.lang.Closure;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkLevelManager;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;

public class PerkTreeConfig extends VirtualizedRegistry<Closure<Long>> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        // TODO: add reloading support, pending sandbox.
    }

    public Closure<Long> xpFunction = new Closure<Long>(null) {
        @Override
        public Long call(Object... args) {
            return ( (long) args[1] ) + 150L + MathHelper.lfloor(Math.pow(2.0, (double)(((int)args[0]) / 2 + 3)));
        }
    };

    public void setXpFunction(Closure<Long> func) {
        if (func != null) {
            this.xpFunction = func;
            ((PerkLevelManagerAccessor) PerkLevelManager.INSTANCE).getLevelMap().clear();
        }
    }

    public void setLevelCap(int cap) {
        PerkLevelManagerAccessor.setLevelCap(cap);
        ((PerkLevelManagerAccessor) PerkLevelManager.INSTANCE).getLevelMap().clear();
    }

}
