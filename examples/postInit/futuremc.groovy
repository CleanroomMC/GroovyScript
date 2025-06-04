
// Auto generated groovyscript example file
// MODS_LOADED: futuremc

log.info 'mod \'futuremc\' detected, running script'

// Blast Furnace:
// Converts an input itemstack into an output itemstack at the cost of burnable fuel.

mods.futuremc.blast_furnace.removeByInput(item('minecraft:gold_ore'))
mods.futuremc.blast_furnace.removeByOutput(item('minecraft:iron_ingot'))
// mods.futuremc.blast_furnace.removeAll()

mods.futuremc.blast_furnace.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.futuremc.blast_furnace.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()


// Campfire:
// Converts an input itemstack into an output itemstack when placed on the Campfire.

mods.futuremc.campfire.removeByInput(item('minecraft:fish'))
mods.futuremc.campfire.removeByOutput(item('minecraft:cooked_mutton'))
// mods.futuremc.campfire.removeAll()

mods.futuremc.campfire.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .duration(10)
    .register()

mods.futuremc.campfire.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .duration(1)
    .register()


// Composter:
// Converts input items into a chance to get a layer of compost, with 8 layers providing a single bonemeal.

mods.futuremc.composter.removeByInput(item('minecraft:cactus'))
// mods.futuremc.composter.removeAll()

mods.futuremc.composter.recipeBuilder()
    .input(item('minecraft:clay'))
    .chance(100)
    .register()

mods.futuremc.composter.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .chance(30)
    .register()


// Smithing:
// Converts two input itemstacks into an output output itemstack in the Smithing Table.

mods.futuremc.smithing.removeByInput(item('minecraft:diamond_hoe'))
// mods.futuremc.smithing.removeByInput(item('futuremc:netherite_ingot'))
mods.futuremc.smithing.removeByOutput(item('futuremc:netherite_pickaxe'))
// mods.futuremc.smithing.removeAll()

mods.futuremc.smithing.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .register()

mods.futuremc.smithing.recipeBuilder()
    .input(item('minecraft:gold_ingot') * 4, item('minecraft:clay'))
    .output(item('minecraft:clay') * 8)
    .register()


// Smoker:
// Converts an input itemstack into an output itemstack at the cost of burnable fuel.

mods.futuremc.smoker.removeByInput(item('minecraft:porkchop'))
mods.futuremc.smoker.removeByOutput(item('minecraft:baked_potato'))
// mods.futuremc.smoker.removeAll()

mods.futuremc.smoker.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.futuremc.smoker.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()


// Stoenecutter:
// Converts an input itemstack into an output itemstack via selecting the desired output from the Stonecutter GUI.

mods.futuremc.stonecutter.removeByInput(item('minecraft:stonebrick'))
mods.futuremc.stonecutter.removeByOutput(item('minecraft:stone_slab'))
// mods.futuremc.stonecutter.removeAll()

mods.futuremc.stonecutter.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.futuremc.stonecutter.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()


