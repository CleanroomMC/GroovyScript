package com.cleanroommc.groovyscript.compat.mods.astralsorcery.perktree;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.PerkLevelManagerAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import groovy.lang.Closure;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkLevelManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;

@RegistryDescription
public class PerkTreeConfig extends VirtualizedRegistry<Closure<Long>> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        this.xpFunction = null;
        this.setLevelCap(30);
    }

    public Closure<Long> xpFunction = null;

    @MethodDescription(example = @Example(value = "{ int i, long prev -> prev + 1000L + MathHelper.lfloor(Math.pow(2.0, i / 2.0F + 3)) }", imports = "net.minecraft.util.math.MathHelper"), type = MethodDescription.Type.VALUE)
    public void setXpFunction(Closure<Long> func) {
        if (!Arrays.equals(func.getParameterTypes(), new Class[]{int.class, long.class})) {
            GroovyLog.msg("Warning: Astral Perk xp closures must take the following parameters (int levelNumber, long previousLevelXp)").debug().post();
        }
        this.xpFunction = func;
        resetLevelMap();
    }

    @MethodDescription(example = @Example("50"), type = MethodDescription.Type.VALUE)
    public void setLevelCap(int cap) {
        PerkLevelManagerAccessor.setLevelCap(cap);
        resetLevelMap();
    }

    private void resetLevelMap() {
        ((PerkLevelManagerAccessor) PerkLevelManager.INSTANCE).getLevelMap().clear();
        ((PerkLevelManagerAccessor) PerkLevelManager.INSTANCE).generateLevelMap();
    }
}
