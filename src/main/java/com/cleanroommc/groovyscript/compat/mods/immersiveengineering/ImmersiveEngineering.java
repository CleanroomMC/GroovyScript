package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ImmersiveEngineering extends ModPropertyContainer {

    public final AlloyKiln alloyKiln = new AlloyKiln();
    public final ArcFurnace arcFurnace = new ArcFurnace();
    public final BlastFurnace blastFurnace = new BlastFurnace();
    public final BlueprintCrafting blueprint = new BlueprintCrafting();
    public final BottlingMachine bottlingMachine = new BottlingMachine();
    public final CokeOven cokeOven = new CokeOven();
    public final Crusher crusher = new Crusher();
    public final Fermenter fermenter = new Fermenter();
    public final MetalPress metalPress = new MetalPress();
    public final Mixer mixer = new Mixer();
    public final Refinery refinery = new Refinery();
    public final Squeezer squeezer = new Squeezer();

    public ImmersiveEngineering() {
        addRegistry(alloyKiln);
        addRegistry(arcFurnace);
        addRegistry(blastFurnace);
        addRegistry(blueprint);
        addRegistry(bottlingMachine);
        addRegistry(cokeOven);
        addRegistry(crusher);
        addRegistry(fermenter);
        addRegistry(metalPress);
        addRegistry(mixer);
        addRegistry(refinery);
        addRegistry(squeezer);
    }
}
