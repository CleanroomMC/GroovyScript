
// Auto generated groovyscript example file
// MODS_LOADED: cyclicmagic

log 'mod \'cyclicmagic\' detected, running script'

// DeHydrator:
// Converts an input itemstack into an output itemstack.

mods.cyclicmagic.dehydrator.removeByInput(item('minecraft:clay'))
mods.cyclicmagic.dehydrator.removeByOutput(item('minecraft:deadbush'))
// mods.cyclicmagic.dehydrator.removeAll()

mods.cyclicmagic.dehydrator.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()

mods.cyclicmagic.dehydrator.recipeBuilder()
    .input(ore('logWood'))
    .output(item('minecraft:clay') * 8)
    .time(100)
    .water(30)
    .register()


// Hydrator:
// Converts up to 4 input itemstacks and some amount of water into an output itemstack.

mods.cyclicmagic.hydrator.removeByInput(item('minecraft:dirt'))
mods.cyclicmagic.hydrator.removeByOutput(item('minecraft:clay_ball'))
// mods.cyclicmagic.hydrator.removeAll()

mods.cyclicmagic.hydrator.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()

mods.cyclicmagic.hydrator.recipeBuilder()
    .input(ore('logWood'), ore('sand'), ore('gravel'), item('minecraft:diamond'))
    .output(item('minecraft:clay') * 8)
    .water(100)
    .register()


// Melter:
// Converts up to 4 input itemstacks into an output itemstack, while being placed above lava.

mods.cyclicmagic.melter.removeByInput(item('minecraft:snow'))
mods.cyclicmagic.melter.removeByOutput(fluid('amber'))
// mods.cyclicmagic.melter.removeAll()

mods.cyclicmagic.melter.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .fluidOutput(fluid('water') * 175)
    .register()

mods.cyclicmagic.melter.recipeBuilder()
    .input(ore('logWood'), ore('sand'), ore('gravel'), item('minecraft:diamond'))
    .fluidOutput(fluid('lava') * 500)
    .register()


// Packager:
// Converts up to 6 input itemstacks into an output itemstack.

mods.cyclicmagic.packager.removeByInput(item('minecraft:grass'))
mods.cyclicmagic.packager.removeByOutput(item('minecraft:melon_block'))
// mods.cyclicmagic.packager.removeAll()

mods.cyclicmagic.packager.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()

mods.cyclicmagic.packager.recipeBuilder()
    .input(ore('logWood'), ore('sand'), ore('gravel'), item('minecraft:diamond'), item('minecraft:diamond_block'), item('minecraft:gold_block'))
    .output(item('minecraft:clay') * 4)
    .register()


// Solidifier:
// Converts up to 4 input itemstacks and an input fluidstack into an output itemstack.

mods.cyclicmagic.solidifier.removeByInput(fluid('water'))
mods.cyclicmagic.solidifier.removeByInput(item('minecraft:bucket'))
mods.cyclicmagic.solidifier.removeByOutput(item('cyclicmagic:crystallized_obsidian'))
// mods.cyclicmagic.solidifier.removeAll()

mods.cyclicmagic.solidifier.recipeBuilder()
    .input(item('minecraft:clay'))
    .fluidInput(fluid('water') * 175)
    .output(item('minecraft:gold_ingot') * 3)
    .register()

mods.cyclicmagic.solidifier.recipeBuilder()
    .input(ore('logWood'), ore('sand'), ore('gravel'), item('minecraft:diamond'))
    .fluidInput(fluid('lava') * 500)
    .output(item('minecraft:clay') * 2)
    .register()


