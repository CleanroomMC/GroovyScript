package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class IntegratedDynamics extends ModPropertyContainer {

    public final DryingBasin dryingBasin = new DryingBasin();
    public final MechanicalDryingBasin mechanicalDryingBasin = new MechanicalDryingBasin();
    public final Squeezer squeezer = new Squeezer();
    public final MechanicalSqueezer mechanicalSqueezer = new MechanicalSqueezer();

    public IntegratedDynamics() {
        addRegistry(dryingBasin);
        addRegistry(mechanicalDryingBasin);
        addRegistry(squeezer);
        addRegistry(mechanicalSqueezer);
    }
}
