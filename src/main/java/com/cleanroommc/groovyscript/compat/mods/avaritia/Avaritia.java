package com.cleanroommc.groovyscript.compat.mods.avaritia;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Avaritia extends ModPropertyContainer {

    public final ExtremeCrafting extremeCrafting = new ExtremeCrafting();
    public final Compressor compressor = new Compressor();

    public Avaritia() {
        addRegistry(this.extremeCrafting);
        addRegistry(this.compressor);
    }
}
