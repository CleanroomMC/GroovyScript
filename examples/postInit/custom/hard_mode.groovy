// PACKMODE: hard

crafting.shapedBuilder()
        .output(item('groovyscriptdev:clay_2'))
        .shape([[null, item('minecraft:diamond'), null],
                [item('minecraft:diamond'), null, item('minecraft:diamond')],
                [null, item('minecraft:diamond'), null]])
        .register()
