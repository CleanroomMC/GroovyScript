
// Auto generated groovyscript example file
// MODS_LOADED: evilcraft

println 'mod \'evilcraft\' detected, running script'

// Blood Infuser:
// Consumes an item, some fluid, and requires a given tier of Promise of Tenacity to produce the output and some experience
// after a duration.

mods.evilcraft.blood_infuser.removeByInput(item('evilcraft:dark_gem'))
mods.evilcraft.blood_infuser.removeByOutput(item('minecraft:leather'))
// mods.evilcraft.blood_infuser.removeAll()

mods.evilcraft.blood_infuser.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:clay'))
    .fluidInput(fluid('evilcraftblood') * 1000)
    .tier(3)
    .duration(100)
    .xp(10000)
    .register()

mods.evilcraft.blood_infuser.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .fluidInput(100000)
    .register()

mods.evilcraft.blood_infuser.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay') * 4)
    .fluidInput(5000)
    .tier(1)
    .register()


// Environmental Accumulator:
// Consumes an item to give an output, possibly changing the weather. Has a cooldown time or a blood cost.

mods.evilcraft.environmental_accumulator.removeByInput(item('evilcraft:exalted_crafter:1'))
mods.evilcraft.environmental_accumulator.removeByOutput(item('evilcraft:exalted_crafter:2'))
// mods.evilcraft.environmental_accumulator.removeAll()

mods.evilcraft.environmental_accumulator.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:clay') * 2)
    .inputWeather(weather('clear'))
    .outputWeather(weather('rain'))
    .processingspeed(1)
    .cooldowntime(1000)
    .duration(10)
    .register()

mods.evilcraft.environmental_accumulator.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .inputWeather(weather('rain'))
    .outputWeather(weather('lightning'))
    .speed(10)
    .cooldown(1)
    .register()

mods.evilcraft.environmental_accumulator.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay') * 16)
    .inputWeather(weather('lightning'))
    .outputWeather(weather('lightning'))
    .register()


