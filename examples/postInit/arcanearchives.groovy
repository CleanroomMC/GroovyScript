
// Auto generated groovyscript example file
// MODS_LOADED: arcanearchives

println 'mod \'arcanearchives\' detected, running script'

// Gem Cutting Table:
// Converts any number of itemstacks into a single output itemstack via selecting the desired output itemstack in the GUI.

mods.arcanearchives.gem_cutting_table.removeByInput(item('minecraft:gold_nugget'))
mods.arcanearchives.gem_cutting_table.removeByOutput(item('arcanearchives:shaped_quartz'))
// mods.arcanearchives.gem_cutting_table.removeAll()

mods.arcanearchives.gem_cutting_table.recipeBuilder()
    .name('clay_craft')
    .input(item('minecraft:stone') * 64)
    .output(item('minecraft:clay'))
    .register()

mods.arcanearchives.gem_cutting_table.recipeBuilder()
    .input(item('minecraft:stone'),item('minecraft:gold_ingot'),item('minecraft:gold_nugget'))
    .output(item('minecraft:clay') * 4)
    .register()


