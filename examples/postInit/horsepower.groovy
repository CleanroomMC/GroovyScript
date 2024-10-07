
// Auto generated groovyscript example file
// MODS_LOADED: horsepower

log.info 'mod \'horsepower\' detected, running script'

// Horse Chopping Block:
// Converts an itemstack input into an itemstack output, with the chance of an additional output after a configurable
// amount of processing has been done. Depending on if the config option `Separate Chopping Recipes` is true, this may
// affect both Horse and Hand Chopping Blocks. Only the Horse Chopping Block can produce secondary outputs.

mods.horsepower.chopping_block.removeByInput(item('minecraft:log:3'))
mods.horsepower.chopping_block.removeByOutput(item('minecraft:planks:4'))
// mods.horsepower.chopping_block.removeAll()

mods.horsepower.chopping_block.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 5)
    .time(5)
    .register()

mods.horsepower.chopping_block.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot'))
    .time(1)
    .register()

mods.horsepower.chopping_block.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'), item('minecraft:diamond'))
    .chance(50)
    .time(2)
    .register()


// Horse Grindstone:
// Converts an itemstack input into an itemstack output, with the chance of an additional output after a configurable
// amount of processing has been done. Depending on if the config option `Separate Grindstone Recipes` is true, this may
// affect both Horse and Hand Grindstones.

mods.horsepower.grindstone.removeByInput(item('minecraft:double_plant:4'))
mods.horsepower.grindstone.removeByOutput(item('minecraft:sugar'))
// mods.horsepower.grindstone.removeAll()

mods.horsepower.grindstone.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 5)
    .time(5)
    .register()

mods.horsepower.grindstone.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot'))
    .time(1)
    .register()

mods.horsepower.grindstone.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'), item('minecraft:diamond'))
    .chance(50)
    .time(2)
    .register()


// Manual Chopping Block:
// Converts an itemstack input into an itemstack output after a configurable amount of processing has been done. Depending
// on if the config option `Separate Chopping Recipes` is true, this may affect both Horse and Hand Chopping Blocks.

mods.horsepower.manual_chopping_block.removeByInput(item('minecraft:log:3'))
mods.horsepower.manual_chopping_block.removeByOutput(item('minecraft:planks:4'))
// mods.horsepower.manual_chopping_block.removeAll()

mods.horsepower.manual_chopping_block.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 5)
    .time(5)
    .register()

mods.horsepower.manual_chopping_block.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot'))
    .time(1)
    .register()


// Manual Grindstone:
// Converts an itemstack input into an itemstack output, with the chance of an additional output after a configurable
// amount of processing has been done. Depending on if the config option `Separate Grindstone Recipes` is true, this may
// affect both Horse and Hand Grindstones.

mods.horsepower.manual_grindstone.removeByInput(item('minecraft:double_plant:4'))
mods.horsepower.manual_grindstone.removeByOutput(item('minecraft:sugar'))
// mods.horsepower.manual_grindstone.removeAll()

mods.horsepower.manual_grindstone.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 5)
    .time(5)
    .register()

mods.horsepower.manual_grindstone.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot'))
    .time(1)
    .register()

mods.horsepower.manual_grindstone.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'), item('minecraft:diamond'))
    .chance(50)
    .time(2)
    .register()


// Horse Press:
// Converts an itemstack into another itemstack or a fluidstack by a horse running laps.

mods.horsepower.press.removeByInput(item('minecraft:wheat_seeds'))
mods.horsepower.press.removeByOutput(fluid('water'))
// mods.horsepower.press.removeByOutput(item('minecraft:dirt'))
// mods.horsepower.press.removeAll()

mods.horsepower.press.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 5)
    .register()

mods.horsepower.press.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidOutput(fluid('lava') * 500)
    .register()


