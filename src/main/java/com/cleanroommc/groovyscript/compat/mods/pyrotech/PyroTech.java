package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class PyroTech extends ModPropertyContainer {

    public final Barrel barrel = new Barrel();
    public final Campfire campfire = new Campfire();
    public final ChoppingBlock choppingBlock = new ChoppingBlock();
    public final CompactingBin compactingBin = new CompactingBin();
    public final CompostBin compostBin = new CompostBin();
    public final CrudeDryingRack crudeDryingRack = new CrudeDryingRack();
    public final DryingRack dryingRack = new DryingRack();
    public final Kiln kiln = new Kiln();
    public final Anvil anvil = new Anvil();
    public final SoakingPot soakingPot = new SoakingPot();
    public final TanningRack tanningRack = new TanningRack();

    public PyroTech() {
        addRegistry(barrel);
        addRegistry(campfire);
        addRegistry(choppingBlock);
        addRegistry(compactingBin);
        addRegistry(compostBin);
        addRegistry(crudeDryingRack);
        addRegistry(dryingRack);
        addRegistry(kiln);
        addRegistry(anvil);
        addRegistry(soakingPot);
        addRegistry(tanningRack);
    }


}
