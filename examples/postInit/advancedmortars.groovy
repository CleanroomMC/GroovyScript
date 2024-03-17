
// Auto generated groovyscript example file
// MODS_LOADED: advancedmortars

println 'mod \'advancedmortars\' detected, running script'

// Mortar:
// Uses any number of specific types of Mortars to convert multiple items into a single output with a possible chance
// output after a number of player interactions.

mods.advancedmortars.mortar.recipeBuilder()
    .type('stone')
    .duration(2)
    .output(item('minecraft:grass'))
    .input(item('minecraft:dirt'))
    .register()

mods.advancedmortars.mortar.recipeBuilder()
    .type('emerald')
    .duration(4)
    .output(item('minecraft:wheat_seeds') * 16)
    .secondaryOutput(item('minecraft:melon_seeds'))
    .input(ore('cropWheat'))
    .register()

mods.advancedmortars.mortar.recipeBuilder()
    .type('obsidian')
    .duration(8)
    .output(item('minecraft:wheat_seeds') * 16)
    .secondaryOutput(item('minecraft:melon_seeds'), 0.5)
    .input(ore('cropWheat'))
    .register()


mods.advancedmortars.mortar.add(['iron', 'wood'], item('minecraft:tnt') * 5, 4, item('minecraft:tnt'), 0.7, [ore('ingotIron'), ore('ingotIron'), ore('ingotIron'), ore('ingotIron'),ore('ingotIron'), ore('ingotIron'), ore('ingotIron'), ore('ingotIron')])
mods.advancedmortars.mortar.add(['stone'], item('minecraft:tnt'), 4, [ore('ingotGold')])
mods.advancedmortars.mortar.add(['stone'], item('minecraft:diamond') * 4, 4, [ore('ingotGold')])

