
// Auto generated groovyscript example file
// MODS_LOADED: actuallyadditions

println 'mod \'actuallyadditions\' detected, running script'

// Atomic Reconstructor:
// The Atomic Reconstructor is a block which uses energy to convert a block or item in front of it into other items.

mods.actuallyadditions.atomic_reconstructor.removeByInput(item('minecraft:diamond'))
mods.actuallyadditions.atomic_reconstructor.removeByOutput(item('actuallyadditions:block_crystal'))
// mods.actuallyadditions.atomic_reconstructor.removeAll()

mods.actuallyadditions.atomic_reconstructor.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .energyUse(1000)
    .register()

mods.actuallyadditions.atomic_reconstructor.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .energy(1000)
    .register()

mods.actuallyadditions.atomic_reconstructor.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 2)
    .register()


// Ball of Fur:
// A weighted itemstack output for using a Ball of Fur, dropped by a cat.

mods.actuallyadditions.ball_of_fur.removeByOutput(item('minecraft:feather'))
// mods.actuallyadditions.ball_of_fur.removeAll()

mods.actuallyadditions.ball_of_fur.recipeBuilder()
    .output(item('minecraft:clay') * 32)
    .weight(15)
    .register()


// Compost:
// Converts an input item into an output item after 150 seconds. Requires an input and output display blockstate.

mods.actuallyadditions.compost.removeByInput(item('actuallyadditions:item_canola_seed'))
mods.actuallyadditions.compost.removeByOutput(item('actuallyadditions:item_fertilizer'))
// mods.actuallyadditions.compost.removeAll()

mods.actuallyadditions.compost.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .inputDisplay(blockstate('minecraft:clay'))
    .outputDisplay(blockstate('minecraft:diamond_block'))
    .register()


// Crusher:
// Converts an input itemstack into an output itemstack with a chance of a second itemstack.

mods.actuallyadditions.crusher.removeByInput(item('minecraft:bone'))
mods.actuallyadditions.crusher.removeByOutput(item('minecraft:sugar'))
// mods.actuallyadditions.crusher.removeAll()

mods.actuallyadditions.crusher.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'), item('minecraft:diamond'))
    .chance(100)
    .register()

mods.actuallyadditions.crusher.recipeBuilder()
    .input(item('minecraft:diamond_block'))
    .output(item('minecraft:diamond') * 12)
    .register()


// Empowerer:
// Turns 5 input items into an output item at the cost of power and time. Has a configurable color.

mods.actuallyadditions.empowerer.removeByInput(item('actuallyadditions:item_crystal'))
mods.actuallyadditions.empowerer.removeByOutput(item('actuallyadditions:item_misc:24'))
// mods.actuallyadditions.empowerer.removeAll()

mods.actuallyadditions.empowerer.recipeBuilder()
    .mainInput(item('minecraft:clay'))
    .input(item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .time(50)
    .energy(1000)
    .red(0.5)
    .green(0.3)
    .blue(0.2)
    .register()

mods.actuallyadditions.empowerer.recipeBuilder()
    .mainInput(item('minecraft:clay'))
    .input(item('minecraft:diamond'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay'))
    .output(item('minecraft:diamond') * 2)
    .time(50)
    .color(0.5, 0.3, 0.2)
    .register()

mods.actuallyadditions.empowerer.recipeBuilder()
    .mainInput(item('minecraft:diamond'))
    .input(item('minecraft:diamond'),item('minecraft:gold_ingot'),item('minecraft:diamond'),item('minecraft:gold_ingot'))
    .output(item('minecraft:dirt') * 8)
    .time(50)
    .particleColor(0x00FF88)
    .register()

mods.actuallyadditions.empowerer.recipeBuilder()
    .input(item('minecraft:gold_ingot'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .time(50)
    .register()


// Nether Mining Lens:
// A weighted oredict for the block obtained via firing a Mining Lens at a block of Netherrack. The oredict must have a
// block, or the world will hang.

mods.actuallyadditions.nether_mining_lens.removeByOre(ore('oreQuartz'))
mods.actuallyadditions.nether_mining_lens.removeByOre('oreQuartz')
// mods.actuallyadditions.nether_mining_lens.removeAll()

mods.actuallyadditions.nether_mining_lens.recipeBuilder()
    .ore(ore('blockDiamond'))
    .weight(100)
    .register()

mods.actuallyadditions.nether_mining_lens.recipeBuilder()
    .ore('blockGold')
    .weight(100)
    .register()


// Oil Gen:
// Turns a fluid into power at a rate.

mods.actuallyadditions.oil_gen.removeByInput(fluid('canolaoil').getFluid())
mods.actuallyadditions.oil_gen.removeByInput(fluid('canolaoil'))
mods.actuallyadditions.oil_gen.removeByInput('refinedcanolaoil')
// mods.actuallyadditions.oil_gen.removeAll()

mods.actuallyadditions.oil_gen.recipeBuilder()
    .fluidInput(fluid('water'))
    .amount(1000)
    .time(50)
    .register()

mods.actuallyadditions.oil_gen.recipeBuilder()
    .fluidInput(fluid('lava') * 50)
    .time(100)
    .register()


// Stone Mining Lens:
// A weighted oredict for the block obtained via firing a Mining Lens at a block of Stone. The oredict must have a block,
// or the world will hang.

mods.actuallyadditions.stone_mining_lens.removeByOre(ore('oreCoal'))
mods.actuallyadditions.stone_mining_lens.removeByOre('oreLapis')
// mods.actuallyadditions.stone_mining_lens.removeAll()

mods.actuallyadditions.stone_mining_lens.recipeBuilder()
    .ore(ore('blockDiamond'))
    .weight(100)
    .register()

mods.actuallyadditions.stone_mining_lens.recipeBuilder()
    .ore('blockGold')
    .weight(100)
    .register()


// Treasure Chest:
// A weighted item, with a weight to obtain and a minimum and maximum amount. Obtained via right-clicking a Treasure Chest
// spawning randomly on the sea floor.

mods.actuallyadditions.treasure_chest.removeByOutput(item('minecraft:iron_ingot'))
// mods.actuallyadditions.treasure_chest.removeAll()

mods.actuallyadditions.treasure_chest.recipeBuilder()
    .output(item('minecraft:clay'))
    .weight(50)
    .min(16)
    .max(32)
    .register()


