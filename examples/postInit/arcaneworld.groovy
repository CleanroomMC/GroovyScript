
// Auto generated groovyscript example file
// MODS_LOADED: arcaneworld

log.info 'mod \'arcaneworld\' detected, running script'

// Ritual:
// Converts up to 5 input itemstacks into a wide number of possible effects, including spawning entities, opening a portal
// to a dungeon dimension to fight a mob, awarding an output itemstack, running commands, and even entirely customized
// effects.

mods.arcaneworld.ritual.removeByInput(item('minecraft:gold_nugget'))
mods.arcaneworld.ritual.removeByOutput(item('arcaneworld:biome_crystal'))
// mods.arcaneworld.ritual.removeAll()

mods.arcaneworld.ritual.recipeBuilder()
    .ritualCreateItem()
    .input(item('minecraft:stone') * 5, item('minecraft:diamond'), item('minecraft:clay'))
    .output(item('minecraft:clay'))
    .translationKey('groovyscript.demo_output')
    .name('groovyscript:custom_name')
    .register()

mods.arcaneworld.ritual.recipeBuilderArena()
    .input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:clay'))
    .translationKey('groovyscript.demo_arena')
    .entity(entity('minecraft:chicken'))
    .register()

mods.arcaneworld.ritual.recipeBuilderCommand()
    .input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:clay'))
    .translationKey('groovyscript.demo_command')
    .command('say hi',
             'give @p minecraft:coal 5')
    .register()

mods.arcaneworld.ritual.recipeBuilderCreateItem()
    .input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'))
    .translationKey('groovyscript.demo_create_item')
    .output(item('minecraft:diamond'))
    .register()

mods.arcaneworld.ritual.recipeBuilderCustom()
    .input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:clay'), item('minecraft:clay'))
    .translationKey('groovyscript.demo_custom')
    .onActivate({ World world, BlockPos blockPos, EntityPlayer player, ItemStack... itemStacks -> { log.info blockPos } })
    .register()

mods.arcaneworld.ritual.recipeBuilderDragonBreath()
    .input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'))
    .translationKey('groovyscript.demo_dragon_breath')
    .register()

mods.arcaneworld.ritual.recipeBuilderDungeon()
    .input(item('minecraft:diamond'), item('minecraft:clay'), item('minecraft:clay'))
    .translationKey('groovyscript.demo_dungeon')
    .register()

mods.arcaneworld.ritual.recipeBuilderSummon()
    .input(item('minecraft:stone'), item('minecraft:clay'), item('minecraft:clay'))
    .translationKey('groovyscript.demo_summon')
    .entity(entity('minecraft:chicken'))
    .register()

mods.arcaneworld.ritual.recipeBuilderTime()
    .input(item('minecraft:diamond'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'))
    .translationKey('groovyscript.demo_time')
    .time(5000)
    .register()

mods.arcaneworld.ritual.recipeBuilderWeather()
    .input(item('minecraft:diamond'), item('minecraft:gold_ingot'), item('minecraft:clay'))
    .translationKey('groovyscript.demo_weather_clear')
    .weatherClear()
    .register()

mods.arcaneworld.ritual.recipeBuilderWeather()
    .input(item('minecraft:gold_ingot'), item('minecraft:diamond'), item('minecraft:clay'))
    .translationKey('groovyscript.demo_weather_rain')
    .weatherRain()
    .register()

mods.arcaneworld.ritual.recipeBuilderWeather()
    .input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:gold_ingot'))
    .translationKey('groovyscript.demo_weather_thunder')
    .weatherThunder()
    .register()


