package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Thaumcraft extends ModPropertyContainer {

    public final Crucible crucible = new Crucible();

    public Thaumcraft() {
        addRegistry(crucible);
    }
}
