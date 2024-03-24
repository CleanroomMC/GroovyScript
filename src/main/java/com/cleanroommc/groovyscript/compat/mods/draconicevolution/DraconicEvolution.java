package com.cleanroommc.groovyscript.compat.mods.draconicevolution;

import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class DraconicEvolution extends ModPropertyContainer {

    public final Fusion fusion = new Fusion();
    public final EnergyCore energyCore;

    public DraconicEvolution() {
        this.energyCore = GroovyScriptConfig.compat.draconicEvolutionEnergyCore ? new EnergyCore() : null;
        addRegistry(fusion);
        if (this.energyCore != null) addRegistry(energyCore);
    }
}
