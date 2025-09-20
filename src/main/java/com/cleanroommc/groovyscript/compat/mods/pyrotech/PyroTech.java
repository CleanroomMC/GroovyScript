package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class PyroTech extends GroovyPropertyContainer {

    public final Barrel barrel;
    public final Campfire campfire;
    public final StoneOven stoneOven;
    public final BrickOven brickOven;
    public final ChoppingBlock choppingBlock;
    public final StoneSawmill stoneSawmill;
    public final BrickSawmill brickSawmill;
    public final CompactingBin compactingBin;
    public final MechanicalCompactingBin mechanicalCompactingBin;
    public final CompostBin compostBin;
    public final CrudeDryingRack crudeDryingRack;
    public final DryingRack dryingRack;
    public final PitKiln pitKiln;
    public final StoneKiln stoneKiln;
    public final BrickKiln brickKiln;
    public final Anvil anvil;
    public final SoakingPot soakingPot;
    public final TanningRack tanningRack;
    public final StoneCrucible stoneCrucible;
    public final BrickCrucible brickCrucible;

    public PyroTech() {
        this.barrel = new Barrel();
        this.campfire = new Campfire();
        this.stoneOven = new StoneOven();
        this.brickOven = new BrickOven();
        this.choppingBlock = new ChoppingBlock();
        this.stoneSawmill = new StoneSawmill();
        this.brickSawmill = new BrickSawmill();
        this.compactingBin = new CompactingBin();
        this.mechanicalCompactingBin = new MechanicalCompactingBin();
        this.compostBin = new CompostBin();
        this.crudeDryingRack = new CrudeDryingRack();
        this.dryingRack = new DryingRack();
        this.pitKiln = new PitKiln();
        this.stoneKiln = new StoneKiln();
        this.brickKiln = new BrickKiln();
        this.anvil = new Anvil();
        this.soakingPot = new SoakingPot();
        this.tanningRack = new TanningRack();
        this.stoneCrucible = new StoneCrucible();
        this.brickCrucible = new BrickCrucible();
    }
}
