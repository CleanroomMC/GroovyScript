package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ProdigyTech extends ModPropertyContainer {
    /*
    public final AtomicReshaper atomicReshaper = new AtomicReshaper();
    public final ExplosionFurnace explosionFurnace = new ExplosionFurnace();
    public final PrimordialisReactor primordialisReactor = new PrimordialisReactor();
     */
    public final SimpleRecipeHandler magneticReassembler = new MagneticReassembler();
    public final SimpleRecipeHandlerSecondaryOutput oreRefinery = new OreRefinery();
    public final SimpleRecipeHandler grinder = new RotaryGrinder();
    public final SimpleRecipeHandlerSecondaryOutput sawmill = new HeatSawmill();
    public final Solderer solderer = new Solderer();
    public final ZorraAltar zorraAltar = new ZorraAltar();
    public final ZorraAltarItem zorraAltarItem = new ZorraAltarItem();

    public ProdigyTech() {
        addRegistry(magneticReassembler);
        addRegistry(oreRefinery);
        addRegistry(grinder);
        addRegistry(sawmill);
        addRegistry(solderer);
        addRegistry(zorraAltar);
        addRegistry(zorraAltarItem);
    }

}
