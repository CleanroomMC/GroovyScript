
if (!isLoaded('avaritia')) return
println 'mod \'avaritia\' detected, running script'

// extreme crafting

// remove by output
mods.avaritia.ExtremeCrafting.removeByOutput(item('avaritia:resource', 6))

// add shaped recipe with nested ingredient list
mods.avaritia.ExtremeCrafting.shapedBuilder()
        .matrix([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
                 [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
                 [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
                 [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
                 [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
                 [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
                 [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
        .output(item('minecraft:gold_block'))
        .register()

// add shaped recipe with key based input
mods.avaritia.ExtremeCrafting.shapedBuilder()
        .output(item('minecraft:stone') * 64)
        .matrix(
                'DLLLLLDDD',
                '  DNIGIND',
                'DDDNIGIND',
                '  DLLLLLD')
        .key('D', item('minecraft:diamond'))
        .key('L', item('minecraft:redstone'))
        .key('N', item('minecraft:stone').reuse()) // stone will not be consumed in the recipe
        .key('I', item('minecraft:iron_ingot'))
        .key('G', item('minecraft:gold_ingot'))
        .register()

// add shapeless recipe with ingredient list
mods.avaritia.ExtremeCrafting.shapelessBuilder()
        .output(item('minecraft:stone') * 64)
        .input(item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'))
        .register()

// Compressor

// remove by output
mods.avaritia.Compressor.removeByOutput(item('avaritia:singularity', 0))

// add
mods.avaritia.Compressor.add(item('minecraft:nether_star'), item('minecraft:clay_ball'), 100) // the last number is the required input amount
mods.avaritia.Compressor.recipeBuilder()
        .input(item('minecraft:clay_ball') * 100) // this also specifies the input amount (you should only specify 1)
        .output(item('minecraft:nether_star'))
        .inputCount(100) // required input amount
        .register()
