
// Auto generated groovyscript example file
// MODS_LOADED: randomthings

log 'mod \'randomthings\' detected, running script'

// Anvil Crafting:
// Converts two itemstacks into an itemstack in the Vanilla Anvil.

mods.randomthings.anvil.removeByInput(item('randomthings:obsidianskull'))
mods.randomthings.anvil.removeByOutput(item('randomthings:lavawader'))
// mods.randomthings.anvil.removeAll()

mods.randomthings.anvil.recipeBuilder()
    .input(item('minecraft:diamond_sword'), item('minecraft:boat'))
    .output(item('minecraft:diamond'))
    .cost(1)
    .register()

mods.randomthings.anvil.recipeBuilder()
    .input(item('minecraft:iron_sword'), item('minecraft:boat'))
    .output(item('minecraft:gold_ingot') * 16)
    .cost(50)
    .register()


// Imbuing Station:
// Converts four itemstacks into an itemstack in the Random Things Imbuing Station.

mods.randomthings.imbuing.removeByInput(item('minecraft:coal'))
mods.randomthings.imbuing.removeByInput(item('minecraft:cobblestone'))
mods.randomthings.imbuing.removeByOutput(item('randomthings:imbue:3'))
// mods.randomthings.imbuing.removeAll()

mods.randomthings.imbuing.recipeBuilder()
    .mainInput(item('minecraft:clay'))
    .input(item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:gold_block'))
    .output(item('minecraft:diamond') * 8)
    .register()

mods.randomthings.imbuing.recipeBuilder()
    .mainInput(item('minecraft:diamond'))
    .input(item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot'))
    .register()
