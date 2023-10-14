package com.cleanroommc.groovyscript.compat.mods.inspirations;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Inspirations extends ModPropertyContainer {

    public final Cauldron cauldron = new Cauldron();
    public final AnvilSmashing anvilSmashing = new AnvilSmashing();

    public Inspirations() {
        addRegistry(cauldron);
        addRegistry(anvilSmashing);
    }
}
