
// Auto generated groovyscript example file
// MODS_LOADED: aether_legacy

println 'mod \'aether_legacy\' detected, running script'

// Accessory:
// The Aether Accessory system.

mods.aether_legacy.accessory.removeByInput(item('aether_legacy:iron_pendant'))
// mods.aether_legacy.accessory.removeAll()

mods.aether_legacy.accessory.recipeBuilder()
    .input(item('minecraft:shield'))
    .accessoryType('shield')
    .register()



// Enchanter:
// Enchanting is a mechanic used to create new items, as well as repair tools, armor, and weapons, using the Altar block.

mods.aether_legacy.enchanter.removeByOutput(item('aether_legacy:enchanted_gravitite'))
// mods.aether_legacy.enchanter.removeAll()

mods.aether_legacy.enchanter.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .time(200)
    .register()



// Enchanter Fuel:
// By default, the Enchantar (Altar) takes Ambrosium Shards as fuel. Using GroovyScript, custom fuels can be added.

mods.aether_legacy.enchanter_fuel.removeByItem(item('aether_legacy:ambrosium_shard'))
// mods.aether_legacy.enchanter_fuel.removeAll()

mods.aether_legacy.enchanter_fuel.add(item('minecraft:blaze_rod'), 1000)

// Freezer:
// The Freezer is used to turn certain items into frozen versions.

mods.aether_legacy.freezer.removeByOutput(item('minecraft:obsidian'))
// mods.aether_legacy.freezer.removeAll()

mods.aether_legacy.freezer.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:dirt'))
    .time(200)
    .register()



// Freezer:
// By default, the Freezer takes Icestone as fuel. Using GroovyScript, custom fuels can be added.

mods.aether_legacy.freezer_fuel.removeByItem(item('aether_legacy:icestone'))
// mods.aether_legacy.freezer_fuel.removeAll()

mods.aether_legacy.freezer_fuel.add(item('minecraft:packed_ice'), 1000)

