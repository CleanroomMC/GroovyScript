
inWorldCrafting.fluidToFluid.recipeBuilder()
        .fluidInput(fluid('water'))
        .input(item('minecraft:diamond') * 2)
        .fluidOutput(fluid('lava'))
        .register()

inWorldCrafting.fluidToItem.recipeBuilder()
        .fluidInput(fluid('water'))
        .input(item('minecraft:netherrack'))
        .input(item('minecraft:gold_ingot'), 0.1f)
        .output(item('minecraft:nether_star'))
        .register()

inWorldCrafting.fluidToBlock.recipeBuilder()
        .fluidInput(fluid('water'))
        .input(item('minecraft:clay_ball'))
        .output(block('minecraft:diamond_block'))
        .register()

inWorldCrafting.explosion.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:nether_star'))
    .chance(0.4f)
    .register()

inWorldCrafting.burning.recipeBuilder()
        .input(item('minecraft:netherrack'))
        .output(item('minecraft:nether_star'))
        //.ticks(40f)
        .register()

inWorldCrafting.pistonPush.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .minHarvestLevel(2)
    .maxConversionsPerPush(3)
    .register()