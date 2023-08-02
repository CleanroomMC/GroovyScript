
mods.avaritia.ExtremeCrafting.removeByOutput(item('avaritia:resource', 6))
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

mods.avaritia.ExtremeCrafting.shapelessBuilder()
        .output(item('minecraft:stone') * 64)
        .input(item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),
                item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'))
        .register()

mods.avaritia.Compressor.removeByOutput(item('avaritia:singularity', 0))
mods.avaritia.Compressor.add(item('minecraft:nether_star'), item('minecraft:clay_ball'), 100) // the last number is the required input amount
mods.avaritia.Compressor.recipeBuilder()
        .input(item('minecraft:clay_ball'))
        .output(item('minecraft:nether_star'))
        .cost(100) // required input amount
        .register()
