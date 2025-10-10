
// Auto generated groovyscript example file
// MODS_LOADED: extrabotany

log 'mod \'extrabotany\' detected, running script'

// Livingrock Pedestal:
// Converts an input item into an output itemstack when placed inside a Livingrock Pedestal and interacted with by an Extra
// Botany Hammer.

mods.extrabotany.pedestal.removeByInput(item('minecraft:cobblestone'))
mods.extrabotany.pedestal.removeByOutput(item('minecraft:flint'))
// mods.extrabotany.pedestal.removeAll()

mods.extrabotany.pedestal.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .register()

mods.extrabotany.pedestal.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond') * 2)
    .register()


