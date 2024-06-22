
// Auto generated groovyscript example file
// MODS_LOADED: theaurorian

println 'mod \'theaurorian\' detected, running script'

// Moonlight Forge:
// Combines two items to get a third item. Only works at night, and works faster the higher it is placed in the world.

mods.theaurorian.moonlight_forge.removeByInput(item('theaurorian:aurorianiteingot'))
mods.theaurorian.moonlight_forge.removeByOutput(item('theaurorian:queenschipper'))
// mods.theaurorian.moonlight_forge.removeAll()

mods.theaurorian.moonlight_forge.recipeBuilder()
    .input(item('minecraft:stone_sword'), item('minecraft:diamond'))
    .output(item('minecraft:diamond_sword'))
    .register()


// Scrapper:
// Turns an input item into an output item. Can be sped up by placing a Crystal on top of it. The crystal has a chance to
// break every time a recipe is executed.

mods.theaurorian.scrapper.removeByInput(item('minecraft:iron_sword'))
mods.theaurorian.scrapper.removeByOutput(item('theaurorian:scrapaurorianite'))
// mods.theaurorian.scrapper.removeAll()

mods.theaurorian.scrapper.recipeBuilder()
    .input(item('minecraft:stone_sword'))
    .output(item('minecraft:cobblestone'))
    .register()


