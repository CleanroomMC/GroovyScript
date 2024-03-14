
// Auto generated groovyscript example file
// MODS_LOADED: integrateddynamics

println 'mod \'integrateddynamics\' detected, running script'

// Drying Basin:
// Takes either an item or fluid input and gives either an item or fluid output after a duration.

// mods.integrateddynamics.drying_basin.removeAll()

mods.integrateddynamics.drying_basin.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .fluidInput(fluid('water') * 500)
    .fluidOutput(fluid('lava') * 2000)
    .mechanical()
    .duration(5)
    .register()

mods.integrateddynamics.drying_basin.recipeBuilder()
    .output(item('minecraft:clay'))
    .fluidInput(fluid('water') * 2000)
    .register()


// Mechanical Drying Basin:
// Takes either an item or fluid input and gives either an item or fluid output after a duration.

// mods.integrateddynamics.mechanical_drying_basin.removeAll()

mods.integrateddynamics.mechanical_drying_basin.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidInput(fluid('water') * 50)
    .fluidOutput(fluid('lava') * 20000)
    .duration(300)
    .register()


// Mechanical Squeezer:
// Takes an item and can give up to 3 chanced item outputs and a fluid.

// mods.integrateddynamics.mechanical_squeezer.removeAll()

mods.integrateddynamics.mechanical_squeezer.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay') * 16, 0.9F)
    .register()


// Squeezer:
// Takes an item and can give up to 3 chanced item outputs and a fluid.

// mods.integrateddynamics.squeezer.removeAll()

mods.integrateddynamics.squeezer.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:clay_ball'), 1F)
    .output(item('minecraft:clay_ball') * 2, 0.7F)
    .output(item('minecraft:clay_ball') * 10, 0.2F)
    .fluidOutput(fluid('lava') * 2000)
    .mechanical()
    .duration(5)
    .register()

mods.integrateddynamics.squeezer.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'), 0.5F)
    .register()

mods.integrateddynamics.squeezer.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidOutput(fluid('lava') * 10)
    .register()


