
// MODS_LOADED: actuallyadditions

println 'mod \'actuallyadditions\' detected, running script'


// Atomic Reconstructor
// The Atomic Reconstructor is a block which uses energy to convert a block or item in front of it into other items.
mods.actuallyadditions.atomicreconstructor.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .energyUse(1000) // Optional, int
    .register()

mods.actuallyadditions.atomicreconstructor.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 2)
    .register()

mods.actuallyadditions.atomicreconstructor.removeByInput(item('minecraft:diamond'))
mods.actuallyadditions.atomicreconstructor.removeByOutput(item('actuallyadditions:block_crystal'))
//mods.actuallyadditions.atomicreconstructor.removeAll()

// Ball of Fur
// A weighted itemstack output for using a Ball of Fur, dropped by a cat.
mods.actuallyadditions.balloffur.recipeBuilder()
    .output(item('minecraft:clay') * 32)
    .weight(15)
    .register()

mods.actuallyadditions.balloffur.removeByOutput(item('minecraft:feather'))
//mods.actuallyadditions.balloffur.removeAll()

// Compost
// Converts an input item into an output item after 150 seconds. Requires an input and output display blockstate.
mods.actuallyadditions.compost.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .inputDisplay(blockstate('minecraft:clay'))
    .outputDisplay(blockstate('minecraft:diamond_block'))
    .register()

mods.actuallyadditions.compost.removeByInput(item('actuallyadditions:item_canola_seed'))
mods.actuallyadditions.compost.removeByOutput(item('actuallyadditions:item_fertilizer'))
//mods.actuallyadditions.compost.removeAll()

// Crusher
// Converts an input itemstack into an output itemstack with a chance of a second itemstack
mods.actuallyadditions.crusher.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'), item('minecraft:diamond')) // Second output is optional, and modified by chance
    .chance(100) // Optional, int
    .register()

mods.actuallyadditions.crusher.recipeBuilder()
    .input(item('minecraft:diamond_block'))
    .output(item('minecraft:diamond') * 12)
    .register()

mods.actuallyadditions.crusher.removeByInput(item('minecraft:bone'))
mods.actuallyadditions.crusher.removeByOutput(item('minecraft:sugar'))
//mods.actuallyadditions.crusher.removeAll()

// Empowerer
// Turns 5 input items into an output item at the cost of power and time. Has a configurable color
mods.actuallyadditions.empowerer.recipeBuilder()
    .mainInput(item('minecraft:clay')) // Optional, itemstack. if undefined and input has 5 items, mainInput uses the first itemstack input. Otherwise, errors.
    .input(item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .time(50)
    .energy(1000) // Optional, int
    .red(0.5) // Optional, float (default 0)
    .green(0.3) // Optional, float (default 0)
    .blue(0.2) // Optional, float (default 0)
    .register()

mods.actuallyadditions.empowerer.recipeBuilder()
    .mainInput(item('minecraft:clay'))
    .input(item('minecraft:diamond'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay'))
    .output(item('minecraft:diamond') * 2)
    .time(50)
    .color(0.5, 0.3, 0.2) // Optional, float... shorthand for (red, green, blue). Must contain exactly 3 entries. "particleColor" and "color" are aliases
    .register()

mods.actuallyadditions.empowerer.recipeBuilder()
    .mainInput(item('minecraft:diamond'))
    .input(item('minecraft:diamond'),item('minecraft:gold_ingot'),item('minecraft:diamond'),item('minecraft:gold_ingot'))
    .output(item('minecraft:dirt') * 8)
    .time(50)
    .particleColor(0x00FF88) // Optional, int. Hexadecimal color. "particleColor" and "color" are aliases
    .register()

mods.actuallyadditions.empowerer.recipeBuilder()
    .input(item('minecraft:gold_ingot'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .time(50)
    .register()

mods.actuallyadditions.empowerer.removeByInput(item('actuallyadditions:item_crystal'))
mods.actuallyadditions.empowerer.removeByOutput(item('actuallyadditions:item_misc:24'))
//mods.actuallyadditions.empowerer.removeAll()

// Nether Mining Lens
// A weighted oredict for the block obtained via firing a Mining Lens at a block of Netherrack. The oredict must have a block, or the world will hang.
mods.actuallyadditions.nethermininglens.recipeBuilder()
    .ore(ore('blockDiamond'))
    .weight(100)
    .register()

mods.actuallyadditions.nethermininglens.recipeBuilder()
    .ore('blockGold')
    .weight(100)
    .register()

mods.actuallyadditions.nethermininglens.removeByOre(ore('oreQuartz'))
mods.actuallyadditions.nethermininglens.removeByOre('oreQuartz')
//mods.actuallyadditions.nethermininglens.removeAll()

// Oil Gen
// Turns a fluid into power at a rate
mods.actuallyadditions.oilgen.recipeBuilder()
    .fluidInput(fluid('water'))
    .amount(1000) // Optional, uses the FluidStack amount if not defined.
    .time(50)
    .register()

mods.actuallyadditions.oilgen.recipeBuilder()
    .fluidInput(fluid('lava') * 50)
    .time(100)
    .register()

mods.actuallyadditions.oilgen.removeByInput(fluid('canolaoil').getFluid())
mods.actuallyadditions.oilgen.removeByInput('refinedcanolaoil')
//mods.actuallyadditions.oilgen.removeAll()


// Stone Mining Lens
// A weighted oredict for the block obtained via firing a Mining Lens at a block of Stone. The oredict must have a block, or the world will hang.
mods.actuallyadditions.stonemininglens.recipeBuilder()
    .ore(ore('blockDiamond'))
    .weight(100)
    .register()

mods.actuallyadditions.stonemininglens.recipeBuilder()
    .ore('blockGold')
    .weight(100)
    .register()

mods.actuallyadditions.stonemininglens.removeByOre(ore('oreCoal'))
mods.actuallyadditions.stonemininglens.removeByOre('oreLapis')
//mods.actuallyadditions.stonemininglens.removeAll()

// Treasure Chest
// A weighted item, with a weight to obtain and a minimum and maximum amount. Obtained via right clicking a Treasure Chest spawning randomly on the sea floor.
mods.actuallyadditions.treasurechest.recipeBuilder()
    .output(item('minecraft:clay'))
    .weight(50)
    .min(16)
    .max(32)
    .register()

mods.actuallyadditions.treasurechest.removeByOutput(item('minecraft:iron_ingot'))
//mods.actuallyadditions.treasurechest.removeAll()






