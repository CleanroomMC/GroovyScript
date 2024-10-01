
// Auto generated groovyscript example file
// MODS_LOADED: techreborn

log.info 'mod \'techreborn\' detected, running script'

// Alloy Smelter:
// Converts two itemstack inputs into an itemstack output after a given process time, consuming energy per tick.

mods.techreborn.alloy_smelter.removeByInput(item('techreborn:ingot:4'))
mods.techreborn.alloy_smelter.removeByOutput(item('techreborn:ingot:5'))
// mods.techreborn.alloy_smelter.removeAll()

mods.techreborn.alloy_smelter.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.alloy_smelter.recipeBuilder()
    .input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Assembling Machine:
// Converts two itemstack inputs into an itemstack output after a given process time, consuming energy per tick.

mods.techreborn.assembling_machine.removeByInput(item('techreborn:plates:35'))
mods.techreborn.assembling_machine.removeByOutput(item('techreborn:part:29'))
// mods.techreborn.assembling_machine.removeAll()

mods.techreborn.assembling_machine.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.assembling_machine.recipeBuilder()
    .input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Industrial Blast Furnace:
// Converts one or two itemstack inputs into one or two itemstack outputs after a given process time, requiring at least a
// given amount of heat and consuming energy per tick.

mods.techreborn.blast_furnace.removeByInput(item('techreborn:dust:1'))
mods.techreborn.blast_furnace.removeByOutput(item('techreborn:ingot:12'))
// mods.techreborn.blast_furnace.removeAll()

mods.techreborn.blast_furnace.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(100)
    .neededHeat(3800)
    .register()

mods.techreborn.blast_furnace.recipeBuilder()
    .input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .neededHeat(1500)
    .register()


// Industrial Centrifuge:
// Converts one or two itemstack inputs into up to four an itemstack output after a given process time, consuming energy
// per tick.

mods.techreborn.centrifuge.removeByInput(item('techreborn:dust:33'))
mods.techreborn.centrifuge.removeByOutput(item('techreborn:dynamiccell').withNbt(['Fluid': ['FluidName': 'fluidmethane', 'Amount': 1000]]))
// mods.techreborn.centrifuge.removeAll()

mods.techreborn.centrifuge.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot'), item('minecraft:clay') * 5, item('minecraft:clay') * 2, item('minecraft:clay'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.centrifuge.recipeBuilder()
    .input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Chemical Reactor:
// Converts two itemstack inputs into an itemstack output after a given process time, consuming energy per tick.

mods.techreborn.chemical_reactor.removeByInput(item('techreborn:dynamiccell').withNbt(['Fluid': ['FluidName': 'water', 'Amount': 1000]]))
mods.techreborn.chemical_reactor.removeByOutput(item('techreborn:dynamiccell').withNbt(['Fluid': ['FluidName': 'water', 'Amount': 1000]]))
// mods.techreborn.chemical_reactor.removeAll()

mods.techreborn.chemical_reactor.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.chemical_reactor.recipeBuilder()
    .input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Compressor:
// Converts an itemstack input into an itemstack output after a given process time, consuming energy per tick.

mods.techreborn.compressor.removeByInput(item('minecraft:diamond'))
mods.techreborn.compressor.removeByOutput(item('techreborn:plates:36'))
// mods.techreborn.compressor.removeAll()

mods.techreborn.compressor.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.compressor.recipeBuilder()
    .input(item('minecraft:diamond') * 3)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Diesel Generator:
// Converts a fluidstack input into power, at a given rate per tick.

mods.techreborn.diesel_generator.removeByInput(fluid('fluiddiesel'))
// mods.techreborn.diesel_generator.removeAll()

mods.techreborn.diesel_generator.recipeBuilder()
    .fluidInput(fluid('water'))
    .energy(10000)
    .perTick(500)
    .register()

mods.techreborn.diesel_generator.recipeBuilder()
    .fluidInput(fluid('lava'))
    .energy(200)
    .perTick(10)
    .register()


// Distillation Tower:
// Converts one or two itemstack inputs into up to four an itemstack output after a given process time, consuming energy
// per tick.

mods.techreborn.distillation_tower.removeByInput(item('techreborn:dynamiccell').withNbt(['Fluid': ['FluidName': 'fluidoil', 'Amount': 1000]]))
// mods.techreborn.distillation_tower.removeByOutput(item('techreborn:dynamiccell').withNbt(['Fluid': ['FluidName': 'fluidmethane', 'Amount': 1000]]))
// mods.techreborn.distillation_tower.removeAll()

mods.techreborn.distillation_tower.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot'), item('minecraft:clay') * 5, item('minecraft:clay') * 2, item('minecraft:clay'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.distillation_tower.recipeBuilder()
    .input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Extractor:
// Converts an itemstack input into an itemstack output after a given process time, consuming energy per tick.

mods.techreborn.extractor.removeByInput(item('minecraft:slime_ball'))
mods.techreborn.extractor.removeByOutput(item('minecraft:wool'))
// mods.techreborn.extractor.removeAll()

mods.techreborn.extractor.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.extractor.recipeBuilder()
    .input(item('minecraft:diamond') * 3)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Fluid Replicator:
// Converts a configurable amount of UU-Matter into a output fluidstack of 1000mb after a given process time, replicating a
// fluid source block placed in-world and consuming energy per tick.

mods.techreborn.fluid_replicator.removeByOutput(fluid('water'))
// mods.techreborn.fluid_replicator.removeAll()

mods.techreborn.fluid_replicator.recipeBuilder()
    .matter(10)
    .fluidOutput(fluid('water'))
    .time(100)
    .perTick(10)
    .register()

mods.techreborn.fluid_replicator.recipeBuilder()
    .matter(1)
    .fluidOutput(fluid('fluidmethane'))
    .time(5)
    .perTick(1000)
    .register()


// Fusion Reactor:
// Converts two itemstack inputs into an itemstack output after a given process time, requiring a cost to start the recipe
// and either generating or consuming power while the recipe runs.

mods.techreborn.fusion_reactor.removeByInput(item('techreborn:part:17'))
mods.techreborn.fusion_reactor.removeByOutput(item('techreborn:ore:1'))
// mods.techreborn.fusion_reactor.removeAll()

mods.techreborn.fusion_reactor.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(-25000)
    .start(200)
    .size(30)
    .register()

mods.techreborn.fusion_reactor.recipeBuilder()
    .input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(30000)
    .start(1000000)
    .register()


// Gas Turbine:
// Converts a fluidstack input into power, at a given rate per tick.

mods.techreborn.gas_turbine.removeByInput(fluid('fluidhydrogen'))
// mods.techreborn.gas_turbine.removeAll()

mods.techreborn.gas_turbine.recipeBuilder()
    .fluidInput(fluid('water'))
    .energy(10000)
    .perTick(500)
    .register()

mods.techreborn.gas_turbine.recipeBuilder()
    .fluidInput(fluid('lava'))
    .energy(200)
    .perTick(10)
    .register()


// Grinder:
// Converts an itemstack input into an itemstack output after a given process time, consuming energy per tick.

mods.techreborn.grinder.removeByInput(item('minecraft:coal_ore'))
mods.techreborn.grinder.removeByOutput(item('minecraft:diamond'))
// mods.techreborn.grinder.removeAll()

mods.techreborn.grinder.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.grinder.recipeBuilder()
    .input(item('minecraft:diamond') * 3)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Implosion Compressor:
// Converts two itemstack inputs into up to two itemstack outputs after a given process time, consuming energy per tick.

mods.techreborn.implosion_compressor.removeByInput(item('techreborn:ingot:22'))
mods.techreborn.implosion_compressor.removeByOutput(item('minecraft:diamond'))
// mods.techreborn.implosion_compressor.removeAll()

mods.techreborn.implosion_compressor.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.implosion_compressor.recipeBuilder()
    .input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Industrial Electrolyzer:
// Converts up to two itemstack inputs into up to four itemstack outputs after a given process time, consuming energy per
// tick.

mods.techreborn.industrial_electrolyzer.removeByInput(item('minecraft:dye:15'))
mods.techreborn.industrial_electrolyzer.removeByOutput(item('techreborn:dust:1'))
// mods.techreborn.industrial_electrolyzer.removeAll()

mods.techreborn.industrial_electrolyzer.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot'), item('minecraft:clay') * 5, item('minecraft:clay') * 2, item('minecraft:clay'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.industrial_electrolyzer.recipeBuilder()
    .input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Industrial Grinder:
// Converts an itemstack input and fluidstack input into up to four itemstack outputs after a given process time, consuming
// energy per tick.

mods.techreborn.industrial_grinder.removeByInput(fluid('water'))
mods.techreborn.industrial_grinder.removeByInput(item('techreborn:ore2'))
mods.techreborn.industrial_grinder.removeByOutput(item('techreborn:dust:53'))
// mods.techreborn.industrial_grinder.removeAll()

mods.techreborn.industrial_grinder.recipeBuilder()
    .input(item('minecraft:clay'))
    .fluidInput(fluid('lava') * 50)
    .output(item('minecraft:gold_ingot'), item('minecraft:clay') * 5, item('minecraft:clay') * 2, item('minecraft:clay'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.industrial_grinder.recipeBuilder()
    .input(item('minecraft:diamond') * 3)
    .fluidInput(fluid('water') * 250)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Sawmill:
// Converts an itemstack input and fluidstack output into three itemstack outputs after a given process time, consuming
// energy per tick.

mods.techreborn.industrial_sawmill.removeByInput(fluid('water'))
mods.techreborn.industrial_sawmill.removeByInput(item('minecraft:log'))
mods.techreborn.industrial_sawmill.removeByOutput(item('minecraft:planks:4'))
// mods.techreborn.industrial_sawmill.removeAll()

mods.techreborn.industrial_sawmill.recipeBuilder()
    .input(item('minecraft:clay'))
    .fluidInput(fluid('lava') * 100)
    .output(item('minecraft:gold_ingot'), item('minecraft:clay') * 5, item('minecraft:clay'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.industrial_sawmill.recipeBuilder()
    .input(item('minecraft:diamond') * 3)
    .fluidInput(fluid('water') * 500)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Plasma Generator:
// Converts a fluidstack input into power, at a given rate per tick.

mods.techreborn.plasma_generator.removeByInput(fluid('fluidheliumplasma'))
// mods.techreborn.plasma_generator.removeAll()

mods.techreborn.plasma_generator.recipeBuilder()
    .fluidInput(fluid('water'))
    .energy(10000)
    .perTick(500)
    .register()

mods.techreborn.plasma_generator.recipeBuilder()
    .fluidInput(fluid('lava'))
    .energy(200)
    .perTick(10)
    .register()


// Plate Bending Machine:
// Converts an itemstack input into an itemstack output after a given process time, consuming energy per tick.

mods.techreborn.plate_bending_machine.removeByInput(item('minecraft:gold_ingot'))
mods.techreborn.plate_bending_machine.removeByOutput(item('techreborn:plates:36'))
// mods.techreborn.plate_bending_machine.removeAll()

mods.techreborn.plate_bending_machine.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.plate_bending_machine.recipeBuilder()
    .input(item('minecraft:diamond') * 3)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Rolling Machine:
// Converts a custom crafting recipe into an output itemstack.

mods.techreborn.rolling_machine.removeByOutput(item('minecraft:tripwire_hook'))
// mods.techreborn.rolling_machine.removeAll()

mods.techreborn.rolling_machine.shapedBuilder()
    .output(item('minecraft:stone'))
    .matrix('BXX',
            'X B')
    .key('B', item('minecraft:stone'))
    .key('X', item('minecraft:gold_ingot'))
    .mirrored()
    .register()

mods.techreborn.rolling_machine.shapedBuilder()
    .output(item('minecraft:diamond') * 32)
    .matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]])
    .register()

mods.techreborn.rolling_machine.shapelessBuilder()
    .output(item('minecraft:clay') * 8)
    .input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))
    .register()

mods.techreborn.rolling_machine.shapelessBuilder()
    .output(item('minecraft:clay') * 32)
    .input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'))
    .register()



// Scrapbox:
// Converts a scrapbox into a random itemstack output, either via manual player interaction or via a machine with a given
// process time, consuming energy per tick.

mods.techreborn.scrapbox.removeByOutput(item('minecraft:diamond'))
// mods.techreborn.scrapbox.removeAll()

mods.techreborn.scrapbox.recipeBuilder()
    .output(item('minecraft:clay'))
    .register()

mods.techreborn.scrapbox.recipeBuilder()
    .output(item('minecraft:gold_block'))
    .time(2)
    .perTick(100)
    .register()


// Semi-Fluid Generator:
// Converts a fluidstack input into power, at a given rate per tick.

mods.techreborn.semi_fluid_generator.removeByInput(fluid('fluidbiofuel'))
// mods.techreborn.semi_fluid_generator.removeAll()

mods.techreborn.semi_fluid_generator.recipeBuilder()
    .fluidInput(fluid('water'))
    .energy(10000)
    .perTick(500)
    .register()

mods.techreborn.semi_fluid_generator.recipeBuilder()
    .fluidInput(fluid('lava'))
    .energy(200)
    .perTick(10)
    .register()


// Solid Canning Machine:
// Converts two itemstack inputs into an itemstack output after a given process time, consuming energy per tick.

mods.techreborn.solid_canning_machine.removeByInput(item('techreborn:ingot:23'))
mods.techreborn.solid_canning_machine.removeByOutput(item('techreborn:part:46'))
// mods.techreborn.solid_canning_machine.removeAll()

mods.techreborn.solid_canning_machine.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.solid_canning_machine.recipeBuilder()
    .input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Thermal Generator:
// Converts a fluidstack input into power, at a given rate per tick.

mods.techreborn.thermal_generator.removeByInput(fluid('lava'))
// mods.techreborn.thermal_generator.removeAll()

mods.techreborn.thermal_generator.recipeBuilder()
    .fluidInput(fluid('water'))
    .energy(10000)
    .perTick(500)
    .register()

mods.techreborn.thermal_generator.recipeBuilder()
    .fluidInput(fluid('lava'))
    .energy(200)
    .perTick(10)
    .register()


// Vacuum Freezer:
// Converts an itemstack input into an itemstack output after a given process time, consuming energy per tick.

mods.techreborn.vacuum_freezer.removeByInput(item('techreborn:dynamiccell').withNbt(['Fluid': ['FluidName': 'water', 'Amount': 1000]]))
mods.techreborn.vacuum_freezer.removeByOutput(item('minecraft:packed_ice'))
// mods.techreborn.vacuum_freezer.removeAll()

mods.techreborn.vacuum_freezer.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.vacuum_freezer.recipeBuilder()
    .input(item('minecraft:diamond') * 3)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


// Wire Mill:
// Converts an itemstack input into an itemstack output after a given process time, consuming energy per tick.

mods.techreborn.wire_mill.removeByInput(item('minecraft:gold_ingot'))
mods.techreborn.wire_mill.removeByOutput(item('techreborn:cable'))
// mods.techreborn.wire_mill.removeAll()

mods.techreborn.wire_mill.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .time(10)
    .perTick(100)
    .register()

mods.techreborn.wire_mill.recipeBuilder()
    .input(item('minecraft:diamond') * 3)
    .output(item('minecraft:clay') * 2)
    .time(5)
    .perTick(32)
    .register()


