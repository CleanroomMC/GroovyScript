
// Auto generated groovyscript example file
// MODS_LOADED: immersivepetroleum

log 'mod \'immersivepetroleum\' detected, running script'

// Distillation Tower:
// Converts an input fluidstack into any number of output fluidstacks and any number of output itemstacks, with each
// itemstack having the ability to have a custom chance, using energy and taking time.

mods.immersivepetroleum.distillation.removeByInput(fluid('oil'))
// mods.immersivepetroleum.distillation.removeByOutput(fluid('lubricant'))
// mods.immersivepetroleum.distillation.removeByOutput(item('immersivepetroleum:material'))
// mods.immersivepetroleum.distillation.removeAll()

mods.immersivepetroleum.distillation.recipeBuilder()
    .fluidInput(fluid('water') * 100)
    .fluidOutput(fluid('water') * 50, fluid('lava') * 30)
    .output(item('minecraft:diamond'), 0.5)
    .output(item('minecraft:clay'), 0.2)
    .output(item('minecraft:diamond'), 0.1)
    .output(item('minecraft:clay'), 0.5)
    .output(item('minecraft:diamond') * 5, 0.01)
    .time(5)
    .energy(1000)
    .register()

mods.immersivepetroleum.distillation.recipeBuilder()
    .fluidInput(fluid('lava') * 5)
    .output(item('minecraft:diamond'))
    .time(1)
    .register()


// Reservoir:
// Adds a Reservoir Type with the given name, weight, minimum size, maximum size, replenishment rate, allowed dimensions,
// and allowed biomes. A Reservoir Type can be extracted by an Pumpjack Multiblock and scanned via a Core Sample Drill.

mods.immersivepetroleum.reservoir.removeByName('aquifer')
mods.immersivepetroleum.reservoir.removeByOutput(fluid('oil'))
// mods.immersivepetroleum.reservoir.removeAll()

mods.immersivepetroleum.reservoir.recipeBuilder()
    .name('demo')
    .fluidOutput(fluid('water'))
    .weight(20000)
    .minSize(100)
    .maxSize(100)
    .dimension(0, 1)
    .biome('hot')
    .register()

mods.immersivepetroleum.reservoir.recipeBuilder()
    .name('demo')
    .fluidOutput(fluid('lava'))
    .weight(2000)
    .minSize(1000)
    .maxSize(5000)
    .replenishRate(100)
    .dimension(-1, 1)
    .dimensionBlacklist()
    .biome('cold')
    .biomeBlacklist()
    .register()
