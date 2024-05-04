
// Auto generated groovyscript example file
// MODS_LOADED: projecte

println 'mod \'projecte\' detected, running script'

// Mob Entity Randomizer:
// Converts an entity on the list into a random other entity on the list when a projectile fired from the Philosopher's
// Stone hits it. This list contains hostile mob entities by default.

mods.projecte.entity_randomizer_mob.remove(entity('minecraft:zombie'))
// mods.projecte.entity_randomizer_mob.removeAll()

mods.projecte.entity_randomizer_mob.add(entity('minecraft:pig'))

// Peaceful Entity Randomizer:
// Converts an entity on the list into a random other entity on the list when a projectile fired from the Philosopher's
// Stone hits it. This list contains peaceful entities by default.

mods.projecte.entity_randomizer_peaceful.remove(entity('minecraft:pig'))
// mods.projecte.entity_randomizer_peaceful.removeAll()

mods.projecte.entity_randomizer_peaceful.add(entity('minecraft:zombie'))

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


