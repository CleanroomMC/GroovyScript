package com.cleanroommc.groovyscript.compat.mods.compactmachines;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class CompactMachines extends ModPropertyContainer {

    public final Miniaturization miniaturization = new Miniaturization();

    public CompactMachines() {
        addRegistry(miniaturization);
    }

}
