
// MODS_LOADED: multiblocked
println 'mod \'multiblocked\' detected, running script'

def mapId = "test"

mods.multiblocked.recipeMap(mapId).recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:clay'))
    .time(100)
    .register()

mods.multiblocked.recipeMap(mapId).recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .input(mbd.fluid(fluid('lava') * 5).perTick().chance(0.5))
    .output(mbd.item(item('minecraft:diamond')).chance(0.5))
    .input(mbd.durability(item('minecraft:diamond_pickaxe', 32767), 10))
    .condition(mbd.thunder())
    .condition(mbd.yRange(100, 200))
    .time(20)
    .register()