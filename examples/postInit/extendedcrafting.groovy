
// MODS_LOADED: extendedcrafting
println 'mod \'extendedcrafting\' detected, running script'

// Combination Crafting (Combination):
// Converts one main item and any number of additional items into an output itemstack, with a configurable rf cost and consumption per tick amount.
mods.extendedcrafting.combination.recipeBuilder()
    .input(item('minecraft:pumpkin'))
    .pedestals(item('minecraft:pumpkin') * 8)
    .output(item('minecraft:diamond') * 2)
    .cost(100)
    .perTick(100) // Optional int, maximum amount of RF consumed per tick until the cost is paid. (Default ModConfig.confCraftingCoreRFRate, 500)
    .register()

mods.extendedcrafting.combinationcrafting.recipeBuilder()
    .input(item('minecraft:pumpkin'))
    .pedestals(item('minecraft:pumpkin'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:pumpkin'))
    .output(item('minecraft:gold_ingot') * 2)
    .cost(10000)
    .register()

// there are no combination recipes by default, and so none can be removed
//mods.extendedcrafting.combination.removeByInput(item('minecraft:pumpkin'))
//mods.extendedcrafting.combination.removeByOutput(item('minecraft:gold_ingot'))
//mods.extendedcrafting.combination.removeAll()


// Compression Crafting (Compression):
// Converts any number of a single item into an output itemstack, with a configurable rf cost, consumption per tick amount, catalyst, and if the catalyst is consumed.
mods.extendedcrafting.compressioncrafting.recipeBuilder()
    .input(item('minecraft:clay'))
    .inputCount(100)
    .output(item('minecraft:gold_ingot') * 7)
    .catalyst(item('minecraft:diamond')) // Optional IIngredient, the item in the catalyst slot. (Default ModConfig.confSingularityCatalyst, ItemMaterial.itemUltimateCatalyst)
    .consumeCatalyst(true) // Optional boolean, if the catalyst stack is consumed when the recipe completes. (Default false)
    .powerCost(10000)
    .powerRate(1000) // Optional int, maximum amount of RF consumed per tick until the cost is paid. (Default ModConfig.confCompressorRFRate, 5000)
    .register()

mods.extendedcrafting.compression.recipeBuilder()
    .input(item('minecraft:clay') * 10) // Input count can also be defined like this.
    .output(item('minecraft:diamond') * 2)
    .powerCost(1000)
    .register()

mods.extendedcrafting.compression.removeByInput(item('minecraft:gold_ingot'))
mods.extendedcrafting.compression.removeByCatalyst(item('extendedcrafting:material:11'))
mods.extendedcrafting.compression.removeByOutput(item('extendedcrafting:singularity:6'))
//mods.extendedcrafting.compression.removeAll()

// Ender Crafting:
// A normal crafting recipe, with the recipe being slowly crafted based on nearby Ender Alternators.
mods.extendedcrafting.endercrafting.shapelessBuilder()
        .output(item('minecraft:clay') * 8)
        .input(item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'))
        .register()

mods.extendedcrafting.endercrafting.shapedBuilder()
        .output(item('minecraft:stone'))
        .matrix('BXX',
                'X B')
        .key('B', item('minecraft:stone'))
        .key('X', item('minecraft:gold_ingot'))
        .time(1)
        .mirrored()
        .register()

mods.extendedcrafting.endercrafting.shapelessBuilder()
        .output(item('minecraft:clay') * 32)
        .input(item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:diamond'))
        .time(1)
        .register()

mods.extendedcrafting.endercrafting.shapedBuilder()
        .output(item('minecraft:diamond') * 32)
        .matrix([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
                [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
                [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
        .time(1)
        .register()

mods.extendedcrafting.endercrafting.removeByOutput(item('extendedcrafting:material:40'))
//mods.extendedcrafting.endercrafting.removeAll()

// Table Crafting
// A normal crafting recipe, but requiring either a specific tier, or at least a given tier, from 3x3 to 9x9.
mods.extendedcrafting.tablecrafting.shapedBuilder()
        .output(item('minecraft:stone') * 64)
        .matrix(
            'DLLLLLDDD',
            '  DNIGIND',
            'DDDNIGIND',
            '  DLLLLLD')
        .key('D', item('minecraft:diamond'))
        .key('L', item('minecraft:redstone'))
        .key('N', item('minecraft:stone'))
        .key('I', item('minecraft:iron_ingot'))
        .key('G', item('minecraft:gold_ingot'))
        .tierUltimate()
        .register()

mods.extendedcrafting.tablecrafting.shapedBuilder()
        .tierAdvanced()
        .output(item('minecraft:stone') * 8)
        .matrix('BXX')
        .mirrored()
        .key('B', item('minecraft:stone'))
        .key('X', item('minecraft:gold_ingot'))
        .register()

mods.extendedcrafting.tablecrafting.shapedBuilder()
        .tierAny()
        .output(item('minecraft:diamond'))
        .matrix('BXXXBX')
        .mirrored()
        .key('B', item('minecraft:stone'))
        .key('X', item('minecraft:gold_ingot'))
        .register()

mods.extendedcrafting.tablecrafting.shapedBuilder()
    .matrix([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
    .output(item('minecraft:gold_ingot') * 64)
    .tier(4) // while we only have a 7x7 of gold ingots in the recipe, specifically requiring tier 4 (ultimate) locks us in
    .register()

mods.extendedcrafting.tablecrafting.shapedBuilder() // can be crafted in any tier of table
    .matrix([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),]])
    .output(item('minecraft:gold_ingot') * 64)
    .register()

mods.extendedcrafting.tablecrafting.shapelessBuilder()
        .output(item('minecraft:stone') * 64)
        .input(item('minecraft:stone'), // 26 stone = can be crafted in either elite or ultimate
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'))
        .register()

mods.extendedcrafting.tablecrafting.removeByOutput(item('extendedcrafting:singularity_ultimate'))
//mods.extendedcrafting.tablecrafting.removeAll()

