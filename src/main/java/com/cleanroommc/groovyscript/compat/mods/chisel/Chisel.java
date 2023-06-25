package com.cleanroommc.groovyscript.compat.mods.chisel;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Chisel extends ModPropertyContainer {

    public final Carving carving = new Carving();

    public Chisel() {
        addRegistry(carving);
    }

}
