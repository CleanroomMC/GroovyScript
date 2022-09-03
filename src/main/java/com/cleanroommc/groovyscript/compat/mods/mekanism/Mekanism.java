package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Mekanism extends ModPropertyContainer {

    public final ChemicalInfuser chemicalInfuser = new ChemicalInfuser();
    public final Combiner combiner = new Combiner();
    public final Crusher crusher = new Crusher();

    public Mekanism() {
        addRegistry(chemicalInfuser);
        addRegistry(combiner);
        addRegistry(crusher);
    }

}
