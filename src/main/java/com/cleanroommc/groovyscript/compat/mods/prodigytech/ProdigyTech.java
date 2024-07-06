package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ProdigyTech extends ModPropertyContainer {
    public final AtomicReshaper atomicReshaper = new AtomicReshaper();
    public final ExplosionFurnace explosionFurnace = new ExplosionFurnace();
    public final ExplosionFurnaceAdditives explosionAdditives = new ExplosionFurnaceAdditives();
    public final SimpleRecipeHandler magneticReassembler = new SimpleRecipeHandler.MagneticReassembler();
    public final SimpleRecipeHandlerSecondaryOutput oreRefinery = new SimpleRecipeHandlerSecondaryOutput.OreRefinery();
    public final SimpleRecipeHandler grinder = new SimpleRecipeHandler.RotaryGrinder();
    public final PrimordialisReactor primordialisReactor = new PrimordialisReactor();
    public final SimpleRecipeHandlerSecondaryOutput sawmill = new SimpleRecipeHandlerSecondaryOutput.HeatSawmill();
    public final Solderer solderer = new Solderer();
    public final ZorraAltar zorraAltar = new ZorraAltar();

    public ProdigyTech() {
        addRegistry(atomicReshaper);
        addRegistry(explosionFurnace);
        addRegistry(explosionAdditives);
        addRegistry(magneticReassembler);
        addRegistry(oreRefinery);
        addRegistry(grinder);
        addRegistry(primordialisReactor);
        addRegistry(sawmill);
        addRegistry(solderer);
        addRegistry(zorraAltar);
    }

}
