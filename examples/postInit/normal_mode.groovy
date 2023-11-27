
// PACKMODE: normal

crafting.removeByOutput(item('minecraft:furnace'))
crafting.shapedBuilder()
        .output(item('placeholdername:clay_2'))
        .shape([[null, item('minecraft:iron_ingot'), null],
                [item('minecraft:iron_ingot'), null, item('minecraft:iron_ingot')],
                [null, item('minecraft:iron_ingot'), null]])
        .register()
