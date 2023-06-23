
// Combination crafting

// there are no combination recipes by default, and so none can be removed
//mods.extendedcrafting.combination.removeAll()
//mods.extendedcrafting.combination.removeByInput(item('minecraft:pumpkin'))
//mods.extendedcrafting.combination.removeByOutput(item('minecraft:gold_ingot'))

mods.extendedcrafting.combination.recipeBuilder()
    .cost(100)
    .perTick(100)
    .output(item('minecraft:diamond') * 2)
    .input(item('minecraft:pumpkin'))
    .pedestals(item('minecraft:pumpkin') * 8)
    .register()

mods.extendedcrafting.combinationcrafting.recipeBuilder()
    .cost(10000)
    .output(item('minecraft:gold_ingot') * 2)
    .input(item('minecraft:pumpkin'))
    .pedestals(item('minecraft:pumpkin'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:pumpkin'))
    .register()

// Compression crafting

//mods.extendedcrafting.combination.removeAll()
//mods.extendedcrafting.combination.removeByInput(item('minecraft:gold_ingot'))
//mods.extendedcrafting.combination.removeByCatalyst(item('extendedcrafting:material:11'))
//mods.extendedcrafting.combination.removeByOutput(item('extendedcrafting:singularity:6'))

mods.extendedcrafting.compression.recipeBuilder()
    .input(item('minecraft:clay') * 10)
    .output(item('minecraft:diamond') * 2)
    .powerCost(1000)
    .register()

mods.extendedcrafting.compressioncrafting.recipeBuilder()
    .input(item('minecraft:clay'))
    .inputCount(100)
    .output(item('minecraft:gold_ingot') * 7)
    .catalyst(item('minecraft:diamond'))
    .consumeCatalyst(true)
    .powerCost(10000)
    .powerRate(1000)
    .register()

// Ender crafting

//mods.extendedcrafting.endercrafting.removeByOutput(item('extendedcrafting:material:40'))

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

// Table crafting

//mods.extendedcrafting.tablecrafting.removeByOutput(item('extendedcrafting:singularity_ultimate'))

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

