package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Mekanism extends ModPropertyContainer {

    public final Crusher crusher = new Crusher();

    public Mekanism() {
        addRegistry(crusher);
    }

}
