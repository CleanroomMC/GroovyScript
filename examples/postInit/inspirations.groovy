
if (!isLoaded('inspirations')) return
println 'mod \'inspirations\' detected, running script'

// Cauldron:
// Converts up to 1 itemstack and up to 1 fluid into up to 1 itemstack or up to 1 fluid, with a boiling boolean and variable amount of fluid consumed or produced.
// Cauldrons have a cap of either 3 or 4 levels, depending on the config.
mods.inspirations.cauldron.recipeBuilder()
    .standard() // Optional, one type is required, it can either be done in a normal recipeBuilder or preset via a recipeBuilder variant.
    .input(item('minecraft:gold_ingot'))
    .fluidInput(fluid('lava'))
    .output(item('minecraft:clay'))
    .boiling()
    .sound(sound('block.anvil.destroy'))
    .levels(3)
    .register()

mods.inspirations.cauldron.recipeBuilderStandard() // Requires 1 input, 1 output, 1 fluid input, 0 < levels < 3/4, and a sound
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .fluidInput(fluid('lava'))
    .levels(3)
    .sound(sound('block.anvil.destroy'))
    .register()

mods.inspirations.cauldron.recipeBuilderTransform() // Requires 1 input, 1 fluid input, 1 fluid output, 0 < levels < 3/4
    .input(item('minecraft:stone:3'))
    .fluidInput(fluid('water'))
    .fluidOutput(fluid('milk'))
    .levels(2)
    .register()

mods.inspirations.cauldron.recipeBuilderMix() // Requires 1 output and 2 fluid inputs
    .output(item('minecraft:clay'))
    .fluidInput(fluid('milk'), fluid('lava'))
    .register()

mods.inspirations.cauldron.recipeBuilderFill() // Requires 1 input, 1 output, 1 fluid input, and a sound
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .fluidInput(fluid('milk'))
    .sound(sound('block.anvil.destroy'))
    .register()

mods.inspirations.cauldron.recipeBuilderBrewing() // Requires 1 input, 1 input potion, and 1 output potion
    .input(item('minecraft:diamond_block'))
    .inputPotion(potionType('fire_resistance'))
    .outputPotion(potionType('strength'))
    .register()

mods.inspirations.cauldron.recipeBuilderPotion() // Requires 1 input, 1 output, 1 input potion, and 0 < levels < 3/4
    .input(item('minecraft:gold_block'))
    .output(item('minecraft:diamond_block'))
    .inputPotion(potionType('fire_resistance'))
    .levels(2)
    .register()

mods.inspirations.cauldron.recipeBuilderDye() // Requires 1 input, 1 output, 1 dye, and 0 < levels < 3/4
    .input(item('minecraft:gold_block'))
    .output(item('minecraft:diamond_block'))
    .dye('blue')
    .levels(2)
    .register()


// Note: some recipes (banners, potions) cannot be removed.
mods.inspirations.cauldron.removeByInput(item('minecraft:ghast_tear'))
mods.inspirations.cauldron.removeByOutput(item('minecraft:piston'))
mods.inspirations.cauldron.removeByFluidOutput(fluid('beetroot_soup'))
mods.inspirations.cauldron.removeByFluidInput(fluid('mushroom_stew'))

//mods.inspirations.cauldron.removeAll()


// Anvil Smashing:
// Converts a Block or IBlockState into an IBlockState when an anvil falls on top of it (from any height).
mods.inspirations.anvilsmashing.recipeBuilder()
    .input(blockstate('minecraft:diamond_block'))
    .output(blockstate('minecraft:clay'))
    .register()

mods.inspirations.anvilsmashing.recipeBuilder()
    .input(blockstate('minecraft:clay'))
    .output(blockstate('minecraft:air'))
    .register()

mods.inspirations.anvilsmashing.removeByInput(blockstate('minecraft:packed_ice'))
mods.inspirations.anvilsmashing.removeByOutput(blockstate('minecraft:cobblestone'))

//mods.inspirations.anvilsmashing.removeAll()

