
// Auto generated groovyscript example file
// MODS_LOADED: prodigytech

println 'mod \'prodigytech\' detected, running script'

// groovyscript.wiki.prodigytech.solderer.title:
// groovyscript.wiki.prodigytech.solderer.description

mods.prodigytech.solderer.removeByAdditive(item('minecraft:iron_ingot'))
mods.prodigytech.solderer.removeByOutput(item('prodigytech:circuit_refined'))
mods.prodigytech.solderer.removeByPattern(item('prodigytech:pattern_circuit_refined'))
// mods.prodigytech.solderer.removeAll()
// mods.prodigytech.solderer.removeWithoutAdditive()

mods.prodigytech.solderer.recipeBuilder()
    .pattern(item('minecraft:clay'))
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .gold(5)
    .time(100)
    .register()

mods.prodigytech.solderer.recipeBuilder()
    .pattern(item('minecraft:coal_block'))
    .output(item('minecraft:nether_star'))
    .gold(75)
    .register()


