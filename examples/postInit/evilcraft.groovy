
// Weather Bracket Handler
weather('clear')
weather('rain')
weather('lightning')


// Blood Infuser:
// Consumes an item, some fluid, and requires a given tier of Promise of Tenacity to produce the output and some experience after a duration.
mods.evilcraft.bloodinfuser.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:clay'))
    .fluidInput(fluid('evilcraftblood') * 1000)
    .tier(3) // Optional integer. Requires at least this tier of Promise of Tenacity to craft. Defaults to 0.
    .duration(100) // Optional integer. Time in ticks for the recipe to complete. Defaults to 0.
    .xp(10000) // Optional float. Experience gained when completing the recipe. Defaults to 0.
    .register()

mods.evilcraft.bloodinfuser.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .fluidInput(100000) // Calling `fluidInput` with just an integer will automatically consider the fluid as "evilcraftblood".
    .register()

mods.evilcraft.bloodinfuser.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay') * 4)
    .fluidInput(5000) // `blood` can also be used as an alias for `fluidInput` when only an integer is used.
    .tier(1)
    .register()

mods.evilcraft.bloodinfuser.removeByInput(item('evilcraft:dark_gem'))
mods.evilcraft.bloodinfuser.removeByOutput(item('minecraft:leather'))
//mods.evilcraft.bloodinfuser.removeAll()


// Environmental Accumulator:
// Consumes an item to give an output, possibly changing the weather. Has a cooldown time or a blood cost.
mods.evilcraft.environmentalaccumulator.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:clay') * 2)
    .inputWeather(weather('clear'))
    .outputWeather(weather('rain'))
    .processingspeed(1) // Optional doube. Controls the visual rotation of the item while crafting. Defaults to the amount set in the config.
    .cooldowntime(1000) // Optional integer. Time it takes before another recipe can be run. Defaults to the time set in the config.
    // cooldowntime also controls the amount of evilcraftblood consumed by the Sanguinary Environmental Accumulator
    .duration(10) // Optional integer. Time it takes to complete the recipe. Defaults to the time set in the config.
    .register()

mods.evilcraft.environmentalaccumulator.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .inputWeather(weather('rain'))
    .outputWeather(weather('lightning'))
    .speed(10) // Short for processingspeed.
    .cooldown(1) // Short for cooldowntime.
    .register()

mods.evilcraft.environmentalaccumulator.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay') * 16)
    .inputWeather(weather('lightning'))
    .outputWeather(weather('lightning'))
    .register()


mods.evilcraft.environmentalaccumulator.removeByInput(item('evilcraft:exalted_crafter:1'))
mods.evilcraft.environmentalaccumulator.removeByOutput(item('evilcraft:exalted_crafter:2'))
//mods.evilcraft.environmentalaccumulator.removeAll()

