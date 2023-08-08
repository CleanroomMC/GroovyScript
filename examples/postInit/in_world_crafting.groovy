
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
