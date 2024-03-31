
// Auto generated groovyscript example file
// MODS_LOADED: alchemistry

println 'mod \'alchemistry\' detected, running script'

// Atomizer:
// Converts a non-element into its component elements.

mods.alchemistry.atomizer.removeByInput(fluid('water'))
// mods.alchemistry.atomizer.removeByOutput(item('alchemistry:compound:7'))
// mods.alchemistry.atomizer.removeAll()

mods.alchemistry.atomizer.recipeBuilder()
    .fluidInput(fluid('water') * 125)
    .output(item('minecraft:clay'))
    .register()

mods.alchemistry.atomizer.recipeBuilder()
    .fluidInput(fluid('lava') * 500)
    .output(item('minecraft:gold_ingot'))
    .reversible()
    .register()



// Chemical Combiner:
// Converts up to 9 input itemstacks into an output itemstack.

mods.alchemistry.combiner.removeByInput(element('carbon'))
mods.alchemistry.combiner.removeByOutput(item('minecraft:glowstone'))
// mods.alchemistry.combiner.removeAll()

mods.alchemistry.combiner.recipeBuilder()
    .input(item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2)
    .output(item('minecraft:gold_block') * 2)
    .register()

mods.alchemistry.combiner.recipeBuilder()
    .input(ItemStack.EMPTY, ItemStack.EMPTY, item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .register()


// Chemical Dissolver:
// Converts an input itemstack into any number of output itemstacks, divided in any manner between different chances, with
// the ability to run multiple rolls to produce additional outputs.

mods.alchemistry.dissolver.removeByInput(item('alchemistry:compound:1'))
// mods.alchemistry.dissolver.removeAll()

mods.alchemistry.dissolver.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .probabilityOutput(item('minecraft:clay'))
    .reversible()
    .rolls(1)
    .register()

mods.alchemistry.dissolver.recipeBuilder()
    .input(item('minecraft:diamond'))
    .probabilityOutput(30, item('minecraft:clay'))
    .probabilityOutput(30, item('minecraft:clay'))
    .probabilityOutput(30, item('minecraft:clay'))
    .rolls(10)
    .register()


// Electrolyzer:
// Converts an input fluidstack into up to 4 output itemstacks, with the 3rd and 4th output itemstacks being able to have
// chances applied to them. May require a catalyst input itemstack, which may also have a chance to be consumed.

// mods.alchemistry.electrolyzer.removeByInput(fluid('water'))
mods.alchemistry.electrolyzer.removeByInput(element('calcium_carbonate'))
mods.alchemistry.electrolyzer.removeByOutput(element('chlorine'))
// mods.alchemistry.electrolyzer.removeAll()

mods.alchemistry.electrolyzer.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .output(item('minecraft:clay'))
    .register()

mods.alchemistry.electrolyzer.recipeBuilder()
    .fluidInput(fluid('water') * 100)
    .input(item('minecraft:gold_ingot'))
    .consumptionChance(100)
    .output(item('minecraft:gold_nugget') * 4)
    .output(item('minecraft:gold_nugget') * 4)
    .output(item('minecraft:gold_nugget') * 4)
    .output(item('minecraft:gold_nugget') * 4)
    .chance(50)
    .chance(50)
    .register()


// Evaporator:
// Converts an input fluidstack into an output fluidstack, taking a set amount of time.

mods.alchemistry.evaporator.removeByInput(fluid('lava'))
mods.alchemistry.evaporator.removeByOutput(item('alchemistry:mineral_salt'))
// mods.alchemistry.evaporator.removeAll()

mods.alchemistry.evaporator.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .output(item('minecraft:redstone') * 8)
    .register()

mods.alchemistry.evaporator.recipeBuilder()
    .fluidInput(fluid('water') * 10)
    .output(item('minecraft:clay'))
    .register()



// Liquifier:
// Converts an input itemstack into an output fluidstack, consuming a set amount of power.

mods.alchemistry.liquifier.removeByInput(element('water'))
// mods.alchemistry.liquifier.removeByOutput(fluid('water'))
// mods.alchemistry.liquifier.removeAll()

mods.alchemistry.liquifier.recipeBuilder()
    .input(element('carbon') * 32)
    .fluidOutput(fluid('water') * 1000)
    .register()

mods.alchemistry.liquifier.recipeBuilder()
    .input(item('minecraft:magma'))
    .fluidOutput(fluid('lava') * 750)
    .register()



