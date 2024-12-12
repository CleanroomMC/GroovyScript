package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import forestry.modules.ForestryModuleUids;

public class Forestry extends GroovyPropertyContainer {

    public final CharcoalPile charcoalPile = new CharcoalPile();
    public final Squeezer squeezer = new Squeezer();
    public final Still still = new Still();
    public final Centrifuge centrifuge = new Centrifuge();
    public final Fermenter fermenter = new Fermenter();
    public final Moistener moistener = new Moistener();
    public final MoistenerFuel moistenerFuel = new MoistenerFuel();
    public final Carpenter carpenter = new Carpenter();
    public final ThermionicFabricator thermionicFabricator = new ThermionicFabricator();
    public final BeeProduce beeProduce = new BeeProduce();
    public final BeeMutations beeMutations = new BeeMutations();

    public static Result<AlleleBeeSpecies> parseSpecies(String mainArg, Object... args) {
        if (!ForestryAPI.moduleManager.isModuleEnabled("forestry", ForestryModuleUids.APICULTURE)) {
            return Result.error("Can't get bee species while apiculture is disabled.");
        }
        String[] parts = mainArg.split(":");
        if (parts.length < 2) {
            if (args.length > 0 && args[0] instanceof String s) {
                parts = new String[]{
                        parts[0], s
                };
            } else {
                Result.error("Can't find bee species for '{}'", mainArg);
            }
        }
        IAlleleBeeSpecies species = (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(parts[0] + "." + parts[1]);
        if (species instanceof AlleleBeeSpecies beeSpecies) return Result.some(beeSpecies);

        species = (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(parts[0] + "." + getNormalName(parts[1]));
        if (species instanceof AlleleBeeSpecies alleleBeeSpecies) return Result.some(alleleBeeSpecies);
        return Result.error();
    }

    protected static String getNormalName(String name) {
        String capital = name.substring(0, 1).toUpperCase() + name.substring(1);
        return "species" + capital;
    }

    @Override
    public void initialize(GroovyContainer<?> container) {
        container.objectMapperBuilder("species", AlleleBeeSpecies.class)
                .parser(Forestry::parseSpecies)
                .completerOfNamed(() -> AlleleManager.alleleRegistry.getRegisteredAlleles().keySet(), s -> s.replace('.', ':')) // elements don't have names
                .docOfType("allele bee species")
                .register();
    }
}
