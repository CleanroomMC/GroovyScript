package com.cleanroommc.groovyscript.brackets;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IBracketHandler;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import forestry.modules.ForestryModuleUids;

public class SpeciesBracketHandler implements IBracketHandler<AlleleBeeSpecies> {

    public static final SpeciesBracketHandler INSTANCE = new SpeciesBracketHandler();

    private SpeciesBracketHandler() {
    }

    @Override
    public AlleleBeeSpecies parse(String arg) {
        if (!ForestryAPI.moduleManager.isModuleEnabled("forestry", ForestryModuleUids.APICULTURE)) {
            GroovyLog.msg("Can't get bee species while apiculture is disabled.").error().post();
            return null;
        }
        String[] parts = arg.split(":");
        if (parts.length < 2) {
            GroovyLog.msg("Can't find bee species for '{}'", arg).error().post();
            return null;
        }
        IAlleleBeeSpecies species = (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(parts[0] + "." + parts[1]);
        if (species instanceof AlleleBeeSpecies) return (AlleleBeeSpecies) species;

        species = (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(parts[0] + "." + getNormalName(parts[1]));
        if (species instanceof AlleleBeeSpecies) return (AlleleBeeSpecies) species;

        GroovyLog.msg("Can't find bee species for '{}'", arg);
        return null;
    }

    protected String getNormalName(String name) {
        String capital = name.substring(0, 1).toUpperCase() + name.substring(1);
        return "species" + capital;
    }
}
