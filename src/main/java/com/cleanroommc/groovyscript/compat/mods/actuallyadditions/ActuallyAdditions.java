package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ActuallyAdditions extends ModPropertyContainer {

    public final Crusher crusher = new Crusher();
    public final OilGen oilGen = new OilGen();
    public final Compost compost = new Compost();
    public final BallOfFur ballOfFur = new BallOfFur();
    public final TreasureChest treasureChest = new TreasureChest();
    public final Empowerer empowerer = new Empowerer();
    public final AtomicReconstructor atomicReconstructor = new AtomicReconstructor();
    public final NetherMiningLens netherMiningLens = new NetherMiningLens();
    public final StoneMiningLens stoneMiningLens = new StoneMiningLens();

    public ActuallyAdditions() {
        addRegistry(crusher);
        addRegistry(oilGen);
        addRegistry(compost);
        addRegistry(ballOfFur);
        addRegistry(treasureChest);
        addRegistry(empowerer);
        addRegistry(atomicReconstructor);
        addRegistry(netherMiningLens);
        addRegistry(stoneMiningLens);
    }

}
