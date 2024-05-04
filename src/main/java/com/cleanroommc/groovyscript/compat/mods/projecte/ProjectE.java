package com.cleanroommc.groovyscript.compat.mods.projecte;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ProjectE extends ModPropertyContainer {

    public final EntityRandomizerMob entityRandomizerMob = new EntityRandomizerMob();
    public final EntityRandomizerPeaceful entityRandomizerPeaceful = new EntityRandomizerPeaceful();
    public final Transmutation transmutation = new Transmutation();

    public ProjectE() {
        addRegistry(entityRandomizerMob);
        addRegistry(entityRandomizerPeaceful);
        addRegistry(transmutation);
    }

}
