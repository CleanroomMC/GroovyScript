
// Auto generated groovyscript example file
// MODS_LOADED: extrabotany

println 'mod \'extrabotany\' detected, running script'

// groovyscript.wiki.extrabotany.pedestal.title:
// groovyscript.wiki.extrabotany.pedestal.description

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



