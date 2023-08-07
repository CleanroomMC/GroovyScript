
if (!isLoaded('compactmachines3')) return
println 'mod \'compactmachines3\' detected, running script'

// Miniaturization:
// Consumes a 3d structure in-world based on keys when an item is thrown into the field.
mods.compactmachines.miniaturization.recipeBuilder()
    .name('diamond_rectangle') // Optional, String
    .input(item('minecraft:clay'))
    .output(item('minecraft:clay'))
    .symmetrical() // Indicates that the recipe does not have to test all 4 rotations to determine if the multiblock is valid
    .ticks(10) // Alias: duration, default 100
    .shape([['www', 'www']]) // Shape is a List<List<String>>
    .key('w', blockstate('minecraft:diamond_block'))
    // character, blockstate, nbt, metadata-sensitive (default true), display item
    //.key('w', blockstate('minecraft:diamond_block'), null, true, item('minecraft:diamond') * 7)
    .register()

mods.compactmachines.miniaturization.recipeBuilder()
    .name('groovy_rocket') // Optional, String
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay') * 64)
    .symmetrical()
    .ticks(5400)
    // both ` ` and `_` are reserved for empty space, and cannot be used as keys
    .key('a', blockstate('minecraft:stained_glass:0'))
    .key('b', blockstate('minecraft:stained_glass:1'))
    .key('c', blockstate('minecraft:stained_glass:2'))
    .key('d', blockstate('minecraft:stained_glass:3'))
    .key('e', blockstate('minecraft:diamond_block'))
    .key('f', blockstate('minecraft:stained_glass:5'))
    .key('g', blockstate('minecraft:stained_glass:6')) // Note: More than 6 keys results in incorrect displays in JEI
    .layer("       ", "       ", "   a   ", "  aaa  ", "   a   ", "       ", "       ") // layer adds String... to the shape structure
    .layer("       ", "   b   ", "  aaa  ", " baaab ", "  aaa  ", "   b   ", "       ") // adds layers in descending Y order
    .layer("       ", "   c   ", "  cac  ", " caeac ", "  cac  ", "   c   ", "       ")
    .layer("       ", "   a   ", "  aaa  ", " aaeaa ", "  aaa  ", "   a   ", "       ")
    .layer("       ", "   a   ", "  aaa  ", " aaeaa ", "  aaa  ", "   a   ", "       ")
    .layer("       ", "   a   ", "  aaa  ", " aaeaa ", "  aaa  ", "   a   ", "       ")
    .layer("       ", "   g   ", "  cac  ", " caeac ", "  cac  ", "   f   ", "       ")
    .layer("       ", "   a   ", "  aaa  ", " aaeaa ", "  aaa  ", "   a   ", "       ")
    .layer("       ", "   a   ", "  aaa  ", " aaeaa ", "  aaa  ", "   a   ", "       ")
    .layer("       ", "   a   ", "  aaa  ", " aaeaa ", "  aaa  ", "   a   ", "       ")
    .layer("       ", "   c   ", "  cac  ", " caeac ", "  cac  ", "   c   ", "       ")
    .layer("       ", "   a   ", "  aaa  ", " aaaaa ", "  aaa  ", "   a   ", "       ")
    .layer("   a   ", "  ccc  ", " cdddc ", "acdddca", " cdddc ", "  ccc  ", "   a   ")
    .register()

mods.compactmachines.miniaturization.removeByInput(item('minecraft:ender_pearl'))
mods.compactmachines.miniaturization.removeByCatalyst(item('minecraft:redstone'))
mods.compactmachines.miniaturization.removeByOutput(item('compactmachines3:machine:3'))
//mods.compactmachines.miniaturization.removeAll()
