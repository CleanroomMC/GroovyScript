package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class TinkersConstruct extends ModPropertyContainer {
    public final Drying drying = new Drying();
    public final Melting melting = new Melting();
    public final SmelteryFuel smelteryFuel = new SmelteryFuel();
    public final Alloying alloying = new Alloying();

    public TinkersConstruct() {
        addRegistry(drying);
        addRegistry(melting);
        addRegistry(smelteryFuel);
        addRegistry(alloying);
        addRegistry(melting.entityMelting);
    }
}
