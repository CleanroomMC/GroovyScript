package com.cleanroommc.groovyscript.compat.mods.projecte;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ProjectE extends ModPropertyContainer {

    public final EntityRandomizer entityRandomizer = new EntityRandomizer();
    public final Transmutation transmutation = new Transmutation();

    public ProjectE() {
        addRegistry(entityRandomizer);
        addRegistry(transmutation);
    }

}
