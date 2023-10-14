
// MODS_LOADED: integrateddynamics
println 'mod \'integrateddynamics\' detected, running script'

// Drying Basin and Mechanical Drying Basin:
// Takes either an item or fluid input and gives either an item or fluid output after a duration.
mods.integrateddynamics.dryingbasin.recipeBuilder() // Defaults to basic-only, and mechanical must be enabled via `mechanical()`
    .input(item('minecraft:gold_ingot')) // Either an item input or fluid input must be defined, or both
    .output(item('minecraft:clay')) // Either an item output or fluid output must be defined, or both
    .fluidInput(fluid('water') * 500) // Either an item input or fluid input must be defined, or both
    .fluidOutput(fluid('lava') * 2000) // Either an item output or fluid output must be defined, or both
    .mechanical() // By default, the recipeBuilder is set to basic-only. This also adds the recipe to the Mechanical Drying Basin
    .duration(5) // Optional integer. Defaults to 10
    .register()

mods.integrateddynamics.dryingbasin.recipeBuilder()
    .output(item('minecraft:clay'))
    .fluidInput(fluid('water') * 2000)
    .register()

mods.integrateddynamics.mechanicaldryingbasin.recipeBuilder() // Defaults to mechanical-only, and basic must be enabled via `basic()`
    .input(item('minecraft:diamond'))
    .fluidInput(fluid('water') * 50)
    .fluidOutput(fluid('lava') * 20000)
    .duration(300)
    .register()

// Squeezer and Mechanical Squeezer:
// Takes an item and can give up to 3 chanced item outputs and a fluid. 
mods.integrateddynamics.squeezer.recipeBuilder() // Defaults to basic-only, and mechanical must be enabled via `mechanical()`
    .input(item('minecraft:clay'))
    .output(item('minecraft:clay_ball'), 1F) // Between 0 and 3 item outputs with chances can be added.
    .output(item('minecraft:clay_ball') * 2, 0.7F)
    .output(item('minecraft:clay_ball') * 10, 0.2F)
    .fluidOutput(fluid('lava') * 2000) // An output fluid may be defined.
    .mechanical()// By default, the recipeBuilder is set to basic-only. This also adds the recipe to the Mechanical Drying Basin
    .duration(5) // Optional integer. Defaults to 10. Only applies if mechanical is set to true
    .register()

mods.integrateddynamics.squeezer.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'), 0.5F)
    .register()

mods.integrateddynamics.squeezer.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidOutput(fluid('lava') * 10)
    .register()

mods.integrateddynamics.mechanicalsqueezer.recipeBuilder() // Defaults to mechanical-only, and basic must be enabled via `basic()`
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay') * 16, 0.9F)
    .register()
