package com.cleanroommc.groovyscript.compat.mods.astralsorcery.perktree;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.PerkLevelManagerAccessor;
import com.cleanroommc.groovyscript.documentation.annotations.Example;
import com.cleanroommc.groovyscript.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import groovy.lang.Closure;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkLevelManager;
import org.jetbrains.annotations.ApiStatus;

@RegistryDescription
public class PerkTreeConfig extends VirtualizedRegistry<Closure<Long>> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        this.setXpFunction(null);
        this.setLevelCap(30);
    }

    public Closure<Long> xpFunction = null;

    @MethodDescription(example = @Example(value = "{ int i, long prev -> prev + 1000L + MathHelper.lfloor(Math.pow(2.0, i / 2.0F + 3)) }", imports = "net.minecraft.util.math.MathHelper"), type = MethodDescription.Type.VALUE)
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

    @MethodDescription(example = @Example("50"), type = MethodDescription.Type.VALUE)
    public void setLevelCap(int cap) {
        PerkLevelManagerAccessor.setLevelCap(cap);
        ((PerkLevelManagerAccessor) PerkLevelManager.INSTANCE).getLevelMap().clear();
        ((PerkLevelManagerAccessor) PerkLevelManager.INSTANCE).generateLevelMap();
    }

}
