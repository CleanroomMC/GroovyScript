
// MODS_LOADED: forestry
println 'mod \'forestry\' detected, running script'

// Species Bracket Handler
// While forestry:speciesCommon is the actual name, for convenience, both will work.
species("forestry:common")
species("forestry:speciesCommon")

mods.forestry.Centrifuge.recipeBuilder()
        .time(5) // Optional
        .input(item('minecraft:iron_ingot'))
        .output(item('forestry:ingot_copper') * 2, 0.5F) // Chance is optional
        .output(item('minecraft:apple'))
        .register()

mods.forestry.Squeezer.recipeBuilder()
        .time(5) // Optional
        .fluidOutput(fluid('water') * 100)
        .input(item('forestry:ingot_copper') * 4)
        .output(item('forestry:ingot_bronze')) // Output item is optional
        .register()

mods.forestry.Carpenter.recipeBuilder()
        .time(5) // Optional
        .output(item('minecraft:bedrock'))
        .shape(
                "#B#",
                "#A#",
                "#B#"
        )
        .key('A', item('minecraft:apple'))
        .key('B', item('forestry:ingot_copper'))
        .key('#', ore("beeComb"))
        .fluidInput(fluid('lava') * 500)
        .box(item('minecraft:stone') * 4) // 'Box' item is optional
        .register()

mods.forestry.Still.recipeBuilder()
        .time(150) // Optional
        .fluidInput(fluid('water') * 20)
        .fluidOutput(fluid('lava') * 1)
        .register()

mods.forestry.Moistener.recipeBuilder()
        .time(20) // Optional
        .input(item('minecraft:iron_ingot'))
        .output(item('forestry:ingot_copper'))
        .register()

// Output, Input, Value for that input, What stage of fuel the input is at
mods.forestry.MoistenerFuel.add(item('minecraft:coal'), item('minecraft:diamond'), 80, 3)
//mods.forestry.MoistenerFuel.removeByInput(item('minecraft:diamond'))

mods.forestry.CharcoalPile.add(blockstate('minecraft:iron_block'), 2)
//mods.forestry.CharcoalPile.removeWall(blockstate('minecraft:iron_block'))

mods.forestry.Fermenter.recipeBuilder()
        .fluidInput(fluid('lava'))
        .fluidOutput(fluid('for.honey'))
        .input(ore('beeComb'))
        .value(50) // Optional
        .modifier(10.0F) // Optional
        .register()

mods.forestry.ThermionicFabricator.recipeBuilder()
        .fluidInput(fluid('for.honey') * 50) // Only fluids that have a ThermionicFabricator Smelting recipe will be valid
        .output(item('minecraft:bedrock') * 5)
        .catalyst(item('minecraft:stone') * 8) // Catalyst item is optional
        .shape(
                "#C#",
                "CIC",
                "#C#"
        )
        .key('C', item('forestry:ingot_copper'))
        .key('I', item('minecraft:iron_ingot'))
        .key('#', ore('cobblestone'))
        .register()

// Fluid to create, Item to melt, What temperature is needed to do the melt
mods.forestry.ThermionicFabricator.Smelting.add(fluid('for.honey') * 5, item('forestry:ingot_bronze'), 4000)

// Species to create, First species in mutation, Second species in mutation, Chance of mutation happening
mods.forestry.Mutations.add(species("forestry:noble"), species("forestry:common"), species("forestry:speciesCommon"), 1.0D)

// This mutation requires a hay block as the foundation
mods.forestry.Mutations.add(species("forestry:forest"), species("forestry:noble"), species("forestry:common"), 0.5D, builder -> builder.requireResource(blockstate("minecraft:hay_block")))

// Species that produces this item, Item to produce, Chance of producing, If the item is a special product
mods.forestry.Produce.add(species("forestry:common"), item("minecraft:apple"), 0.5F, true)