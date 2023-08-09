
inWorldCrafting.fluidToFluid.recipeBuilder()
        .fluidInput(fluid('water'))
        .input(item('minecraft:diamond') * 2)
        .fluidOutput(fluid('lava'))
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
