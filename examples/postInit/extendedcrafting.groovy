
// Auto generated groovyscript example file
// MODS_LOADED: extendedcrafting

println 'mod \'extendedcrafting\' detected, running script'

// Combination Crafting:
// Converts one main item and any number of additional items into an output itemstack, with a configurable rf cost and
// consumption per tick amount.

// mods.extendedcrafting.combination_crafting.removeByInput(item('minecraft:pumpkin'))
// mods.extendedcrafting.combination_crafting.removeByOutput(item('minecraft:gold_ingot'))
// mods.extendedcrafting.combination_crafting.removeAll()

mods.extendedcrafting.combination_crafting.recipeBuilder()
    .input(item('minecraft:pumpkin'))
    .pedestals(item('minecraft:pumpkin') * 8)
    .output(item('minecraft:diamond') * 2)
    .cost(100)
    .perTick(100)
    .register()

mods.extendedcrafting.combination_crafting.recipeBuilder()
    .input(item('minecraft:pumpkin'))
    .pedestals(item('minecraft:pumpkin'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:pumpkin'))
    .output(item('minecraft:gold_ingot') * 2)
    .cost(10000)
    .register()



// Compression Crafting:
// Converts any number of a single item into an output itemstack, with a configurable rf cost, consumption per tick amount,
// catalyst, and if the catalyst is consumed.

mods.extendedcrafting.compression_crafting.removeByCatalyst(item('extendedcrafting:material:11'))
mods.extendedcrafting.compression_crafting.removeByInput(item('minecraft:gold_ingot'))
mods.extendedcrafting.compression_crafting.removeByOutput(item('extendedcrafting:singularity:6'))
// mods.extendedcrafting.compression_crafting.removeAll()

mods.extendedcrafting.compression_crafting.recipeBuilder()
    .input(item('minecraft:clay'))
    .inputCount(100)
    .output(item('minecraft:gold_ingot') * 7)
    .catalyst(item('minecraft:diamond'))
    .consumeCatalyst(true)
    .powerCost(10000)
    .powerRate(1000)
    .register()

mods.extendedcrafting.compression_crafting.recipeBuilder()
    .input(item('minecraft:clay') * 10)
    .output(item('minecraft:diamond') * 2)
    .powerCost(1000)
    .register()



// Ender Crafting:
// A normal crafting recipe, with the recipe being slowly crafted based on nearby Ender Alternators.

mods.extendedcrafting.ender_crafting.removeByOutput(item('extendedcrafting:material:40'))
// mods.extendedcrafting.ender_crafting.removeAll()

mods.extendedcrafting.ender_crafting.shapedBuilder()
    .output(item('minecraft:stone'))
    .matrix('BXX',
            'X B')
    .key('B', item('minecraft:stone'))
    .key('X', item('minecraft:gold_ingot'))
    .time(1)
    .mirrored()
    .register()

mods.extendedcrafting.ender_crafting.shapedBuilder()
    .output(item('minecraft:diamond') * 32)
    .matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]])
    .time(1)
    .register()

mods.extendedcrafting.ender_crafting.shapelessBuilder()
    .output(item('minecraft:clay') * 8)
    .input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))
    .register()

mods.extendedcrafting.ender_crafting.shapelessBuilder()
    .output(item('minecraft:clay') * 32)
    .input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'))
    .time(1)
    .register()



// Table Crafting:
// A normal crafting recipe, but requiring either a specific tier, or at least a given tier, from 3x3 to 9x9.

mods.extendedcrafting.table_crafting.removeByOutput(item('extendedcrafting:singularity_ultimate'))
// mods.extendedcrafting.table_crafting.removeAll()

mods.extendedcrafting.table_crafting.shapedBuilder()
    .output(item('minecraft:stone') * 64)
    .matrix('DLLLLLDDD',
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

mods.extendedcrafting.table_crafting.shapedBuilder()
    .tierAdvanced()
    .output(item('minecraft:stone') * 8)
    .matrix('BXX')
    .mirrored()
    .key('B', item('minecraft:stone'))
    .key('X', item('minecraft:gold_ingot'))
    .register()

mods.extendedcrafting.table_crafting.shapedBuilder()
    .tierAny()
    .output(item('minecraft:diamond'))
    .matrix('BXXXBX')
    .mirrored()
    .key('B', item('minecraft:stone'))
    .key('X', item('minecraft:gold_ingot'))
    .register()

mods.extendedcrafting.table_crafting.shapedBuilder()
    .matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]])
    .output(item('minecraft:gold_ingot') * 64)
    .tier(4)
    .register()

mods.extendedcrafting.table_crafting.shapedBuilder()
    .matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]])
    .output(item('minecraft:gold_ingot') * 64)
    .register()

mods.extendedcrafting.table_crafting.shapelessBuilder()
    .output(item('minecraft:stone') * 64)
    .input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))
    .register()



