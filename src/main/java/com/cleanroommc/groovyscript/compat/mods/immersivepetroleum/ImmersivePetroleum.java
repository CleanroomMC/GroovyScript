package com.cleanroommc.groovyscript.compat.mods.immersivepetroleum;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ImmersivePetroleum extends ModPropertyContainer {

    public final Distillation distillation = new Distillation();
    public final Reservoir reservoir = new Reservoir();

    public ImmersivePetroleum() {
        addRegistry(distillation);
        addRegistry(reservoir);
    }

}
