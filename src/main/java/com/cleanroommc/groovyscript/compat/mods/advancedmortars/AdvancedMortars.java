package com.cleanroommc.groovyscript.compat.mods.advancedmortars;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class AdvancedMortars extends ModPropertyContainer {

    public final Mortar mortar = new Mortar();

    public AdvancedMortars() {
        addRegistry(mortar);
    }

}
