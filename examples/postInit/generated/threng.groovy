
// Auto generated groovyscript example file
// MODS_LOADED: threng

log 'mod \'threng\' detected, running script'

// Fluix Aggregation:
// Converts up to 3 input itemstacks into an output itemstack.

mods.threng.aggregator.removeByInput(item('appliedenergistics2:material:45'))
mods.threng.aggregator.removeByOutput(item('appliedenergistics2:material:7'))
// mods.threng.aggregator.removeAll()

mods.threng.aggregator.recipeBuilder()
    .input(ore('blockGlass'), item('minecraft:diamond'))
    .output(item('minecraft:diamond') * 4)
    .register()

mods.threng.aggregator.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .register()


// Pulse Centrifuge:
// Converts 1 input itemstack into an output itemstack.

mods.threng.centrifuge.removeByInput(item('appliedenergistics2:material'))
mods.threng.centrifuge.removeByOutput(item('appliedenergistics2:material:4'))
// mods.threng.centrifuge.removeAll()

mods.threng.centrifuge.recipeBuilder()
    .input(ore('blockGlass'))
    .output(item('minecraft:diamond'))
    .register()

mods.threng.centrifuge.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .register()


// Crystal Energization:
// Converts 1 input itemstack into an output itemstack, consuming a set amount of energy.

// mods.threng.energizer.removeByInput(item('appliedenergistics2:material'))
mods.threng.energizer.removeByOutput(item('appliedenergistics2:material:1'))
// mods.threng.energizer.removeAll()

mods.threng.energizer.recipeBuilder()
    .input(ore('blockGlass'))
    .energy(50)
    .output(item('minecraft:diamond'))
    .register()

mods.threng.energizer.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .energy(10000)
    .output(item('minecraft:diamond'))
    .register()


// ME Circuit Etching:
// Converts up to 3 input itemstacks from specific slots into an output itemstack.

mods.threng.etcher.removeByInput(item('minecraft:diamond'))
mods.threng.etcher.removeByOutput(item('appliedenergistics2:material:22'))
// mods.threng.etcher.removeAll()

mods.threng.etcher.recipeBuilder()
    .input(ore('blockGlass'))
    .top(item('minecraft:diamond'))
    .bottom(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 5)
    .register()

mods.threng.etcher.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .register()


