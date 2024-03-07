
// Auto generated groovyscript example file
// MODS_LOADED: compactmachines3

println 'mod \'compactmachines3\' detected, running script'

// Miniaturization:
// Consumes a 3d structure in-world based on keys when an item is thrown into the field.

mods.compactmachines3.miniaturization.removeByCatalyst(item('minecraft:redstone'))
mods.compactmachines3.miniaturization.removeByInput(item('minecraft:ender_pearl'))
mods.compactmachines3.miniaturization.removeByOutput(item('compactmachines3:machine:3'))
// mods.compactmachines3.miniaturization.removeAll()

mods.compactmachines3.miniaturization.recipeBuilder()
    .name('diamond_rectangle')
    .input(item('minecraft:clay'))
    .output(item('minecraft:clay'))
    .symmetrical()
    .ticks(10)
    .shape([['www',
             'www']])
    .key('w', blockstate('minecraft:diamond_block'))
    .register()

mods.compactmachines3.miniaturization.recipeBuilder()
    .name('groovy_rocket')
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay') * 64)
    .symmetrical()
    .ticks(5400)
    .key('a', blockstate('minecraft:stained_glass:0'))
    .key('b', blockstate('minecraft:stained_glass:1'))
    .key('c', blockstate('minecraft:stained_glass:2'))
    .key('d', blockstate('minecraft:stained_glass:3'))
    .key('e', blockstate('minecraft:diamond_block'))
    .key('f', blockstate('minecraft:stained_glass:5'))
    .key('g', blockstate('minecraft:stained_glass:6'))
    .layer('       ',
           '       ',
           '   a   ',
           '  aaa  ',
           '   a   ',
           '       ',
           '       ')
    .layer('       ',
           '   b   ',
           '  aaa  ',
           ' baaab ',
           '  aaa  ',
           '   b   ',
           '       ')
    .layer('       ',
           '   c   ',
           '  cac  ',
           ' caeac ',
           '  cac  ',
           '   c   ',
           '       ')
    .layer('       ',
           '   a   ',
           '  aaa  ',
           ' aaeaa ',
           '  aaa  ',
           '   a   ',
           '       ')
    .layer('       ',
           '   a   ',
           '  aaa  ',
           ' aaeaa ',
           '  aaa  ',
           '   a   ',
           '       ')
    .layer('       ',
           '   a   ',
           '  aaa  ',
           ' aaeaa ',
           '  aaa  ',
           '   a   ',
           '       ')
    .layer('       ',
           '   g   ',
           '  cac  ',
           ' caeac ',
           '  cac  ',
           '   f   ',
           '       ')
    .layer('       ',
           '   a   ',
           '  aaa  ',
           ' aaeaa ',
           '  aaa  ',
           '   a   ',
           '       ')
    .layer('       ',
           '   a   ',
           '  aaa  ',
           ' aaeaa ',
           '  aaa  ',
           '   a   ',
           '       ')
    .layer('       ',
           '   a   ',
           '  aaa  ',
           ' aaeaa ',
           '  aaa  ',
           '   a   ',
           '       ')
    .layer('       ',
           '   c   ',
           '  cac  ',
           ' caeac ',
           '  cac  ',
           '   c   ',
           '       ')
    .layer('       ',
           '   a   ',
           '  aaa  ',
           ' aaaaa ',
           '  aaa  ',
           '   a   ',
           '       ')
    .layer('   a   ',
           '  ccc  ',
           ' cdddc ',
           'acdddca',
           ' cdddc ',
           '  ccc  ',
           '   a   ')
    .register()


