package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import com.cleanroommc.groovyscript.api.IGameObjectParser;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.thermalexpansion.device.*;
import com.cleanroommc.groovyscript.compat.mods.thermalexpansion.dynamo.*;
import com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine.*;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandler;

import java.util.Arrays;
import java.util.Locale;

public class ThermalExpansion extends ModPropertyContainer {

    public final Brewer brewer = new Brewer();
    public final Centrifuge centrifuge = new Centrifuge();
    public final CentrifugeMobs centrifugeMob = new CentrifugeMobs();
    public final Charger charger = new Charger();
    public final Compactor compactor = new Compactor();
    public final Compression compression = new Compression();
    public final Coolant coolant = new Coolant();
    public final Crucible crucible = new Crucible();
    public final Diffuser diffuser = new Diffuser();
    public final Enchanter enchanter = new Enchanter();
    public final Enervation enervation = new Enervation();
    public final Extruder extruder = new Extruder();
    public final Factorizer factorizer = new Factorizer();
    public final Fisher fisher = new Fisher();
    public final FisherBait fisherBait = new FisherBait();
    public final Furnace furnace = new Furnace();
    public final FurnacePyrolysis furnacePyrolysis = new FurnacePyrolysis();
    public final Insolator insolator = new Insolator();
    public final Lapidary lapidary = new Lapidary();
    public final Magmatic magmatic = new Magmatic();
    public final Numismatic numismatic = new Numismatic();
    public final Precipitator precipitator = new Precipitator();
    public final Pulverizer pulverizer = new Pulverizer();
    public final Reactant reactant = new Reactant();
    public final Refinery refinery = new Refinery();
    public final RefineryPotion refineryPotion = new RefineryPotion();
    public final Sawmill sawmill = new Sawmill();
    public final Smelter smelter = new Smelter();
    public final Steam steam = new Steam();
    public final Tapper tapper = new Tapper();
    public final TapperFertilizer tapperFertilizer = new TapperFertilizer();
    public final TapperTree tapperTree = new TapperTree();
    public final TransposerExtract transposerExtract = new TransposerExtract();
    public final TransposerFill transposerFill = new TransposerFill();
    public final XpCollector xpCollector = new XpCollector();


    public ThermalExpansion() {
        addRegistry(brewer);
        addRegistry(centrifuge);
        addRegistry(centrifugeMob);
        addRegistry(charger);
        addRegistry(compactor);
        addRegistry(compression);
        addRegistry(coolant);
        addRegistry(crucible);
        addRegistry(diffuser);
        addRegistry(enchanter);
        addRegistry(enervation);
        addRegistry(extruder);
        addRegistry(factorizer);
        addRegistry(fisher);
        addRegistry(fisherBait);
        addRegistry(furnace);
        addRegistry(furnacePyrolysis);
        addRegistry(insolator);
        addRegistry(lapidary);
        addRegistry(magmatic);
        addRegistry(numismatic);
        addRegistry(precipitator);
        addRegistry(pulverizer);
        addRegistry(reactant);
        addRegistry(refinery);
        addRegistry(refineryPotion);
        addRegistry(sawmill);
        addRegistry(smelter);
        addRegistry(steam);
        addRegistry(tapper);
        addRegistry(tapperFertilizer);
        addRegistry(tapperTree);
        addRegistry(transposerExtract);
        addRegistry(transposerFill);
        addRegistry(xpCollector);
    }

    @Override
    public void initialize() {
        GameObjectHandler.builder("compactorMode", CompactorManager.Mode.class)
                .mod("thermalexpansion")
                .parser(IGameObjectParser.wrapEnum(CompactorManager.Mode.class, false))
                .completerOfNamed(() -> Arrays.asList(CompactorManager.Mode.values()), v -> v.name().toUpperCase(Locale.ROOT))
                .register();
    }

}
