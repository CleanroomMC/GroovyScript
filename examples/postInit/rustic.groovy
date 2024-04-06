
// Auto generated groovyscript example file
// MODS_LOADED: rustic

import net.minecraft.potion.PotionEffect

println 'mod \'rustic\' detected, running script'

// Alchemy Condenser:
// Converts some number of input itemstacks and a fluidstack into a single output stack after a time in a small multiblock
// structure, with a basic and advanced tier.

mods.rustic.alchemy.removeByInput(item('minecraft:sugar'))
mods.rustic.alchemy.removeByOutput(item('rustic:elixir').withNbt(['ElixirEffects': [['Effect': 'minecraft:night_vision', 'Duration': 3600, 'Amplifier': 0]]]))
// mods.rustic.alchemy.removeAll()

mods.rustic.alchemy.recipeBuilder()
    .input(item('minecraft:stone'), item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 4)
    .time(20)
    .register()

mods.rustic.alchemy.recipeBuilder()
    .input(item('minecraft:stone'), item('minecraft:gold_ingot'), item('minecraft:diamond'))
    .bottle(item('minecraft:torch'))
    .advanced()
    .effect(new PotionEffect(potion('minecraft:night_vision'), 3600, 1))
    .register()

mods.rustic.alchemy.recipeBuilder()
    .input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))
    .modifier(item('minecraft:clay'))
    .fluidInput(fluid('lava') * 500)
    .advanced()
    .output(item('minecraft:diamond'))
    .register()

mods.rustic.alchemy.recipeBuilder()
    .input(item('minecraft:cobblestone'), item('minecraft:cobblestone'))
    .fluidInput(fluid('lava') * 25)
    .bottle(item('minecraft:bucket'))
    .output(item('minecraft:lava_bucket'))
    .register()


// Brewing Barrel:
// Converts a fluid into another fluid after a long period of time. If the fluid is an instanceof FluidBooze, has a
// variable Quality that can be refined through further cycles of conversion.

mods.rustic.brewing_barrel.removeByInput(fluid('ironberryjuice'))
mods.rustic.brewing_barrel.removeByOutput(fluid('ale'))
// mods.rustic.brewing_barrel.removeAll()

mods.rustic.brewing_barrel.recipeBuilder()
    .fluidInput(fluid('ironberryjuice'))
    .fluidOutput(fluid('lava'))
    .register()

mods.rustic.brewing_barrel.recipeBuilder()
    .fluidInput(fluid('water'))
    .fluidOutput(fluid('lava'))
    .register()


// Crushing Tub:
// Convert items into a fluidstacks and optionally itemstacks when any entity, typically a player, lands atop it.

mods.rustic.crushing_tub.removeByInput(item('rustic:wildberries'))
mods.rustic.crushing_tub.removeByOutput(fluid('ironberryjuice'))
mods.rustic.crushing_tub.removeByOutput(item('minecraft:sugar'))
// mods.rustic.crushing_tub.removeAll()

mods.rustic.crushing_tub.recipeBuilder()
    .input(item('minecraft:stone'))
    .fluidOutput(fluid('lava') * 50)
    .register()

mods.rustic.crushing_tub.recipeBuilder()
    .input(item('minecraft:clay'))
    .fluidOutput(fluid('lava') * 20)
    .byproduct(item('minecraft:gold_ingot') * 4)
    .register()


// Drying Basin:
// Converts fluids into itemstacks over time.

mods.rustic.evaporating_basin.removeByInput(fluid('ironberryjuice'))
// mods.rustic.evaporating_basin.removeByOutput(item('rustic:dust_tiny_iron'))
// mods.rustic.evaporating_basin.removeAll()

mods.rustic.evaporating_basin.recipeBuilder()
    .fluidInput(fluid('water') * 200)
    .output(item('minecraft:clay'))
    .register()

mods.rustic.evaporating_basin.recipeBuilder()
    .fluidInput(fluid('lava') * 50)
    .output(item('minecraft:iron_ingot'))
    .register()


