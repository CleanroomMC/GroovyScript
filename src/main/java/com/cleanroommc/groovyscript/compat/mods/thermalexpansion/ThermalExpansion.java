package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ThermalExpansion extends ModPropertyContainer {

    public final Pulverizer pulverizer = new Pulverizer();

    public ThermalExpansion() {
        addRegistry(pulverizer);
    }

}
