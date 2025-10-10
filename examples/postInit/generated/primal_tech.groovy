
// Auto generated groovyscript example file
// MODS_LOADED: primal_tech

log 'mod \'primal_tech\' detected, running script'

// Clay Kiln:
// Converts an input item into an output itemstack after a given amount of time. Requires the block below to be Minecraft
// Fire or a Primal Tech Flame Grilled Wopper. Takes some time to heat up before beginning to smelt items.

mods.primal_tech.clay_kiln.removeByInput(item('minecraft:gravel'))
mods.primal_tech.clay_kiln.removeByOutput(item('primal_tech:charcoal_block'))
// mods.primal_tech.clay_kiln.removeAll()

mods.primal_tech.clay_kiln.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .cookTime(50)
    .register()

mods.primal_tech.clay_kiln.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond') * 4)
    .cookTime(100)
    .register()


// Stone Anvil:
// Converts an input item into an output itemstack after being interacted with by a player using a Stone Mallet.

// mods.primal_tech.stone_anvil.removeByInput(item('primal_tech:flint_block'))
mods.primal_tech.stone_anvil.removeByOutput(item('minecraft:flint'))
// mods.primal_tech.stone_anvil.removeAll()

mods.primal_tech.stone_anvil.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .register()

mods.primal_tech.stone_anvil.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond') * 4)
    .register()


// Water Saw:
// Converts an input item into an output itemstack after a given amount of time. Requires the block below it to be a water
// source block, and the block below and behind it flowing water.

mods.primal_tech.water_saw.removeByInput(item('minecraft:log'))
mods.primal_tech.water_saw.removeByOutput(item('minecraft:planks:1'))
// mods.primal_tech.water_saw.removeAll()

mods.primal_tech.water_saw.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .choppingTime(50)
    .register()

mods.primal_tech.water_saw.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond') * 4)
    .choppingTime(100)
    .register()


// Wooden Basin:
// Converts up to 4 items and 1000mb of fluid into an output itemstack.

mods.primal_tech.wooden_basin.removeByInput(fluid('lava'))
// mods.primal_tech.wooden_basin.removeByInput(item('minecraft:cobblestone'))
// mods.primal_tech.wooden_basin.removeByOutput(item('minecraft:obsidian'))
// mods.primal_tech.wooden_basin.removeAll()

mods.primal_tech.wooden_basin.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidInput(fluid('lava'))
    .output(item('minecraft:clay'))
    .register()

mods.primal_tech.wooden_basin.recipeBuilder()
    .input(item('minecraft:gold_ingot'), item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:clay'))
    .fluidInput(fluid('water'))
    .output(item('minecraft:diamond') * 4)
    .register()


