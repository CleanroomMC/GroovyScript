package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ProdigyTech extends ModPropertyContainer {

    /*
    public final AtomicReshaper atomicReshaper = new AtomicReshaper();
    public final ExplosionFurnace explosionFurnace = new ExplosionFurnace();
    public final HeatSawmill sawmill = new HeatSawmill();
    public final MagneticReassember magneticReassember = new MagneticReassembler();
    public final OreRefinery oreRefinery = new OreRefinery();
    public final PrimordialisReactor primordialisReactor = new PrimordialisReactor();
    public final RotaryGrinder rotaryGrinder = new RotaryGrinder();
    public final ZorraAltar zorraAltar = new ZorraAltar();
     */
    public final Solderer solderer = new Solderer();

    public ProdigyTech() {
        addRegistry(solderer);
    }

}
