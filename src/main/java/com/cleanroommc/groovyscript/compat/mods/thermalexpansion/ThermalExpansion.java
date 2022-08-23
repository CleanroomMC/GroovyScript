package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ThermalExpansion extends ModPropertyContainer {

    public final Pulverizer pulverizer = new Pulverizer();
    public final Brewer brewer = new Brewer();

    public ThermalExpansion() {
        addRegistry(pulverizer);
        addRegistry(brewer);
    }

}
