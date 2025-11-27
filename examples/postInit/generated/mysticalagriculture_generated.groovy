
// Auto generated groovyscript example file
// MODS_LOADED: mysticalagriculture

log 'mod \'mysticalagriculture\' detected, running script'

// Seed Reprocessor:
// Converts an input itemstack into an output itemstack, taking a set amount of time based on the machine and consuming
// fuel.

mods.mysticalagriculture.reprocessor.removeByInput(item('mysticalagriculture:stone_seeds'))
mods.mysticalagriculture.reprocessor.removeByOutput(item('mysticalagriculture:dirt_essence'))
// mods.mysticalagriculture.reprocessor.removeAll()

mods.mysticalagriculture.reprocessor.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 3)
    .register()

mods.mysticalagriculture.reprocessor.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot'))
    .register()
