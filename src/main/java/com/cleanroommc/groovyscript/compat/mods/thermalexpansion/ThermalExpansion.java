package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.thermalexpansion.device.*;
import com.cleanroommc.groovyscript.compat.mods.thermalexpansion.dynamo.*;
import com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine.*;
import com.cleanroommc.groovyscript.mapper.ObjectMapper;

import java.util.Arrays;
import java.util.Locale;

public class ThermalExpansion extends GroovyPropertyContainer {

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

    @Override
    public void initialize(GroovyContainer<?> owner) {
        ObjectMapper.builder("compactorMode", CompactorManager.Mode.class)
                .mod("thermalexpansion")
                .parser(IObjectParser.wrapEnum(CompactorManager.Mode.class, false))
                .completerOfNamed(() -> Arrays.asList(CompactorManager.Mode.values()), v -> v.name().toUpperCase(Locale.ROOT))
                .register();
    }
}
