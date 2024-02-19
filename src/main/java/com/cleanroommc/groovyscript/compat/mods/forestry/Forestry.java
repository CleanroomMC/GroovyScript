package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlerManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import forestry.modules.ForestryModuleUids;
import org.jetbrains.annotations.Nullable;

public class Forestry extends ModPropertyContainer {

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

    public Forestry() {
        addRegistry(charcoalPile);
        addRegistry(squeezer);
        addRegistry(still);
        addRegistry(centrifuge);
        addRegistry(fermenter);
        addRegistry(moistener);
        addRegistry(moistenerFuel);
        addRegistry(carpenter);
        addRegistry(thermionicFabricator);
        addRegistry(thermionicFabricator.smelting);
        addRegistry(beeProduce);
        addRegistry(beeMutations);
    }

    @Override
    public void initialize() {
        GameObjectHandlerManager.registerGameObjectHandler("forestry", "species", Forestry::parseSpecies);
    }

    @Override
    public @Nullable Object getProperty(String name) {
        Object registry = super.getProperty(name);
        if (registry instanceof ForestryRegistry<?> && !((ForestryRegistry<?>) registry).isEnabled()) return null;
        return registry;
    }

    public static Result<AlleleBeeSpecies> parseSpecies(String mainArg, Object... args) {
        if (!ForestryAPI.moduleManager.isModuleEnabled("forestry", ForestryModuleUids.APICULTURE)) {
            return Result.error("Can't get bee species while apiculture is disabled.");
        }
        String[] parts = mainArg.split(":");
        if (parts.length < 2) {
            Result.error("Can't find bee species for '{}'", mainArg);
        }
        IAlleleBeeSpecies species = (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(parts[0] + "." + parts[1]);
        if (species instanceof AlleleBeeSpecies) return Result.some((AlleleBeeSpecies) species);

        species = (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(parts[0] + "." + getNormalName(parts[1]));
        if (species instanceof AlleleBeeSpecies) return Result.some((AlleleBeeSpecies) species);
        return Result.error();
    }

    protected static String getNormalName(String name) {
        String capital = name.substring(0, 1).toUpperCase() + name.substring(1);
        return "species" + capital;
    }
}
