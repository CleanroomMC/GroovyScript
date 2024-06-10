package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class IntegratedDynamics extends GroovyPropertyContainer {

    public final DryingBasin dryingBasin = new DryingBasin();
    public final MechanicalDryingBasin mechanicalDryingBasin = new MechanicalDryingBasin();
    public final Squeezer squeezer = new Squeezer();
    public final MechanicalSqueezer mechanicalSqueezer = new MechanicalSqueezer();

}
