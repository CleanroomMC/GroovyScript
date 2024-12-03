
// Auto generated groovyscript example file
// MODS_LOADED: silentgems

log.info 'mod \'silentgems\' detected, running script'

// Chaos Altar:
// Converts an input itemstack into an output itemstack with an optional catalyst, consuming a specified amount of Chaos
// from a Chaos Altar. Chaos is consumed at a maximum of 400 per tick, meaning the time taken corresponds to the Chaos
// cost.

mods.silentgems.chaos_altar.removeByCatalyst(item('minecraft:slime_ball'))
mods.silentgems.chaos_altar.removeByInput(item('silentgems:gem'))
mods.silentgems.chaos_altar.removeByOutput(item('silentgems:craftingmaterial'))
// mods.silentgems.chaos_altar.removeAll()

mods.silentgems.chaos_altar.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .cost(5)
    .register()

mods.silentgems.chaos_altar.recipeBuilder()
    .input(item('minecraft:gold_ingot') * 2)
    .output(item('minecraft:clay'))
    .catalyst(item('minecraft:diamond'))
    .cost(5000)
    .register()


