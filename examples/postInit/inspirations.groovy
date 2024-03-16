
// Auto generated groovyscript example file
// MODS_LOADED: inspirations

println 'mod \'inspirations\' detected, running script'

// Anvil Smashing:
// Converts a Block or IBlockState into an IBlockState when an anvil falls on top of it (from any height).

mods.inspirations.anvil_smashing.removeByInput(blockstate('minecraft:packed_ice'))
mods.inspirations.anvil_smashing.removeByOutput(blockstate('minecraft:cobblestone'))
// mods.inspirations.anvil_smashing.removeAll()

mods.inspirations.anvil_smashing.recipeBuilder()
    .input(blockstate('minecraft:diamond_block'))
    .output(blockstate('minecraft:clay'))
    .register()

mods.inspirations.anvil_smashing.recipeBuilder()
    .input(blockstate('minecraft:clay'))
    .output(blockstate('minecraft:air'))
    .register()



// Cauldron:
// Converts up to 1 itemstack and up to 1 fluid into up to 1 itemstack or up to 1 fluid, with a boiling boolean and
// variable amount of fluid consumed or produced.

mods.inspirations.cauldron.removeByFluidInput(fluid('mushroom_stew'))
mods.inspirations.cauldron.removeByFluidOutput(fluid('beetroot_soup'))
mods.inspirations.cauldron.removeByInput(item('minecraft:ghast_tear'))
mods.inspirations.cauldron.removeByOutput(item('minecraft:piston'))
// mods.inspirations.cauldron.removeAll()

mods.inspirations.cauldron.recipeBuilder()
    .standard()
    .input(item('minecraft:gold_ingot'))
    .fluidInput(fluid('lava'))
    .output(item('minecraft:clay'))
    .boiling()
    .sound(sound('minecraft:block.anvil.destroy'))
    .levels(3)
    .register()

mods.inspirations.cauldron.recipeBuilderBrewing()
    .input(item('minecraft:diamond_block'))
    .inputPotion(potionType('minecraft:fire_resistance'))
    .outputPotion(potionType('minecraft:strength'))
    .register()

mods.inspirations.cauldron.recipeBuilderDye()
    .input(item('minecraft:gold_block'))
    .output(item('minecraft:diamond_block'))
    .dye('blue')
    .levels(2)
    .register()

mods.inspirations.cauldron.recipeBuilderFill()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .fluidInput(fluid('milk'))
    .sound(sound('minecraft:block.anvil.destroy'))
    .register()

mods.inspirations.cauldron.recipeBuilderMix()
    .output(item('minecraft:clay'))
    .fluidInput(fluid('milk'), fluid('lava'))
    .register()

mods.inspirations.cauldron.recipeBuilderPotion()
    .input(item('minecraft:gold_block'))
    .output(item('minecraft:diamond_block'))
    .inputPotion(potionType('minecraft:fire_resistance'))
    .levels(2)
    .register()

mods.inspirations.cauldron.recipeBuilderStandard()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .fluidInput(fluid('lava'))
    .levels(3)
    .sound(sound('minecraft:block.anvil.destroy'))
    .register()

mods.inspirations.cauldron.recipeBuilderTransform()
    .input(item('minecraft:stone:3'))
    .fluidInput(fluid('water'))
    .fluidOutput(fluid('milk'))
    .levels(2)
    .register()


