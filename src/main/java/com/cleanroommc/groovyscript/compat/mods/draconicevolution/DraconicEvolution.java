package com.cleanroommc.groovyscript.compat.mods.draconicevolution;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class DraconicEvolution extends ModPropertyContainer {

    public final Fusion fusion = new Fusion();
    public final EnergyCore energyCore = new EnergyCore();

    public DraconicEvolution() {
        addRegistry(fusion);
        addRegistry(energyCore);
    }
}
