// Railcraft GroovyScript Example
// This file demonstrates how to use the Railcraft compat features

// ============================================
// Blast Furnace
// ============================================

// Add a recipe: input, output, time (ticks), slag amount
mods.railcraft.blastFurnace.add(item('minecraft:iron_ingot'), item('railcraft:ingot:1'), 1280, 1)

// Add with default time (1280 ticks) and slag (1)
mods.railcraft.blastFurnace.add(item('minecraft:iron_block'), item('railcraft:ingot:1') * 9)

// Using recipe builder
mods.railcraft.blastFurnace.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('railcraft:ingot:2'))
    .time(1000)
    .slag(2)
    .register()

// Remove by output
mods.railcraft.blastFurnace.removeByOutput(item('railcraft:ingot:1'))

// Remove by input
mods.railcraft.blastFurnace.removeByInput(item('minecraft:iron_ingot'))

// Remove all
mods.railcraft.blastFurnace.removeAll()


// ============================================
// Coke Oven
// ============================================

// Add a recipe: input, output, fluid output, time (ticks)
mods.railcraft.cokeOven.add(item('minecraft:log'), item('railcraft:fuel_coke'), fluid('creosote') * 500, 1800)

// Add with item output only
mods.railcraft.cokeOven.add(item('minecraft:log:1'), item('railcraft:fuel_coke'), 1800)

// Using recipe builder
mods.railcraft.cokeOven.recipeBuilder()
    .input(item('minecraft:coal'))
    .output(item('railcraft:fuel_coke'))
    .fluid(fluid('creosote') * 1000)
    .time(2000)
    .register()

// Remove by output
mods.railcraft.cokeOven.removeByOutput(item('railcraft:fuel_coke'))

// Remove by input
mods.railcraft.cokeOven.removeByInput(item('minecraft:log'))

// Remove all
mods.railcraft.cokeOven.removeAll()


// ============================================
// Rock Crusher
// ============================================

// Add a recipe with multiple outputs and chances
mods.railcraft.rockCrusher.recipeBuilder()
    .input(item('minecraft:stone'))
    .output(item('minecraft:cobblestone'), 1.0f)
    .output(item('minecraft:sand'), 0.5f)
    .output(item('minecraft:gravel'), 0.25f)
    .time(200)
    .register()

// Remove by output
mods.railcraft.rockCrusher.removeByOutput(item('minecraft:cobblestone'))

// Remove by input
mods.railcraft.rockCrusher.removeByInput(item('minecraft:stone'))

// Remove all
mods.railcraft.rockCrusher.removeAll()


// ============================================
// Rolling Machine
// ============================================

// Add shaped recipe
mods.railcraft.rollingMachine.addShaped(
    item('minecraft:iron_bars') * 8,
    [
        [item('minecraft:iron_ingot'), item('minecraft:iron_ingot'), item('minecraft:iron_ingot')],
        [item('minecraft:iron_ingot'), item('minecraft:iron_ingot'), item('minecraft:iron_ingot')]
    ],
    200
)

// Add shapeless recipe
mods.railcraft.rollingMachine.addShapeless(
    item('minecraft:iron_ingot') * 9,
    [item('minecraft:iron_block')],
    100
)

// Using shaped builder
mods.railcraft.rollingMachine.shapedBuilder()
    .output(item('minecraft:rail') * 16)
    .matrix('I I', 'I I', 'I I')
    .key('I', item('minecraft:iron_ingot'))
    .time(100)
    .register()

// Using shapeless builder
mods.railcraft.rollingMachine.shapelessBuilder()
    .output(item('minecraft:iron_nugget') * 9)
    .input(item('minecraft:iron_ingot'))
    .time(50)
    .register()

// Remove by output
mods.railcraft.rollingMachine.removeByOutput(item('minecraft:rail'))

// Remove all
mods.railcraft.rollingMachine.removeAll()


// ============================================
// Fluid Fuels (for Boilers)
// ============================================

// Add a fluid fuel with heat value
mods.railcraft.fluidFuels.add(fluid('lava'), 32000)

// Add with default heat value
mods.railcraft.fluidFuels.add(fluid('oil'))

// Remove a fluid fuel
mods.railcraft.fluidFuels.remove(fluid('creosote'))

// Remove all
mods.railcraft.fluidFuels.removeAll()
