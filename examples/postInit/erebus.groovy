
// Auto generated groovyscript example file
// MODS_LOADED: erebus

log.info 'mod \'erebus\' detected, running script'

// Organic Composter:
// Converts valid items into compost. The Blacklist blocks all ItemStacks on it from being used, the Material list allows
// any Blocks using the valid Materials to be converted, and the Registry contains any valid ItemStacks. The conversion
// takes 10 seconds, and is fueled by erebus wall plants.

// mods.erebus.composter.removeFromBlacklist(item('erebus:wall_plants', 1))
mods.erebus.composter.removeFromMaterial(blockmaterial('sponge'))
mods.erebus.composter.removeFromRegistry(item('minecraft:stick'))
// mods.erebus.composter.removeAllFromBlacklist()
// mods.erebus.composter.removeAllFromMaterial()
// mods.erebus.composter.removeAllFromRegistry()

mods.erebus.composter.addBlacklist(item('erebus:wall_plants', 7))
mods.erebus.composter.addBlacklist(item('erebus:wall_plants_cultivated', 7))
mods.erebus.composter.addMaterial(blockmaterial('tnt'))
mods.erebus.composter.addRegistry(item('minecraft:gold_ingot'))

// Offering Altar:
// Converts up to 3 input itemstacks into an output itemstack.

// mods.erebus.offering_altar.removeByInput(item('minecraft:emerald'))
mods.erebus.offering_altar.removeByOutput(item('erebus:materials', 38))
// mods.erebus.offering_altar.removeAll()

mods.erebus.offering_altar.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()

mods.erebus.offering_altar.recipeBuilder()
    .input(item('minecraft:stone'), ore('gemDiamond'), item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .register()


// Smoothie-matic:
// Converts a container item, up to 4 input items, and up to 4 input fluidstacks into an output itemstack.

mods.erebus.smoothie.removeByContainer(item('minecraft:bucket'))
mods.erebus.smoothie.removeByInput(fluid('honey'))
mods.erebus.smoothie.removeByInput(item('erebus:materials', 18))
mods.erebus.smoothie.removeByOutput(item('erebus:materials', 21))
// mods.erebus.smoothie.removeAll()

mods.erebus.smoothie.recipeBuilder()
    .container(item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot'))
    .register()

mods.erebus.smoothie.recipeBuilder()
    .container(item('minecraft:clay'))
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .register()

mods.erebus.smoothie.recipeBuilder()
    .container(item('minecraft:gold_block'))
    .fluidInput(fluid('water') * 5000)
    .output(item('minecraft:gold_ingot'))
    .register()

mods.erebus.smoothie.recipeBuilder()
    .container(item('minecraft:stone'))
    .input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'))
    .fluidInput(fluid('lava') * 500, fluid('formic_acid') * 500, fluid('honey') * 500, fluid('milk') * 500)
    .output(item('minecraft:clay') * 5)
    .register()


