
// Auto generated groovyscript example file
// MODS_LOADED: betterwithmods

println 'mod \'betterwithmods\' detected, running script'

// Anvil Crafting:
// Similar to a normal crafting table, but 4x4 instead.

mods.betterwithmods.anvil_crafting.removeByInput(item('minecraft:redstone'))
mods.betterwithmods.anvil_crafting.removeByOutput(item('betterwithmods:steel_block'))
mods.betterwithmods.anvil_crafting.removeAll()

mods.betterwithmods.anvil_crafting.shapedBuilder()
    .output(item('minecraft:diamond') * 32)
    .matrix([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),null],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),null],
            [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),null],
            [null,null,null,item('minecraft:gold_ingot').transform({ _ -> item('minecraft:diamond') })]])
    .register()

mods.betterwithmods.anvil_crafting.shapedBuilder()
    .output(item('minecraft:diamond'))
    .matrix('BXXX')
    .mirrored()
    .key('B', item('minecraft:stone'))
    .key('X', item('minecraft:gold_ingot'))
    .register()

mods.betterwithmods.anvil_crafting.shapelessBuilder()
    .name(resource('example:anvil_clay'))
    .output(item('minecraft:clay'))
    .input([item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
    .register()


// Cauldron:
// Converts a large number of items into other items, with the ability to require specific amounts of heat.

mods.betterwithmods.cauldron.removeByInput(item('minecraft:gunpowder'))
mods.betterwithmods.cauldron.removeByOutput(item('minecraft:gunpowder'))
mods.betterwithmods.cauldron.removeAll()

mods.betterwithmods.cauldron.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .heat(2)
    .register()

mods.betterwithmods.cauldron.recipeBuilder()
    .input(item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot') * 16)
    .ignoreHeat()
    .register()


// groovyscript.wiki.betterwithmods.crucible.title:
// groovyscript.wiki.betterwithmods.crucible.description

mods.betterwithmods.crucible.removeByInput(item('minecraft:gunpowder'))
mods.betterwithmods.crucible.removeByOutput(item('minecraft:gunpowder'))
mods.betterwithmods.crucible.removeAll()

mods.betterwithmods.crucible.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .heat(2)
    .register()

mods.betterwithmods.crucible.recipeBuilder()
    .input(item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot') * 16)
    .ignoreHeat()
    .register()


// Kiln:
// Converts a block into up to three output itemstacks, with the ability to require specific amounts of heat.

mods.betterwithmods.kiln.removeByInput(item('minecraft:end_stone'))
mods.betterwithmods.kiln.removeByOutput(item('minecraft:brick'))
mods.betterwithmods.kiln.removeAll()

mods.betterwithmods.kiln.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .heat(2)
    .register()

mods.betterwithmods.kiln.recipeBuilder()
    .input(item('minecraft:diamond_block'))
    .output(item('minecraft:gold_ingot') * 16)
    .ignoreHeat()
    .register()


// Mill Stone:
// Converts input itemstacks into output itemstacks after being ground via rotation power for a given time.

mods.betterwithmods.mill_stone.removeByInput(item('minecraft:netherrack'))
mods.betterwithmods.mill_stone.removeByOutput(item('minecraft:blaze_powder'))
mods.betterwithmods.mill_stone.removeAll()

mods.betterwithmods.mill_stone.recipeBuilder()
    .input(item('minecraft:diamond') * 2)
    .output(item('minecraft:gold_ingot') * 16)
    .register()

mods.betterwithmods.mill_stone.recipeBuilder()
    .input(item('minecraft:diamond_block'))
    .output(item('minecraft:gold_ingot'), item('minecraft:gold_block'), item('minecraft:clay'))
    .register()


// Saw:
// Converts a block into output itemstacks after being powered via rotation power.

mods.betterwithmods.saw.removeByInput(item('minecraft:vine'))
mods.betterwithmods.saw.removeByOutput(item('minecraft:pumpkin'))
mods.betterwithmods.saw.removeAll()

mods.betterwithmods.saw.recipeBuilder()
    .input(item('minecraft:diamond_block'))
    .output(item('minecraft:gold_ingot') * 16)
    .register()


// Turntable:
// Converts a block into an output block and up to two itemstacks after being powered via rotation power.

mods.betterwithmods.turntable.removeByInput(item('betterwithmods:unfired_pottery'))
mods.betterwithmods.turntable.removeByOutput(item('minecraft:clay_ball'))
mods.betterwithmods.turntable.removeAll()

mods.betterwithmods.turntable.recipeBuilder()
    .input(item('minecraft:gold_block'))
    .outputBlock(blockstate('minecraft:clay'))
    .output(item('minecraft:gold_ingot') * 5)
    .rotations(5)
    .register()

mods.betterwithmods.turntable.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .rotations(2)
    .register()


