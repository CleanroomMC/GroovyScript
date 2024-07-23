
// Auto generated groovyscript example file
// MODS_LOADED: pneumaticcraft

log.info 'mod \'pneumaticcraft\' detected, running script'

// Amadron:
// Uses an Amadron Tablet and linked inventories in world to trade via drones.

mods.pneumaticcraft.amadron.removeByInput(item('minecraft:rotten_flesh'))
mods.pneumaticcraft.amadron.removeByOutput(item('minecraft:emerald'))
// mods.pneumaticcraft.amadron.removeAll()
// mods.pneumaticcraft.amadron.removeAllPeriodic()
// mods.pneumaticcraft.amadron.removeAllStatic()

mods.pneumaticcraft.amadron.recipeBuilder()
    .input(item('minecraft:clay') * 3)
    .output(item('minecraft:gold_ingot'))
    .register()

mods.pneumaticcraft.amadron.recipeBuilder()
    .fluidInput(fluid('water') * 50)
    .output(item('minecraft:clay') * 3)
    .register()

mods.pneumaticcraft.amadron.recipeBuilder()
    .fluidInput(fluid('water') * 50)
    .fluidOutput(fluid('lava') * 10)
    .periodic()
    .register()


// Assembly Controller:
// Uses a given Program to convert an input itemstack into an output itemstack. Drill recipes that output an itemstack used
// for the input itemstack of a Laser recipe can be chained via the Drill & Laser Program.

mods.pneumaticcraft.assembly_controller.removeByInput(item('minecraft:redstone'))
mods.pneumaticcraft.assembly_controller.removeByOutput(item('pneumaticcraft:pressure_chamber_valve'))
// mods.pneumaticcraft.assembly_controller.removeAll()
// mods.pneumaticcraft.assembly_controller.removeAllDrill()
// mods.pneumaticcraft.assembly_controller.removeAllLaser()

mods.pneumaticcraft.assembly_controller.recipeBuilder()
    .input(item('minecraft:clay') * 3)
    .output(item('minecraft:gold_ingot') * 6)
    .drill()
    .register()

mods.pneumaticcraft.assembly_controller.recipeBuilder()
    .input(item('minecraft:gold_ingot') * 6)
    .output(item('minecraft:diamond'))
    .laser()
    .register()

mods.pneumaticcraft.assembly_controller.recipeBuilder()
    .input(item('minecraft:stone'))
    .output(item('minecraft:clay') * 5)
    .laser()
    .register()


// Explosion:
// Converts an input item into an output item, with a chance to fail and destroy the item.

// mods.pneumaticcraft.explosion.removeByInput(item('minecraft:iron_block'))
mods.pneumaticcraft.explosion.removeByOutput(item('pneumaticcraft:compressed_iron_block'))
// mods.pneumaticcraft.explosion.removeAll()

mods.pneumaticcraft.explosion.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .lossRate(40)
    .register()

mods.pneumaticcraft.explosion.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:obsidian'))
    .register()


// Heat Frame Cooling:
// Converts an input itemstack into an output itemstack when inside an inventory placed within a Heat Frame which is
// cooled.

mods.pneumaticcraft.heat_frame_cooling.removeByInput(item('minecraft:water_bucket'))
mods.pneumaticcraft.heat_frame_cooling.removeByOutput(item('minecraft:obsidian'))
// mods.pneumaticcraft.heat_frame_cooling.removeAll()

mods.pneumaticcraft.heat_frame_cooling.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .register()

mods.pneumaticcraft.heat_frame_cooling.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:obsidian'))
    .register()


// Liquid Fuel:
// Converts fluid into Pressure in a Liquid Compressor.

mods.pneumaticcraft.liquid_fuel.remove(fluid('lava'))
// mods.pneumaticcraft.liquid_fuel.removeAll()

mods.pneumaticcraft.liquid_fuel.recipeBuilder()
    .fluidInput(fluid('water'))
    .pressure(100_000_000)
    .register()



// Plastic Mixer:
// Converts a fluidstack and an item with a variable damage value into each other, requiring temperature to operate the
// process, optionally consuming dye, and allowing either only melting or only solidifying.

// mods.pneumaticcraft.plastic_mixer.removeByFluid(fluid('plastic'))
// mods.pneumaticcraft.plastic_mixer.removeByItem(item('pneumaticcraft:plastic'))
// mods.pneumaticcraft.plastic_mixer.removeAll()

mods.pneumaticcraft.plastic_mixer.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .output(item('minecraft:clay'))
    .allowMelting()
    .allowSolidifying()
    .requiredTemperature(323)
    .register()

mods.pneumaticcraft.plastic_mixer.recipeBuilder()
    .fluidInput(fluid('water') * 50)
    .output(item('minecraft:sapling'))
    .allowSolidifying()
    .requiredTemperature(298)
    .meta(-1)
    .register()


// Pressure Chamber:
// Converts any number of input itemstacks into any number of output itemstacks, either generating Pressure or consuming
// Pressure from the Pressure Chamber.

mods.pneumaticcraft.pressure_chamber.removeByInput(item('minecraft:iron_block'))
mods.pneumaticcraft.pressure_chamber.removeByOutput(item('minecraft:diamond'))
// mods.pneumaticcraft.pressure_chamber.removeAll()

mods.pneumaticcraft.pressure_chamber.recipeBuilder()
    .input(item('minecraft:clay') * 3)
    .output(item('minecraft:gold_ingot'))
    .pressure(4)
    .register()

mods.pneumaticcraft.pressure_chamber.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:gold_block'), item('minecraft:gold_nugget'), item('minecraft:diamond'), item('minecraft:diamond_block'), item('minecraft:obsidian'), item('minecraft:stone'), item('minecraft:stone:1'), item('minecraft:stone:2'), item('minecraft:stone:3'), item('minecraft:stone:4'), item('minecraft:stone:5'), item('minecraft:stone:6'))
    .output(item('minecraft:cobblestone'))
    .pressure(4)
    .register()


// Refinery:
// Converts an input fluidstack into between 2 and 4 fluidstacks, consuming Temperature, with the number of fluidstacks
// output depending on the recipe and the number of Refineries vertically stacked.

// mods.pneumaticcraft.refinery.removeByInput(fluid('oil'))
mods.pneumaticcraft.refinery.removeByOutput(fluid('kerosene'))
// mods.pneumaticcraft.refinery.removeAll()

mods.pneumaticcraft.refinery.recipeBuilder()
    .fluidInput(fluid('water') * 1000)
    .fluidOutput(fluid('lava') * 750, fluid('lava') * 250, fluid('lava') * 100, fluid('lava') * 50)
    .register()

mods.pneumaticcraft.refinery.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .fluidOutput(fluid('water') * 50, fluid('kerosene') * 25)
    .register()


// Thermopneumatic Processing Plant:
// Converts an input fluidstack into an output fluidstack, consuming Pressure and Temperature, with an optional itemstack
// being consumed.

mods.pneumaticcraft.thermopneumatic_processing_plant.removeByInput(fluid('diesel'))
mods.pneumaticcraft.thermopneumatic_processing_plant.removeByInput(item('minecraft:coal'))
mods.pneumaticcraft.thermopneumatic_processing_plant.removeByOutput(fluid('lpg'))
// mods.pneumaticcraft.thermopneumatic_processing_plant.removeAll()

mods.pneumaticcraft.thermopneumatic_processing_plant.recipeBuilder()
    .input(item('minecraft:clay') * 3)
    .fluidInput(fluid('water') * 100)
    .fluidOutput(fluid('kerosene') * 100)
    .pressure(4)
    .requiredTemperature(323)
    .register()

mods.pneumaticcraft.thermopneumatic_processing_plant.recipeBuilder()
    .fluidInput(fluid('water') * 100)
    .fluidOutput(fluid('lava') * 100)
    .pressure(4)
    .requiredTemperature(323)
    .register()


// XP Fluid:
// Controls what fluids are considered XP Fluids and how much experience they provide.

// mods.pneumaticcraft.xp_fluid.remove(fluid('xpjuice'))
// mods.pneumaticcraft.xp_fluid.removeAll()

mods.pneumaticcraft.xp_fluid.recipeBuilder()
    .fluidInput(fluid('lava'))
    .ratio(5)
    .register()



