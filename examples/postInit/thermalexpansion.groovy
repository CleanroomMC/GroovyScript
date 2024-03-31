
// Auto generated groovyscript example file
// MODS_LOADED: thermalexpansion

import cofh.thermalexpansion.util.managers.machine.InsolatorManager

println 'mod \'thermalexpansion\' detected, running script'

// Alchemical Imbuer:
// Converts an input fluidstack and input itemstack into an output fluidstack, costing power and taking time based on the
// power cost.

mods.thermalexpansion.brewer.removeByInput(item('minecraft:glowstone_dust'))
mods.thermalexpansion.brewer.removeByInput(fluid('potion').withNbt(['Potion': 'minecraft:leaping']))
mods.thermalexpansion.brewer.removeByOutput(fluid('potion_splash').withNbt(['Potion': 'cofhcore:luck2']))
// mods.thermalexpansion.brewer.removeAll()

mods.thermalexpansion.brewer.recipeBuilder()
    .input(item('minecraft:clay'))
    .fluidInput(fluid('water') * 100)
    .fluidOutput(fluid('lava') * 100)
    .register()

mods.thermalexpansion.brewer.recipeBuilder()
    .input(item('minecraft:diamond') * 2)
    .fluidInput(fluid('water') * 1000)
    .fluidOutput(fluid('steam') * 100)
    .energy(1000)
    .register()


// mods.thermalexpansion.brewer.add(1000, item('minecraft:obsidian') * 2, fluid('water') * 1000, fluid('steam') * 100)

// Centrifugal Separator:
// Converts an input itemstack into an optional output fluidstack and up to four output itemstacks with chance, costing
// power and taking time based on the power cost.

mods.thermalexpansion.centrifuge.removeByInput(item('minecraft:reeds'))
mods.thermalexpansion.centrifuge.removeByOutput(fluid('redstone'))
mods.thermalexpansion.centrifuge.removeByOutput(item('minecraft:redstone'))
// mods.thermalexpansion.centrifuge.removeAll()

mods.thermalexpansion.centrifuge.recipeBuilder()
    .input(item('minecraft:clay'))
    .fluidOutput(fluid('water') * 100)
    .output(item('minecraft:diamond') * 2, item('minecraft:gold_ingot'), item('minecraft:gold_ingot'))
    .chance(50, 100, 1)
    .register()

mods.thermalexpansion.centrifuge.recipeBuilder()
    .input(item('minecraft:diamond') * 3)
    .output(item('minecraft:clay'))
    .chance(100)
    .energy(1000)
    .register()


// mods.thermalexpansion.centrifuge.add(1000, item('minecraft:obsidian') * 3, [item('minecraft:clay')], [100], null)

// Centrifugal Separator - Enstabulation Apparatus:
// Converts an input itemstack into an optional output fluidstack and up to four output itemstacks with chance, costing
// power and taking time based on the power cost.

mods.thermalexpansion.centrifuge_mobs.removeByInput(item('thermalexpansion:morb').withNbt(['id': 'minecraft:slime']))
mods.thermalexpansion.centrifuge_mobs.removeByOutput(item('minecraft:fish'))
// mods.thermalexpansion.centrifuge_mobs.removeByOutput(fluid('experience'))
// mods.thermalexpansion.centrifuge_mobs.removeAll()

mods.thermalexpansion.centrifuge_mobs.recipeBuilder()
    .input(item('thermalexpansion:morb').withNbt(['id': 'minecraft:slime']))
    .fluidOutput(fluid('water') * 100)
    .output(item('minecraft:diamond') * 2, item('minecraft:gold_ingot'), item('minecraft:gold_ingot'))
    .chance(50, 100, 1)
    .register()

mods.thermalexpansion.centrifuge_mobs.recipeBuilder()
    .input(item('minecraft:diamond') * 3)
    .output(item('minecraft:clay'))
    .chance(100)
    .energy(1000)
    .register()


// mods.thermalexpansion.centrifuge_mobs.add(1000, item('minecraft:obsidian') * 3, item('minecraft:clay'), 100)

// Energetic Infuser:
// Converts an input itemstack into an output itemstack, costing power and taking time based on the power cost.

mods.thermalexpansion.charger.removeByInput(item('thermalfoundation:bait:1'))
mods.thermalexpansion.charger.removeByOutput(item('thermalfoundation:fertilizer:2'))
// mods.thermalexpansion.charger.removeAll()

mods.thermalexpansion.charger.recipeBuilder()
    .input(item('minecraft:diamond') * 5)
    .output(item('minecraft:clay'))
    .register()

mods.thermalexpansion.charger.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 2)
    .energy(1000)
    .register()


// mods.thermalexpansion.charger.add(1000, item('minecraft:obsidian'), item('minecraft:diamond') * 2)

// Compactor:
// Converts an input itemstack into an output itemstack, with different modes each requiring a different augment to be
// installed, costing power and taking time based on the power cost.

mods.thermalexpansion.compactor.removeByInput(mode('coin'), item('thermalfoundation:material:130'))
mods.thermalexpansion.compactor.removeByInput(item('minecraft:iron_ingot'))
// mods.thermalexpansion.compactor.removeByMode(mode('plate'))
mods.thermalexpansion.compactor.removeByOutput(mode('coin'), item('thermalfoundation:coin:102'))
mods.thermalexpansion.compactor.removeByOutput(item('minecraft:blaze_rod'))
mods.thermalexpansion.compactor.removeByOutput(item('thermalfoundation:material:24'))
// mods.thermalexpansion.compactor.removeAll()

mods.thermalexpansion.compactor.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 2)
    .mode(mode('coin'))
    .register()

mods.thermalexpansion.compactor.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .mode(mode('all'))
    .register()

mods.thermalexpansion.compactor.recipeBuilder()
    .input(item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot'))
    .mode(mode('plate'))
    .energy(1000)
    .register()


// mods.thermalexpansion.compactor.add(1000, mode('plate'), item('minecraft:obsidian') * 2, item('minecraft:gold_ingot'))

// Compression Dynamo:
// Converts an input fluidstack into power, taking time based on the power.

mods.thermalexpansion.compression.removeByInput(fluid('seed_oil'))
// mods.thermalexpansion.compression.removeAll()

mods.thermalexpansion.compression.add(fluid('steam'), 100)

// Thermal Mediator:
// Consumes fluid to speed up the tick rate of adjacent machines and devices and generate power in the Compression Dynamo.

mods.thermalexpansion.coolant.remove(fluid('cryotheum'))
// mods.thermalexpansion.coolant.removeAll()

mods.thermalexpansion.coolant.add(fluid('lava'), 4000, 30)

// Magma Crucible:
// Converts an input itemstack into an output itemstack, costing power and taking time based on the power cost.

mods.thermalexpansion.crucible.removeByInput(item('minecraft:glowstone_dust'))
mods.thermalexpansion.crucible.removeByOutput(fluid('lava'))
// mods.thermalexpansion.crucible.removeAll()

mods.thermalexpansion.crucible.recipeBuilder()
    .input(item('minecraft:clay'))
    .fluidOutput(fluid('lava') * 25)
    .register()

mods.thermalexpansion.crucible.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidOutput(fluid('water') * 1000)
    .energy(1000)
    .register()


mods.thermalexpansion.crucible.add(1000, item('minecraft:obsidian'), fluid('water') * 1000)

// Decorative Diffuser:
// Controls what items can be used in to boost the potion time and level in the Decorative Diffuser.

mods.thermalexpansion.diffuser.remove(item('minecraft:redstone'))
// mods.thermalexpansion.diffuser.removeAll()

mods.thermalexpansion.diffuser.add(item('minecraft:clay'), 2, 30)

// Arcane Ensorcellator:
// Converts two input itemstacks and liquid experience into an output itemstack, costing power and taking time based on the
// power cost.

mods.thermalexpansion.enchanter.removeByInput(item('minecraft:blaze_rod'))
// mods.thermalexpansion.enchanter.removeByInput(item('minecraft:book'))
mods.thermalexpansion.enchanter.removeByOutput(item('minecraft:enchanted_book').withNbt(['StoredEnchantments': [['lvl': 1, 'id': 34]]]))
// mods.thermalexpansion.enchanter.removeAll()

mods.thermalexpansion.enchanter.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:gold_ingot') * 4)
    .output(item('minecraft:diamond'))
    .register()

mods.thermalexpansion.enchanter.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .experience(1000)
    .energy(1000)
    .register()


mods.thermalexpansion.enchanter.add(1000, item('minecraft:obsidian'), item('minecraft:gold_ingot'), item('minecraft:diamond'), 1000)
mods.thermalexpansion.enchanter.addArcana(item('minecraft:clay'))

// Enervation Dynamo:
// Converts an input itemstack into power, taking time based on the power.

mods.thermalexpansion.enervation.removeByInput(item('minecraft:redstone'))
// mods.thermalexpansion.enervation.removeAll()

mods.thermalexpansion.enervation.add(item('minecraft:clay'), 100)

// Igneous Extruder:
// Converts a variable amount of lava and water into a specific output itemstack.

// mods.thermalexpansion.extruder.removeByInput(false, fluid('lava'))
// mods.thermalexpansion.extruder.removeByInput(fluid('water'))
mods.thermalexpansion.extruder.removeByOutput(true, item('minecraft:gravel'))
mods.thermalexpansion.extruder.removeByOutput(item('minecraft:obsidian'))
// mods.thermalexpansion.extruder.removeByType(true)
// mods.thermalexpansion.extruder.removeAll()

mods.thermalexpansion.extruder.recipeBuilder()
    .fluidHot(100)
    .fluidCold(1000)
    .output(item('minecraft:clay'))
    .register()

mods.thermalexpansion.extruder.recipeBuilder()
    .fluidHot(100)
    .fluidCold(1000)
    .output(item('minecraft:gold_ingot'))
    .sedimentary()
    .energy(1000)
    .register()


mods.thermalexpansion.extruder.add(1000, item('minecraft:gold_block'), 100, 1000, false)

// Factorizer:
// Converts an input itemstack into an output itemstack, with the ability to undo the the recipe. Mainly used for
// compressing ingots into blocks and splitting blocks into ingots.

mods.thermalexpansion.factorizer.removeByInput(false, item('minecraft:diamond'))
mods.thermalexpansion.factorizer.removeByInput(item('minecraft:coal:1'))
// mods.thermalexpansion.factorizer.removeByOutput(false, item('minecraft:coal:1'))
mods.thermalexpansion.factorizer.removeByOutput(item('minecraft:emerald_block'))
// mods.thermalexpansion.factorizer.removeByType(true)
// mods.thermalexpansion.factorizer.removeAll()

mods.thermalexpansion.factorizer.recipeBuilder()
    .input(item('minecraft:clay') * 7)
    .output(item('minecraft:book') * 2)
    .combine()
    .split()
    .register()

mods.thermalexpansion.factorizer.recipeBuilder()
    .input(item('minecraft:planks:*') * 4)
    .output(item('minecraft:crafting_table'))
    .combine()
    .register()



// Aquatic Entangler:
// Controls what itemstacks can be gained and how likely each is to be obtained.

mods.thermalexpansion.fisher.remove(item('minecraft:fish:0'))
// mods.thermalexpansion.fisher.removeAll()

mods.thermalexpansion.fisher.add(item('minecraft:clay'), 100)

// Aquatic Entangler Bait:
// Controls what items can be used in the bait slot of the Aquatic Entangler and how effective they are.

mods.thermalexpansion.fisher_bait.remove(item('thermalfoundation:bait:2'))
// mods.thermalexpansion.fisher_bait.removeAll()

mods.thermalexpansion.fisher_bait.add(item('minecraft:clay'), 100)

// Redstone Furnace:
// Converts an input itemstack into an output itemstack, costing power and taking time based on the power cost.

mods.thermalexpansion.furnace.removeByInput(item('minecraft:cactus:*'))
mods.thermalexpansion.furnace.removeByOutput(item('minecraft:cooked_porkchop'))
mods.thermalexpansion.furnace.removeFood(item('minecraft:rabbit:*'))
// mods.thermalexpansion.furnace.removeAll()
// mods.thermalexpansion.furnace.removeAllFood()

mods.thermalexpansion.furnace.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay') * 2)
    .register()

mods.thermalexpansion.furnace.recipeBuilder()
    .input(item('minecraft:gold_ingot') * 2)
    .output(item('minecraft:clay'))
    .energy(1000)
    .register()


mods.thermalexpansion.furnace.add(1000, item('minecraft:obsidian') * 2, item('minecraft:clay'))
mods.thermalexpansion.furnace.addFood(item('minecraft:emerald_ore'))

// Redstone Furnace - Pyrolytic Conversion:
// Converts an input itemstack into an output itemstack and creosote amount, costing power and taking time based on the
// power cost.

mods.thermalexpansion.furnace_pyrolysis.removeByInput(item('minecraft:cactus:*'))
mods.thermalexpansion.furnace_pyrolysis.removeByOutput(item('thermalfoundation:storage_resource:1'))
// mods.thermalexpansion.furnace_pyrolysis.removeAll()

mods.thermalexpansion.furnace_pyrolysis.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 2)
    .creosote(100)
    .register()

mods.thermalexpansion.furnace_pyrolysis.recipeBuilder()
    .input(item('minecraft:gold_ingot') * 2)
    .output(item('minecraft:clay'))
    .creosote(1000)
    .energy(1000)
    .register()


mods.thermalexpansion.furnace_pyrolysis.add(1000, item('minecraft:obsidian') * 2, item('minecraft:clay'), 1000)

// Phytogenic Insolator:
// Converts two input itemstacks into an output itemstack and optional output itemstack with a chance, costing power and
// taking time based on the power cost.

mods.thermalexpansion.insolator.removeByInput(item('minecraft:double_plant:4'))
mods.thermalexpansion.insolator.removeByInput(item('thermalfoundation:fertilizer'))
mods.thermalexpansion.insolator.removeByOutput(item('minecraft:melon_seeds'))
mods.thermalexpansion.insolator.removeByOutput(item('minecraft:red_flower:6'))
// mods.thermalexpansion.insolator.removeAll()

mods.thermalexpansion.insolator.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:diamond'))
    .output(item('minecraft:diamond') * 4)
    .register()

mods.thermalexpansion.insolator.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:gold_ingot') * 2)
    .output(item('minecraft:clay'), item('minecraft:diamond'))
    .chance(5)
    .water(100)
    .tree()
    .energy(1000)
    .register()


mods.thermalexpansion.insolator.add(1000, 100, item('minecraft:obsidian'), item('minecraft:gold_ingot') * 2, item('minecraft:clay'), item('minecraft:diamond'), 5, InsolatorManager.Type.TREE)

// Numismatic Dynamo - Lapidary Calibration:
// Converts an input itemstack into power, taking time based on the power.

mods.thermalexpansion.lapidary.removeByInput(item('minecraft:diamond'))
// mods.thermalexpansion.lapidary.removeAll()

mods.thermalexpansion.lapidary.add(item('minecraft:clay'), 1000)

// Magmatic Dynamo:
// Converts an input fluidstack into power, taking time based on the power.

mods.thermalexpansion.magmatic.removeByInput(fluid('lava'))
// mods.thermalexpansion.magmatic.removeAll()

mods.thermalexpansion.magmatic.add(fluid('steam'), 100)

// Numismatic Dynamo:
// Converts an input itemstack into power, taking time based on the power.

mods.thermalexpansion.numismatic.removeByInput(item('thermalfoundation:coin:69'))
// mods.thermalexpansion.numismatic.removeAll()

mods.thermalexpansion.numismatic.add(item('minecraft:clay'), 100)

// Glacial Precipitator:
// Converts an amount of water into a specific output itemstack, costing power and taking time based on the power cost.

// mods.thermalexpansion.precipitator.removeByInput(fluid('water'))
mods.thermalexpansion.precipitator.removeByOutput(item('minecraft:snowball'))
// mods.thermalexpansion.precipitator.removeAll()

mods.thermalexpansion.precipitator.recipeBuilder()
    .output(item('minecraft:clay'))
    .register()

mods.thermalexpansion.precipitator.recipeBuilder()
    .water(100)
    .output(item('minecraft:clay'))
    .energy(1000)
    .register()


mods.thermalexpansion.precipitator.add(1000, item('minecraft:obsidian'), 100)

// Pulverizer:
// Converts an input itemstack into an output itemstack and optional output itemstack with a chance, costing power and
// taking time based on the power cost.

mods.thermalexpansion.pulverizer.removeByInput(item('minecraft:emerald_ore'))
mods.thermalexpansion.pulverizer.removeByOutput(item('minecraft:diamond'))
mods.thermalexpansion.pulverizer.removeByOutput(item('thermalfoundation:material:772'))
// mods.thermalexpansion.pulverizer.removeAll()

mods.thermalexpansion.pulverizer.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'), item('minecraft:diamond'))
    .chance(1)
    .register()

mods.thermalexpansion.pulverizer.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'), item('minecraft:gold_ingot'))
    .energy(1000)
    .register()


mods.thermalexpansion.pulverizer.add(1000, item('minecraft:obsidian'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), 100)

// Reactant Dynamo:
// Converts an input itemstack and input fluidstack into power, taking time based on the power.

mods.thermalexpansion.reactant.removeByInput(fluid('redstone'))
mods.thermalexpansion.reactant.removeByInput(item('minecraft:blaze_powder'))
mods.thermalexpansion.reactant.removeElementalFluid(fluid('cryotheum'))
mods.thermalexpansion.reactant.removeElementalReactant(item('thermalfoundation:material:1024'))
// mods.thermalexpansion.reactant.removeAll()

mods.thermalexpansion.reactant.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidInput(fluid('steam'))
    .register()

mods.thermalexpansion.reactant.recipeBuilder()
    .input(item('minecraft:clay'))
    .fluidInput(fluid('glowstone'))
    .energy(100)
    .register()


mods.thermalexpansion.reactant.add(item('minecraft:clay'), fluid('steam'), 100)
mods.thermalexpansion.reactant.addElementalFluid(fluid('glowstone'))
mods.thermalexpansion.reactant.addElementalReactant(item('minecraft:clay'))
mods.thermalexpansion.reactant.addElementalReactant(item('minecraft:gunpowder'))

// Fractionating Still:
// Converts an input fluidstack into an output fluidstack and optional output itemstack with chance, costing power and
// taking time based on the power cost.

mods.thermalexpansion.refinery.removeBioFuel(fluid('resin'))
mods.thermalexpansion.refinery.removeByInput(fluid('resin'))
mods.thermalexpansion.refinery.removeByOutput(fluid('refined_biofuel'))
// mods.thermalexpansion.refinery.removeByOutput(item('thermalfoundation:material:771'))
mods.thermalexpansion.refinery.removeFossilFuel(fluid('coal'))
// mods.thermalexpansion.refinery.removeAll()
// mods.thermalexpansion.refinery.removeAllBioFuels()
// mods.thermalexpansion.refinery.removeAllFossilFuels()

mods.thermalexpansion.refinery.recipeBuilder()
    .fluidInput(fluid('water') * 100)
    .fluidOutput(fluid('steam') * 80)
    .register()

mods.thermalexpansion.refinery.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .fluidOutput(fluid('steam') * 150)
    .output(item('minecraft:clay'))
    .chance(25)
    .energy(1000)
    .register()


mods.thermalexpansion.refinery.add(1000, fluid('ender') * 100, fluid('steam') * 150, item('minecraft:clay'), 25)
mods.thermalexpansion.refinery.addBioFuel(fluid('coal'))
mods.thermalexpansion.refinery.addFossilFuel(fluid('crude_oil'))

// Fractionating Still - Alchemical Retort:
// Converts an input fluidstack into an output fluidstack and optional output itemstack with chance, costing power and
// taking time based on the power cost.

mods.thermalexpansion.refinery_potion.removeByInput(fluid('potion_lingering').withNbt(['Potion': 'cofhcore:healing3']))
mods.thermalexpansion.refinery_potion.removeByOutput(fluid('potion_splash').withNbt(['Potion': 'cofhcore:leaping4']))
// mods.thermalexpansion.refinery_potion.removeAll()

mods.thermalexpansion.refinery_potion.recipeBuilder()
    .fluidInput(fluid('water') * 100)
    .fluidOutput(fluid('steam') * 200)
    .register()

mods.thermalexpansion.refinery_potion.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .fluidOutput(fluid('steam') * 30)
    .output(item('minecraft:clay'))
    .chance(75)
    .energy(1000)
    .register()


mods.thermalexpansion.refinery_potion.add(1000, fluid('ender') * 100, fluid('steam') * 30, item('minecraft:clay'), 75)

// Sawmill:
// Converts an input itemstack into an output itemstack and optional output itemstack with a chance, costing power and
// taking time based on the power cost.

mods.thermalexpansion.sawmill.removeByInput(item('minecraft:pumpkin'))
mods.thermalexpansion.sawmill.removeByOutput(item('minecraft:leather'))
mods.thermalexpansion.sawmill.removeByOutput(item('thermalfoundation:material:800'))
// mods.thermalexpansion.sawmill.removeAll()

mods.thermalexpansion.sawmill.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot') * 2)
    .register()

mods.thermalexpansion.sawmill.recipeBuilder()
    .input(item('minecraft:clay') * 4)
    .output(item('minecraft:gold_ingot'), item('minecraft:diamond'))
    .chance(25)
    .energy(1000)
    .register()


mods.thermalexpansion.sawmill.add(1000, item('minecraft:obsidian') * 4, item('minecraft:gold_ingot'), item('minecraft:diamond'), 25)

// Induction Smelter:
// Converts two input itemstacks into an output itemstack and optional output itemstack with a chance, costing power and
// taking time based on the power cost.

mods.thermalexpansion.smelter.removeByInput(ore('sand'))
mods.thermalexpansion.smelter.removeByInput(item('minecraft:iron_ingot'))
mods.thermalexpansion.smelter.removeByOutput(item('thermalfoundation:material:166'))
// mods.thermalexpansion.smelter.removeAll()

mods.thermalexpansion.smelter.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:diamond'))
    .output(item('minecraft:diamond') * 4)
    .register()

mods.thermalexpansion.smelter.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:gold_ingot') * 2)
    .output(item('minecraft:clay'), item('minecraft:diamond'))
    .chance(5)
    .energy(1000)
    .register()


// mods.thermalexpansion.smelter.add(1000, item('minecraft:obsidian'), item('minecraft:gold_ingot') * 2, item('minecraft:clay'), item('minecraft:diamond'), 5)

// Steam Dynamo:
// Converts an input itemstack into power, taking time based on the power.

mods.thermalexpansion.steam.removeByInput(item('minecraft:coal:1'))
// mods.thermalexpansion.steam.removeAll()

mods.thermalexpansion.steam.add(item('minecraft:clay'), 100)

// Arboreal Extractor:
// Controls what items and blocks can be turned into what fluids. Output can be boosted via Fertilizer items.

mods.thermalexpansion.tapper.removeBlockByInput(item('minecraft:log'))
mods.thermalexpansion.tapper.removeItemByInput(item('minecraft:log:1'))
// mods.thermalexpansion.tapper.removeAll()
// mods.thermalexpansion.tapper.removeAllBlocks()
// mods.thermalexpansion.tapper.removeAllItems()

mods.thermalexpansion.tapper.addBlock(item('minecraft:clay'), fluid('lava') * 150)
mods.thermalexpansion.tapper.addItem(item('minecraft:clay'), fluid('lava') * 300)

// Arboreal Extractor Fertilizer:
// Controls what items can be used in the fertilizer slot of the Arboreal Extractor Fertilizer and how effective they are.

mods.thermalexpansion.tapper_fertilizer.remove(item('thermalfoundation:fertilizer:2'))
// mods.thermalexpansion.tapper_fertilizer.removeAll()

mods.thermalexpansion.tapper_fertilizer.add(item('minecraft:clay'), 1000)

// Arboreal Extractor Tree Structures:
// Controls what valid log blocks and leaf blocks are to define a tree structure which the Arboreal Extractor can function
// on. The \"tree\" must contain some number of leaves adjacent to the log blocks to be valid.

mods.thermalexpansion.tapper_tree.removeByLeaf(blockstate('minecraft:leaves', 'variant=birch'))
mods.thermalexpansion.tapper_tree.removeByLog(blockstate('minecraft:log', 'variant=spruce'))
// mods.thermalexpansion.tapper_tree.removeAll()

mods.thermalexpansion.tapper_tree.add(blockstate('minecraft:clay'), blockstate('minecraft:gold_block'))

// Fluid Transposer - Empty:
// Converts an input itemstack into an output fluidstack and optional output itemstack with chance, costing power and
// taking time based on the power cost.

mods.thermalexpansion.transposer_extract.removeByInput(item('minecraft:sponge:1'))
mods.thermalexpansion.transposer_extract.removeByOutput(fluid('seed_oil'))
mods.thermalexpansion.transposer_extract.removeByOutput(item('minecraft:bowl'))
// mods.thermalexpansion.transposer_extract.removeAll()

mods.thermalexpansion.transposer_extract.recipeBuilder()
    .input(item('minecraft:diamond') * 2)
    .fluidOutput(fluid('water') * 100)
    .register()

mods.thermalexpansion.transposer_extract.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 2)
    .fluidOutput(fluid('water') * 50)
    .energy(1000)
    .register()


mods.thermalexpansion.transposer_extract.add(1000, item('minecraft:obsidian'), fluid('water') * 50, item('minecraft:diamond') * 2, 100)

// Fluid Transposer - Fill:
// Converts an input itemstack and input fluidstack into an output itemstack with chance, costing power and taking time
// based on the power cost.

mods.thermalexpansion.transposer_fill.removeByInput(fluid('glowstone'))
mods.thermalexpansion.transposer_fill.removeByInput(item('minecraft:concrete_powder:3'))
mods.thermalexpansion.transposer_fill.removeByOutput(item('minecraft:ice'))
// mods.thermalexpansion.transposer_fill.removeAll()

mods.thermalexpansion.transposer_fill.recipeBuilder()
    .input(item('minecraft:diamond') * 2)
    .fluidInput(fluid('water') * 100)
    .register()

mods.thermalexpansion.transposer_fill.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 2)
    .fluidInput(fluid('water') * 50)
    .energy(1000)
    .register()


mods.thermalexpansion.transposer_fill.add(1000, item('minecraft:obsidian'), fluid('water') * 50, item('minecraft:diamond') * 2, 100)

// Insightful Condenser:
// Collects experience orbs nearby, with the ability to increase the XP gained via catalyst itemstacks.

mods.thermalexpansion.xp_collector.remove(item('minecraft:soul_sand'))
// mods.thermalexpansion.xp_collector.removeAll()

mods.thermalexpansion.xp_collector.add(item('minecraft:clay'), 100, 30)

