package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Mekanism extends ModPropertyContainer {

    public final ChemicalInfuser chemicalInfuser = new ChemicalInfuser();
    public final Combiner combiner = new Combiner();
    public final Crusher crusher = new Crusher();
    public final Crystallizer crystallizer = new Crystallizer();
    public final DissolutionChamber dissolutionChamber = new DissolutionChamber();
    public final EnrichmentChamber enrichmentChamber = new EnrichmentChamber();
    public final InjectionChamber injectionChamber = new InjectionChamber();
    public final MetallurgicInfuser metallurgicInfuser = new MetallurgicInfuser();
    public final OsmiumCompressor osmiumCompressor = new OsmiumCompressor();
    public final ChemicalOxidizer chemicalOxidizer = new ChemicalOxidizer();
    public final PurificationChamber purificationChamber = new PurificationChamber();
    public final Sawmill sawmill = new Sawmill();
    public final Separator separator = new Separator();
    public final Smelter smelter = new Smelter();
    public final SolarNeutronActivator solarNeutronActivator = new SolarNeutronActivator();
    public final ThermalEvaporation thermalEvaporation = new ThermalEvaporation();

    public Mekanism() {
        addRegistry(chemicalInfuser);
        addRegistry(combiner);
        addRegistry(crusher);
        addRegistry(crystallizer);
        addRegistry(dissolutionChamber);
        addRegistry(enrichmentChamber);
        addRegistry(injectionChamber);
        addRegistry(metallurgicInfuser);
        addRegistry(osmiumCompressor);
        addRegistry(chemicalOxidizer);
        addRegistry(purificationChamber);
        addRegistry(sawmill);
        addRegistry(separator);
        addRegistry(smelter);
        addRegistry(solarNeutronActivator);
        addRegistry(thermalEvaporation);
    }
}
