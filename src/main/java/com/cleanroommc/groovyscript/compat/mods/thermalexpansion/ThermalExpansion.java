package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ThermalExpansion extends ModPropertyContainer {

    public final Pulverizer pulverizer = new Pulverizer();
    public final Brewer brewer = new Brewer();
    public final Crucible crucible = new Crucible();
    public final Centrifuge centrifuge = new Centrifuge();
    public final Charger charger = new Charger();
    public final Compactor compactor = new Compactor();
    public final Enchanter enchanter = new Enchanter();

    public ThermalExpansion() {
        addRegistry(pulverizer);
        addRegistry(brewer);
        addRegistry(crucible);
        addRegistry(centrifuge);
        addRegistry(charger);
        addRegistry(compactor);
        addRegistry(enchanter);
    }

}
