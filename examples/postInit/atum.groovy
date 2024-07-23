
// Auto generated groovyscript example file
// MODS_LOADED: atum

println 'mod \'atum\' detected, running script'

// Kiln:
// Smelts an input item into an output itemstack and giving experience similar to a Furnace, but can process up to 4 stacks
// simultaneously. Makes a copy of the vanilla furnace recipes, excluding entries on a blacklist.

mods.atum.kiln.removeByInput(item('minecraft:netherrack'))
mods.atum.kiln.removeByOutput(item('minecraft:stone'))
// mods.atum.kiln.removeAll()

mods.atum.kiln.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .register()

mods.atum.kiln.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 4)
    .experience(0.5f)
    .register()


// Quern:
// Converts an input item into an output itemstack after a given number of rotations, which are done via a player right
// clicking the Quern.

mods.atum.quern.removeByInput(item('minecraft:blaze_rod'))
mods.atum.quern.removeByOutput(item('minecraft:sugar'))
// mods.atum.quern.removeAll()

mods.atum.quern.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .rotations(1)
    .register()

mods.atum.quern.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 4)
    .rotations(5)
    .register()


// Spinning Wheel:
// Converts three input items into an output itemstack after a given number of rotations for each input item, items are
// inserted by interacting with the top, rotations are increased by interacting with the top, and output items are
// extracted by interacting with the spool side.

mods.atum.spinning_wheel.removeByInput(item('atum:flax'))
mods.atum.spinning_wheel.removeByOutput(item('minecraft:string'))
// mods.atum.spinning_wheel.removeAll()

mods.atum.spinning_wheel.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .rotations(1)
    .register()

mods.atum.spinning_wheel.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 4)
    .rotations(5)
    .register()


