
// Auto generated groovyscript example file
// MODS_LOADED: botania_tweaks

log.info 'mod \'botania_tweaks\' detected, running script'

// Agglomeration Plate:
// Converts any number of input itemstacks into an item output, consuming mana to do so. Occurs in-world above a
// Terrestrial Agglomeration Plate place on top of a small 3x3 multiblock, of which the center, sides, and corners may be
// set to require specific blockstates. While the recipe is running, particles will gradually change color until the recipe
// is finished. Upon finishing the recipe, the center, sides, and corners can each be converted into a replacement
// blockstate, if a replacement blockstate was set.

// mods.botania_tweaks.agglomeration_plate.removeByCenter(blockstate('botania:livingrock'))
// mods.botania_tweaks.agglomeration_plate.removeByCorner(blockstate('botania:livingrock'))
// mods.botania_tweaks.agglomeration_plate.removeByEdge(blockstate('minecraft:lapis_block'))
// mods.botania_tweaks.agglomeration_plate.removeByInput(item('botania:manaresource:2'))
mods.botania_tweaks.agglomeration_plate.removeByOutput(item('botania:manaresource:4'))
// mods.botania_tweaks.agglomeration_plate.removeAll()

mods.botania_tweaks.agglomeration_plate.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond') * 3)
    .baseStructure()
    .mana(100000)
    .color(0xff00ff, 0x00ffff)
    .register()

mods.botania_tweaks.agglomeration_plate.recipeBuilder()
    .input(item('minecraft:diamond'), item('minecraft:gold_ingot'), item('minecraft:gold_block'))
    .output(item('minecraft:clay') * 32)
    .colorStart(0x000000)
    .colorEnd(0x0000ff)
    .center(blockstate('minecraft:gold_block'))
    .edge(blockstate('botania:livingwood:variant=glimmering'))
    .corner(blockstate('botania:livingwood:variant=glimmering'))
    .register()

mods.botania_tweaks.agglomeration_plate.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .mana(50000)
    .baseStructure()
    .center(blockstate('minecraft:diamond_block'))
    .centerReplacement(blockstate('minecraft:clay'))
    .edgeReplacement(blockstate('botania:livingrock:variant=default'))
    .cornerReplacement(blockstate('minecraft:lapis_block'))
    .register()

mods.botania_tweaks.agglomeration_plate.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:clay'))
    .mana(1000)
    .center(blockstate('minecraft:clay'))
    .edge(blockstate('botania:livingrock:variant=default'))
    .corner(blockstate('minecraft:lapis_block'))
    .centerReplacement(blockstate('minecraft:diamond_block'))
    .edgeReplacement(blockstate('minecraft:lapis_block'))
    .cornerReplacement(blockstate('botania:livingrock:variant=default'))
    .register()


