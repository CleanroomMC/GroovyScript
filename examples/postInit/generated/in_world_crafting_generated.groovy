
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

in_world_crafting.burning.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .ticks(100)
    .register()

in_world_crafting.burning.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .ticks(10)
    .startCondition({ stack -> stack.getItem().getCount() > 5 })
    .register()


// Explosion Conversion:
// Converts an input itemstack into an output itemstack, with an optional fail rate.

// in_world_crafting.explosion.removeAll()

in_world_crafting.explosion.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:nether_star'))
    .chance(0.4f)
    .register()

in_world_crafting.explosion.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .startCondition({ entityItem, itemStack -> entityItem.posY <= 60 })
    .register()

in_world_crafting.explosion.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()


// Fluid and ItemStack To Block Conversion:
// Converts any number of input itemstacks and a fluid source block into a block in-world, with each input having a chance
// to be consumed. Allows an additional closure check to start the recipe and a closure run after the recipe is finished.

// in_world_crafting.fluid_to_block.removeByInput(fluid('water'))
// in_world_crafting.fluid_to_block.removeByInput(fluid('water'), item('minecraft:clay'))
// in_world_crafting.fluid_to_block.removeAll()

in_world_crafting.fluid_to_block.recipeBuilder()
    .fluidInput(fluid('water'))
    .input(item('minecraft:clay_ball'))
    .output(block('minecraft:diamond_block'))
    .register()

in_world_crafting.fluid_to_block.recipeBuilder()
    .fluidInput(fluid('water'))
    .input(item('minecraft:clay'), 0.5f)
    .output(block('minecraft:gold_block'))
    .register()

in_world_crafting.fluid_to_block.recipeBuilder()
    .fluidInput(fluid('water'))
    .input(item('minecraft:diamond'), item('minecraft:iron_ingot'))
    .output(block('minecraft:clay'))
    .startCondition({ world, pos -> pos.getY() > 50 })
    .register()


// Fluid and ItemStack To Fluid Conversion:
// Converts any number of input itemstacks and a fluid source block into a fluid block in-world, with each input having a
// chance to be consumed. Allows an additional closure check to start the recipe and a closure run after the recipe is
// finished.

// in_world_crafting.fluid_to_fluid.removeByInput(fluid('water'))
// in_world_crafting.fluid_to_fluid.removeByInput(fluid('water'), item('minecraft:clay'))
// in_world_crafting.fluid_to_fluid.removeAll()

in_world_crafting.fluid_to_fluid.recipeBuilder()
    .fluidInput(fluid('water'))
    .input(item('minecraft:diamond'), item('minecraft:dirt'))
    .fluidOutput(fluid('lava'))
    .register()

in_world_crafting.fluid_to_fluid.recipeBuilder()
    .fluidInput(fluid('water'))
    .input(item('minecraft:diamond'), item('minecraft:gold_nugget'))
    .fluidOutput(fluid('lava'))
    .register()

in_world_crafting.fluid_to_fluid.recipeBuilder()
    .fluidInput(fluid('water'))
    .input(item('minecraft:diamond'), item('minecraft:diamond_block'))
    .fluidOutput(fluid('lava'))
    .startCondition({ world, pos -> pos.getY() < 50 })
    .register()


// Fluid and ItemStack To ItemStack Conversion:
// Converts any number of input itemstacks and a fluid source block into an itemstack in-world, with each input having a
// chance to be consumed and a chance to consume the fluid block. Allows an additional closure check to start the recipe
// and a closure run after the recipe is finished.

// in_world_crafting.fluid_to_item.removeByInput(fluid('water'))
// in_world_crafting.fluid_to_item.removeByInput(fluid('water'), item('minecraft:clay'))
// in_world_crafting.fluid_to_item.removeAll()

in_world_crafting.fluid_to_item.recipeBuilder()
    .fluidInput(fluid('water'), 0.22f)
    .input(item('minecraft:netherrack'))
    .input(item('minecraft:gold_ingot'), 0.1f)
    .output(item('minecraft:nether_star'))
    .register()

in_world_crafting.fluid_to_item.recipeBuilder()
    .fluidInput(fluid('water'))
    .fluidConsumptionChance(0.9f)
    .input(item('minecraft:diamond'), item('minecraft:gold_block'))
    .output(item('minecraft:diamond') * 10)
    .startCondition({ world, pos -> pos.getY() > 50 })
    .register()

in_world_crafting.fluid_to_item.recipeBuilder()
    .fluidInput(fluid('water'))
    .input(item('minecraft:diamond'), item('minecraft:iron_block') * 3)
    .output(item('minecraft:gold_ingot'))
    .afterRecipe({ world, pos -> world.setBlockState(pos, block('minecraft:dirt')) })
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

in_world_crafting.piston_push.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .register()

in_world_crafting.piston_push.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot'))
    .startCondition({entityItem, itemStack, pushingAgainst -> pushingAgainst.getBlock() == block('minecraft:clay') })
    .register()
