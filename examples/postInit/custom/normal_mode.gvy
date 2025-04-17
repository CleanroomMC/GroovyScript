
// PACKMODE: normal

crafting.removeByOutput(item('minecraft:furnace'))
crafting.shapedBuilder()
        .output(item('groovyscriptdev:clay_2'))
        .shape([[null, item('minecraft:iron_ingot'), null],
                [item('minecraft:iron_ingot'), null, item('minecraft:iron_ingot')],
                [null, item('minecraft:iron_ingot'), null]])
        .register()
