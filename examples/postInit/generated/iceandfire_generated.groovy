
// Auto generated groovyscript example file
// MODS_LOADED: iceandfire

log 'mod \'iceandfire\' detected, running script'

// Fire Dragonforge:
// Converts two input itemstacks into an output itemstack in a multiblock Dragonforge Fire Multiblock while there is a
// stage 3+ Fire Dragon nearby.

mods.iceandfire.fire_forge.removeByInput(item('minecraft:iron_ingot'))
// mods.iceandfire.fire_forge.removeByInput(item('iceandfire:fire_dragon_blood'))
// mods.iceandfire.fire_forge.removeByOutput(item('iceandfire:dragonsteel_fire_ingot'))
// mods.iceandfire.fire_forge.removeAll()

mods.iceandfire.fire_forge.recipeBuilder()
    .input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()

mods.iceandfire.fire_forge.recipeBuilder()
    .input(item('minecraft:diamond'), item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .register()


// Ice Dragonforge:
// Converts two input itemstacks into an output itemstack in a multiblock Dragonforge Ice Multiblock while there is a stage
// 3+ Ice Dragon nearby.

mods.iceandfire.ice_forge.removeByInput(item('minecraft:iron_ingot'))
// mods.iceandfire.ice_forge.removeByInput(item('iceandfire:ice_dragon_blood'))
// mods.iceandfire.ice_forge.removeByOutput(item('iceandfire:dragonsteel_ice_ingot'))
// mods.iceandfire.ice_forge.removeAll()

mods.iceandfire.ice_forge.recipeBuilder()
    .input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()

mods.iceandfire.ice_forge.recipeBuilder()
    .input(item('minecraft:diamond'), item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()


// Lightning Dragonforge:
// Converts two input itemstacks into an output itemstack in a multiblock Dragonforge Lightning Multiblock while there is a
// stage 3+ Lightning Dragon nearby.

// mods.iceandfire.lightning_forge.removeByInput(item('minecraft:iron_ingot'))
// mods.iceandfire.lightning_forge.removeByInput(item('iceandfire:lightning_dragon_blood'))
// mods.iceandfire.lightning_forge.removeByOutput(item('iceandfire:dragonsteel_lightning_ingot'))
// mods.iceandfire.lightning_forge.removeAll()

//mods.iceandfire.lightning_forge.recipeBuilder()
//    .input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot'))
//    .output(item('minecraft:clay'))
//    .register()

//mods.iceandfire.lightning_forge.recipeBuilder()
//    .input(item('minecraft:diamond'), item('minecraft:gold_ingot'))
//    .output(item('minecraft:clay'))
//    .register()
