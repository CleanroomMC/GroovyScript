// MODS_LOADED: aether_legacy

println 'mod \'aether_legacy\' detected, running script'

mods.aether_legacy.enchanter.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .time(200)
    .register()


mods.aether_legacy.enchanter.removeByOutput(item('aether_legacy:enchanted_gravitite'))

mods.aether_legacy.enchanterFuel.add(item('minecraft:blaze_rod'), 1000)

mods.aether_legacy.freezer.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:dirt'))
    .time(200)
    .register()


mods.aether_legacy.freezer.removeByOutput(item('minecraft:obsidian'))

mods.aether_legacy.freezerFuel.add(item('minecraft:packed_ice'), 1000)

mods.aether_legacy.accessory.recipeBuilder()
    .input(item('minecraft:shield'))
    .accessoryType("shield")
    .register()