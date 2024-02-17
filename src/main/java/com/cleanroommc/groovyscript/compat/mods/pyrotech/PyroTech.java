package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class PyroTech extends ModPropertyContainer {

    public static final Barrel barrel = new Barrel();
    public static final Campfire campfire = new Campfire();
    public static final ChoppingBlock choppingBlock = new ChoppingBlock();
    public static final CompactingBin compactingBin = new CompactingBin();
    public static final CompostBin compostBin = new CompostBin();
    public static final CrudeDryingRack crudeDryingRack = new CrudeDryingRack();
    public static final DryingRack dryingRack = new DryingRack();
    public static final Kiln kiln = new Kiln();
    public static final Anvil anvil = new Anvil();
    public static final SoakingPot soakingPot = new SoakingPot();
    public static final TanningRack tanningRack = new TanningRack();

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
