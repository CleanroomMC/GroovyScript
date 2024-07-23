
// Auto generated groovyscript example file
// MODS_LOADED: projecte

log.info 'mod \'projecte\' detected, running script'

// Entity Randomizer:
// Converts an entity on the list into a random other entity on the list when a projectile fired from the Philosopher's
// Stone hits it. There are two lists, one for 'mobs' and the other for 'peacefuls', but any entity can go on either list.

mods.projecte.entity_randomizer.removeMob(entity('minecraft:zombie'))
mods.projecte.entity_randomizer.removePeaceful(entity('minecraft:pig'))
// mods.projecte.entity_randomizer.removeAll()
// mods.projecte.entity_randomizer.removeAllMobs()
// mods.projecte.entity_randomizer.removeAllPeacefuls()

mods.projecte.entity_randomizer.addMob(entity('minecraft:pig'))
mods.projecte.entity_randomizer.addPeaceful(entity('minecraft:zombie'))

// World Transmutation:
// Converts an input blockstate into an output blockstate when right-clicked with by a Philosopher's Stone, with the abity
// to be converted into a different output blockstate when holding shift.

mods.projecte.transmutation.removeByInput(blockstate('minecraft:wool'))
mods.projecte.transmutation.removeByOutput(blockstate('minecraft:dirt'))
// mods.projecte.transmutation.removeAll()

mods.projecte.transmutation.recipeBuilder()
    .input(blockstate('minecraft:end_stone'))
    .output(blockstate('minecraft:diamond_block'), blockstate('minecraft:gold_block'))
    .register()

mods.projecte.transmutation.recipeBuilder()
    .input(blockstate('minecraft:diamond_block'))
    .output(blockstate('minecraft:end_stone'))
    .altOutput(blockstate('minecraft:gold_block'))
    .register()

mods.projecte.transmutation.recipeBuilder()
    .input(blockstate('minecraft:gold_block'))
    .output(blockstate('minecraft:diamond_block'))
    .register()


