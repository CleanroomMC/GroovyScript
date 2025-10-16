
// Auto generated groovyscript example file

log 'running In-World Crafting example'

// Burning Conversion:
// Converts an input item into an output itemstack after some number of ticks while burning. This also makes the input item
// effectively fireproof.

// in_world_crafting.burning.removeAll()

in_world_crafting.burning.recipeBuilder()
    .input(item('minecraft:netherrack'))
    .output(item('minecraft:nether_star'))
    .register()


// Explosion Conversion:
// Converts an input itemstack into an output itemstack, with an optional fail rate.

// in_world_crafting.explosion.removeAll()

in_world_crafting.explosion.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:nether_star'))
    .chance(0.4f)
    .register()


// Fluid and ItemStack To Block Conversion:
// Converts any number of input itemstacks and a fluid source block into a block in-world, with each input having a chance
// to be consumed. Allows an additional closure check to start the recipe and a closure run after the recipe is finished.

in_world_crafting.fluid_to_block.recipeBuilder()
    .fluidInput(fluid('water'))
    .input(item('minecraft:clay_ball'))
    .output(block('minecraft:diamond_block'))
    .register()


// Fluid and ItemStack To Fluid Conversion:
// Converts any number of input itemstacks and a fluid source block into a fluid block in-world, with each input having a
// chance to be consumed. Allows an additional closure check to start the recipe and a closure run after the recipe is
// finished.

in_world_crafting.fluid_to_fluid.recipeBuilder()
    .fluidInput(fluid('water'))
    .input(item('minecraft:diamond') * 2)
    .fluidOutput(fluid('lava'))
    .register()


// Fluid and ItemStack To ItemStack Conversion:
// Converts any number of input itemstacks and a fluid source block into an itemstack in-world, with each input having a
// chance to be consumed and a chance to consume the fluid block. Allows an additional closure check to start the recipe
// and a closure run after the recipe is finished.

in_world_crafting.fluid_to_item.recipeBuilder()
    .fluidInput(fluid('water'), 0.22f)
    .input(item('minecraft:netherrack'))
    .input(item('minecraft:gold_ingot'), 0.1f)
    .output(item('minecraft:nether_star'))
    .register()


// Piston Pushing Conversion:
// Converts an input item into an output item when a piston pushes the item into a block, with an optional minimum harvest
// level requirement for the block. Amount converted in each entity item per push is configurable.

// in_world_crafting.piston_push.removeAll()

in_world_crafting.piston_push.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .minHarvestLevel(2)
    .maxConversionsPerPush(3)
    .register()
