
// Auto generated groovyscript example file
// MODS_LOADED: railcraft

log 'mod \'railcraft\' detected, running script'

// groovyscript.wiki.railcraft.blast_furnace.title:
// groovyscript.wiki.railcraft.blast_furnace.description.

mods.railcraft.blast_furnace.removeByInput(item('minecraft:iron_ingot'))
mods.railcraft.blast_furnace.removeByOutput(item('railcraft:ingot:1'))
// mods.railcraft.blast_furnace.removeAll()

mods.railcraft.blast_furnace.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('railcraft:ingot:1'))
    .time(1280)
    .slag(1)
    .register()


// groovyscript.wiki.railcraft.coke_oven.title:
// groovyscript.wiki.railcraft.coke_oven.description.

mods.railcraft.coke_oven.removeByInput(item('minecraft:log'))
mods.railcraft.coke_oven.removeByOutput(item('railcraft:fuel_coke'))
// mods.railcraft.coke_oven.removeAll()

mods.railcraft.coke_oven.recipeBuilder()
    .input(item('minecraft:log'))
    .output(item('railcraft:fuel_coke'))
    .fluidOutput(fluid('creosote') * 500)
    .time(1800)
    .register()


// groovyscript.wiki.railcraft.fluid_fuels.title:
// groovyscript.wiki.railcraft.fluid_fuels.description.

mods.railcraft.fluid_fuels.remove(fluid('creosote'))
// mods.railcraft.fluid_fuels.removeAll()

mods.railcraft.fluid_fuels.add(fluid('lava'), 32000)

// groovyscript.wiki.railcraft.rock_crusher.title:
// groovyscript.wiki.railcraft.rock_crusher.description.

mods.railcraft.rock_crusher.removeByInput(item('minecraft:stone'))
mods.railcraft.rock_crusher.removeByOutput(item('minecraft:cobblestone'))
// mods.railcraft.rock_crusher.removeAll()

mods.railcraft.rock_crusher.recipeBuilder()
    .input(item('minecraft:stone'))
    .output(item('minecraft:cobblestone'), 1.0)
    .output(item('minecraft:sand'), 0.5)
    .time(200)
    .register()


// groovyscript.wiki.railcraft.rolling_machine.title:
// groovyscript.wiki.railcraft.rolling_machine.description.

mods.railcraft.rolling_machine.removeByOutput(item('minecraft:tripwire_hook'))
// mods.railcraft.rolling_machine.removeAll()

mods.railcraft.rolling_machine.shapedBuilder()
    .output(item('minecraft:stone'))
    .matrix('BXX',
            'X B')
    .key('B', item('minecraft:stone'))
    .key('X', item('minecraft:gold_ingot'))
    .time(200)


mods.railcraft.rolling_machine.shapedBuilder()
    .output(item('minecraft:diamond') * 32)
    .matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
             [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
             [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]])
    .time(400)


mods.railcraft.rolling_machine.shapelessBuilder()
    .output(item('minecraft:clay') * 8)
    .input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))
    .time(100)


mods.railcraft.rolling_machine.shapelessBuilder()
    .output(item('minecraft:diamond') * 32)
    .input(item('minecraft:gold_ingot') * 9)
    .time(500)
