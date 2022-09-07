// crusher
mods.mekanism.Crusher.add('<item:minecraft:clay_ball>', '<minecraft:gold_ingot>')
mods.mekanism.Crusher.removeByInput('<ore:ingotSilver>')

// chemical infuser
mods.mekanism.ChemicalInfuser.add(gas('copper'), gas('iron'), gas('gold'))
mods.mekanism.ChemicalInfuser.removeByInput(gas('hydrogen'), gas('chlorine'))

// chemical oxidizer
mods.mekanism.ChemicalOxidizer.add(ore('dustGold'), gas('gold'))
mods.mekanism.ChemicalOxidizer.removeByInput(ore('dustSulfur'))

// combiner
mods.mekanism.Combiner.add(ore('gemQuartz') * 8, item('minecraft:netherrack'), item('minecraft:quartz_ore'))
mods.mekanism.Combiner.removeByInput(ore('gemQuartz') * 8, item('minecraft:cobblestone'))

// crystallizer
mods.mekanism.Crystallizer.removeByInput(gas('cleanGold')) // either remove first or don't remove at all. the recipe below should overwrite the original recipe
mods.mekanism.Crystallizer.add(gas('cleanGold'), item('minecraft:gold_ingot'))

// dissolution chamber
mods.mekanism.DissolutionChamber.add(item('minecraft:packed_ice'), gas('water'))
mods.mekanism.DissolutionChamber.removeByInput(item('mekanism:oreblock:0'))

// enrichment chamber
mods.mekanism.EnrichmentChamber.add(item('minecraft:clay_ball'), item('minecraft:nether_star'))
mods.mekanism.EnrichmentChamber.removeByInput(item('minecraft:diamond'))

// injection chamber
mods.mekanism.InjectionChamber.add(item('minecraft:diamond'), gas('water'), item('minecraft:nether_star'))
mods.mekanism.InjectionChamber.removeByInput(item('minecraft:hardened_clay'), gas('water'))

// metallurgic infuser
mods.mekanism.MetallurgicInfuser.add(item('minecraft:clay_ball'), 'DIAMOND', 200, item('minecraft:nether_star'))
mods.mekanism.MetallurgicInfuser.removeByInput(ore('dustObsidian'), 'DIAMOND')

// osmium compressor
mods.mekanism.OsmiumCompressor.add(item('minecraft:diamond'), gas('liquidosmium'), item('minecraft:nether_star'))
mods.mekanism.OsmiumCompressor.removeByInput(ore('dustRefinedObsidian'), gas('liquidosmium'))

// prc
mods.mekanism.PRC.recipeBuilder()
        .fluidInput(fluid('water'))
        .gasInput(gas('water'))
        .input(item('minecraft:clay_ball'))
        .output(item('minecraft:diamond'))
        .gasOutput(gas('ethene'))
        .register()
mods.mekanism.PRC.removeByInput(ore('dustCoal'), fluid('water'), gas('oxygen'))

// purification chamber
mods.mekanism.PurificationChamber.add(item('minecraft:diamond'), gas('oxygen'), item('minecraft:nether_star'))
mods.mekanism.PurificationChamber.removeByInput(item('mekanism:oreblock:0'), gas('oxygen'))

// sawmill
mods.mekanism.Sawmill.add(item('minecraft:diamond_block'), item('minecraft:diamond') * 9)
mods.mekanism.Sawmill.removeByInput(item('minecraft:ladder'))

// separator
mods.mekanism.ElectrolyticSeparator.add(fluid('lava'), gas('cleanGold'), gas('cleanCopper'), 3000)
mods.mekanism.ElectrolyticSeparator.removeByInput(fluid('water'))

// solar neutron activator
mods.mekanism.SolarNeutronActivator.add(gas('water'), gas('hydrogen'))
mods.mekanism.SolarNeutronActivator.removeByInput(gas('lithium'))

// thermal evaporation plant
mods.mekanism.ThermalEvaporationPlant.removeByInput(fluid('water'))
mods.mekanism.ThermalEvaporationPlant.add(fluid('water'), fluid('steam'))

// washer
mods.mekanism.Washer.add(gas('water'), gas('hydrogen'))
mods.mekanism.Washer.removeByInput(gas('iron'))
