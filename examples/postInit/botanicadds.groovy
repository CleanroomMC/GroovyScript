
// Auto generated groovyscript example file
// MODS_LOADED: botanicadds

log.info 'mod \'botanicadds\' detected, running script'

// Gaia Plate:
// Converts an number of input items into an output itemstack, consuming a given amount of mana when dropped in-world atop
// a Gaia Agglomeration Plate as part of a multiblock structure.

mods.botanicadds.gaia_plate.removeByInput(item('botania:manaresource'))
mods.botanicadds.gaia_plate.removeByOutput(item('botanicadds:gaiasteel_ingot'))
// mods.botanicadds.gaia_plate.removeAll()

mods.botanicadds.gaia_plate.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot') * 16)
    .mana(1000)
    .register()

mods.botanicadds.gaia_plate.recipeBuilder()
    .input(item('minecraft:diamond_block'), item('minecraft:gold_block'), item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .mana(100)
    .register()


