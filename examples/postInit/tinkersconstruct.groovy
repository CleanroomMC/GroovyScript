
// MODS_LOADED: tconstruct
println 'mod \'tinkers construct\' detected, running script'

// accessing tinker conxtruct
//mods.tconstruct
//mods.ticon
//mods.tinkersconstruct

// Alloying
// requires at least 2 inputs and exactly 1 output
mods.tconstruct.Alloying.recipeBuilder()
    .fluidInput(fluid('manyullyn') * 1)
    .fluidInput(fluid('water') * 999)
    .fluidOutput(fluid('obsidian') * 1000)
    .register()

mods.tconstruct.Alloying.removeByInputs(fluid('water') * 125, fluid('lava') * 125)
mods.tconstruct.Alloying.removeByOutput(fluid('clay'))
mods.tconstruct.Alloying.removeByInputsAndOutput(fluid('manyullyn'), fluid('cobalt') * 2, fluid('ardite') * 2) // first output then inputs

// Casting
// There are 2 types: table and basin casting
//mods.tconstruct.casting.Table.
