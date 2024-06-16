package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class IndustrialForegoing extends ModPropertyContainer {

    public final BioReactor bioReactor = new BioReactor();
    public final Extractor extractor = new Extractor();
    public final FluidDictionary fluidDictionary = new FluidDictionary();
    public final LaserDrill laserDrill = new LaserDrill();
    public final OreFermenter oreFermenter = new OreFermenter();
    public final OreRaw oreRaw = new OreRaw();
    public final OreSieve oreSieve = new OreSieve();
    public final ProteinReactor proteinReactor = new ProteinReactor();
    public final SludgeRefiner sludgeRefiner = new SludgeRefiner();
    public final Straw straw = new Straw();

    public IndustrialForegoing() {
        addRegistry(bioReactor);
        addRegistry(extractor);
        addRegistry(fluidDictionary);
        addRegistry(laserDrill);
        addRegistry(oreFermenter);
        addRegistry(oreRaw);
        addRegistry(oreSieve);
        addRegistry(proteinReactor);
        addRegistry(sludgeRefiner);
        addRegistry(straw);
    }

}
