package com.cleanroommc.groovyscript.compat.mods.draconicevolution;

import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class DraconicEvolution extends GroovyPropertyContainer {

    public final Fusion fusion = new Fusion();
    public final EnergyCore energyCore;

    public DraconicEvolution() {
        this.energyCore = GroovyScriptConfig.compat.draconicEvolutionEnergyCore ? new EnergyCore() : null;
    }
}
