
// Auto generated groovyscript example file
// MODS_LOADED: avaritia

println 'mod \'avaritia\' detected, running script'

// Compressor:
// Converts any number of a single item into an output itemstack.

mods.avaritia.compressor.removeByOutput(item('avaritia:singularity', 0))
// mods.avaritia.compressor.removeAll()

mods.avaritia.compressor.recipeBuilder()
    .input(item('minecraft:clay_ball') * 100)
    .output(item('minecraft:nether_star'))
    .inputCount(100)
    .register()


mods.avaritia.compressor.add(item('minecraft:nether_star'), item('minecraft:clay_ball'), 100)

// Extreme Crafting:
// A normal crafting table, by 9x9 instead.

mods.avaritia.extreme_crafting.removeByOutput(item('avaritia:resource', 6))
// mods.avaritia.extreme_crafting.removeAll()

mods.avaritia.extreme_crafting.shapedBuilder()
    .matrix([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
    .output(item('minecraft:gold_block'))
    .register()

mods.avaritia.extreme_crafting.shapedBuilder()
    .output(item('minecraft:stone') * 64)
    .matrix('DLLLLLDDD',
            '  DNIGIND',
            'DDDNIGIND',
            '  DLLLLLD')
    .key('D', item('minecraft:diamond'))
    .key('L', item('minecraft:redstone'))
    .key('N', item('minecraft:stone').reuse())
    .key('I', item('minecraft:iron_ingot'))
    .key('G', item('minecraft:gold_ingot'))
    .register()

mods.avaritia.extreme_crafting.shapelessBuilder()
    .output(item('minecraft:stone') * 64)
    .input(item('minecraft:stone'), item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'), item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'), item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'), item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'), item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'))
    .register()



