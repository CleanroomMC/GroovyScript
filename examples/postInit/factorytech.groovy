
// Auto generated groovyscript example file
// MODS_LOADED: factorytech

log.info 'mod \'factorytech\' detected, running script'

// Fluid Agitator:
// Converts either one or two input fluidstacks and up to one input itemstack into an output itemstack, output fluidstack,
// or both.

mods.factorytech.agitator.removeByInput(fluid('lava'))
mods.factorytech.agitator.removeByInput(fluid('ftglowstone'))
mods.factorytech.agitator.removeByInput(item('minecraft:sand'))
mods.factorytech.agitator.removeByOutput(fluid('h2so4'))
mods.factorytech.agitator.removeByOutput(item('minecraft:stone'))
// mods.factorytech.agitator.removeAll()

mods.factorytech.agitator.recipeBuilder()
    .input(item('minecraft:clay'))
    .fluidInput(fluid('water') * 100)
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.agitator.recipeBuilder()
    .fluidInput(fluid('ftglowstone') * 100)
    .output(item('minecraft:clay'))
    .register()

mods.factorytech.agitator.recipeBuilder()
    .fluidInput(fluid('lava') * 100, fluid('water') * 100)
    .output(item('minecraft:clay'))
    .register()

mods.factorytech.agitator.recipeBuilder()
    .fluidInput(fluid('lava') * 100, fluid('ftglowstone') * 100)
    .fluidOutput(fluid('ftglowstone') * 100)
    .register()

mods.factorytech.agitator.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .fluidInput(fluid('water') * 100)
    .output(item('minecraft:diamond') * 5)
    .register()


// Centrifuge:
// Converts an input itemstack into up to 3 output itemstacks, with the ability to control if stone parts are allowed.

mods.factorytech.centrifuge.removeByInput(item('minecraft:gravel'))
mods.factorytech.centrifuge.removeByOutput(item('minecraft:iron_nugget'))
// mods.factorytech.centrifuge.removeAll()

mods.factorytech.centrifuge.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:obsidian'), item('minecraft:gold_ingot') * 2, item('minecraft:diamond'))
    .register()

mods.factorytech.centrifuge.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()


// Circuit Scribe:
// Converts an input itemstack into an output itemstack, with the ability to control if stone parts are allowed.

mods.factorytech.circuit_scribe.removeByInput(item('factorytech:circuit_intermediate:8'))
// mods.factorytech.circuit_scribe.removeByOutput(item('factorytech:circuit_intermediate:8'))
// mods.factorytech.circuit_scribe.removeAll()

mods.factorytech.circuit_scribe.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.circuit_scribe.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .allowStoneParts()
    .register()


// Compression Chamber:
// Converts an input itemstack and input fluidstack into an output itemstack.

mods.factorytech.compressor.removeByInput(fluid('water'))
mods.factorytech.compressor.removeByInput(item('factorytech:machinepart:60'))
mods.factorytech.compressor.removeByOutput(item('factorytech:machinepart:141'))
// mods.factorytech.compressor.removeAll()

mods.factorytech.compressor.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.compressor.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidInput(fluid('lava') * 100)
    .output(item('minecraft:clay'))
    .register()

mods.factorytech.compressor.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .fluidInput(fluid('water') * 100)
    .output(item('minecraft:diamond') * 5)
    .register()


// Crucible:
// Converts an input itemstack into an output fluidstack.

mods.factorytech.crucible.removeByInput(item('minecraft:ice'))
mods.factorytech.crucible.removeByOutput(fluid('lava'))
// mods.factorytech.crucible.removeAll()

mods.factorytech.crucible.recipeBuilder()
    .input(item('minecraft:clay'))
    .fluidOutput(fluid('water'))
    .register()

mods.factorytech.crucible.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .fluidOutput(fluid('lava') * 30)
    .register()


// Terraneous Extractor:
// Passively generates resources when placed at or below y 8 and.

mods.factorytech.deep_drill.removeByInput(item('minecraft:gold_ore'))
// mods.factorytech.deep_drill.removeAll()

mods.factorytech.deep_drill.recipeBuilder()
    .output(item('minecraft:diamond'))
    .weight(10)
    .register()

mods.factorytech.deep_drill.recipeBuilder()
    .output(item('minecraft:clay'))
    .weight(30)
    .register()


// Mob Disassembler:
// Kills an entity in-world, dropping the mob's normal loot in addition to custom loot.

mods.factorytech.disassembler.removeByEntity(entity('minecraft:creeper'))
mods.factorytech.disassembler.removeByOutput(item('minecraft:rotten_flesh'))
// mods.factorytech.disassembler.removeAll()

mods.factorytech.disassembler.recipeBuilder()
    .entity(entity('minecraft:chicken'))
    .output(item('minecraft:obsidian'), item('minecraft:gold_ingot') * 2, item('minecraft:clay'), item('minecraft:diamond'))
    .register()

mods.factorytech.disassembler.recipeBuilder()
    .entity(entity('minecraft:rabbit'))
    .output(item('minecraft:clay'), item('minecraft:diamond') * 2)
    .register()


// Electroplater:
// Converts an input itemstack into an output itemstack, with the ability to control if stone parts are allowed.

mods.factorytech.electroplater.removeByInput(item('minecraft:sand'))
mods.factorytech.electroplater.removeByOutput(item('minecraft:gold_ingot'))
// mods.factorytech.electroplater.removeAll()

mods.factorytech.electroplater.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.electroplater.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .allowStoneParts()
    .register()


// Grindstone:
// Converts an input itemstack into an output itemstack, with the ability to control if stone parts are allowed.

mods.factorytech.grindstone.removeByInput(item('minecraft:stone'))
mods.factorytech.grindstone.removeByOutput(item('factorytech:machinepart:1'))
// mods.factorytech.grindstone.removeAll()

mods.factorytech.grindstone.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.grindstone.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .allowStoneParts()
    .register()


// Electric Furnace:
// Converts an input itemstack into an output itemstack, with the ability to control if stone parts are allowed.

mods.factorytech.high_tech_furnace.removeByInput(item('minecraft:cactus'))
mods.factorytech.high_tech_furnace.removeByOutput(item('minecraft:iron_ingot'))
// mods.factorytech.high_tech_furnace.removeAll()

mods.factorytech.high_tech_furnace.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.high_tech_furnace.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .allowStoneParts()
    .register()


// Magnet Centrifuge:
// Converts an input itemstack into up to 3 output itemstacks, with the ability to control if stone parts are allowed.

mods.factorytech.magnet_centrifuge.removeByInput(item('minecraft:gravel'))
mods.factorytech.magnet_centrifuge.removeByOutput(item('minecraft:redstone'))
// mods.factorytech.magnet_centrifuge.removeAll()

mods.factorytech.magnet_centrifuge.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:obsidian'), item('minecraft:gold_ingot') * 2, item('minecraft:diamond'))
    .register()

mods.factorytech.magnet_centrifuge.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .allowStoneParts()
    .register()


// Magnetizer:
// Converts an input itemstack into an output itemstack, with the ability to control if stone parts are allowed.

mods.factorytech.magnetizer.removeByInput(item('minecraft:iron_ingot'))
// mods.factorytech.magnetizer.removeByOutput(item('factorytech:machinepart:130'))
// mods.factorytech.magnetizer.removeAll()

mods.factorytech.magnetizer.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.magnetizer.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .allowStoneParts()
    .register()


// Metal Cutter:
// Converts an input itemstack into an output itemstack, with the ability to control if stone parts are allowed.

mods.factorytech.metal_cutter.removeByInput(item('minecraft:gold_ingot'))
mods.factorytech.metal_cutter.removeByOutput(item('factorytech:machinepart:20'))
// mods.factorytech.metal_cutter.removeAll()

mods.factorytech.metal_cutter.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.metal_cutter.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .allowStoneParts()
    .register()


// Drill Grinder:
// Converts an input itemstack into an output itemstack, with the ability to control if stone parts are allowed.

mods.factorytech.ore_drill.removeByInput(item('minecraft:gold_ore'))
mods.factorytech.ore_drill.removeByOutput(item('minecraft:sand'))
// mods.factorytech.ore_drill.removeAll()

mods.factorytech.ore_drill.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.ore_drill.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .allowStoneParts()
    .register()


// Scrap Furnace:
// Converts an input itemstack into an output itemstack, with the ability to control if stone parts are allowed.

mods.factorytech.reclaimer.removeByInput(item('factorytech:salvagepart:22'))
mods.factorytech.reclaimer.removeByOutput(item('minecraft:iron_nugget'))
// mods.factorytech.reclaimer.removeAll()

mods.factorytech.reclaimer.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.reclaimer.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .allowStoneParts()
    .register()


// Refrigerator:
// Converts an input fluidstack into an output itemstack.

mods.factorytech.refrigerator.removeByInput(fluid('water'))
mods.factorytech.refrigerator.removeByOutput(item('minecraft:obsidian'))
// mods.factorytech.refrigerator.removeAll()

mods.factorytech.refrigerator.recipeBuilder()
    .fluidInput(fluid('water') * 100)
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.refrigerator.recipeBuilder()
    .fluidInput(fluid('lava') * 30)
    .output(item('minecraft:clay'))
    .register()


// River Grate:
// Slowly produces the output entries while in a river biome surrounded by water, and between y 60 and 70.

mods.factorytech.river_grate.removeByOutput(item('minecraft:fish'))
// mods.factorytech.river_grate.removeAll()

mods.factorytech.river_grate.recipeBuilder()
    .output(item('minecraft:diamond'))
    .weight(10)
    .register()

mods.factorytech.river_grate.recipeBuilder()
    .output(item('minecraft:clay'))
    .allowStoneParts()
    .weight(30)
    .register()


// Chop Saw:
// Converts an input itemstack into an output itemstack, with the ability to control if stone parts are allowed.

mods.factorytech.saw.removeByInput(item('minecraft:log'))
mods.factorytech.saw.removeByOutput(item('minecraft:stick'))
// mods.factorytech.saw.removeAll()

mods.factorytech.saw.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.saw.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .allowStoneParts()
    .register()


// Tempering Oven:
// Converts an input itemstack into an output itemstack, with the ability to control if stone parts are allowed and how
// long the recipe takes.

mods.factorytech.temperer.removeByInput(item('minecraft:iron_ingot'))
mods.factorytech.temperer.removeByOutput(item('factorytech:machinepart:4'))
// mods.factorytech.temperer.removeAll()

mods.factorytech.temperer.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.factorytech.temperer.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .time(30)
    .register()


