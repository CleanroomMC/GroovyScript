
// Auto generated groovyscript example file
// MODS_LOADED: draconicevolution

println 'mod \'draconicevolution\' detected, running script'

// Energy Core:
// A multiblock with 8 tiers for storing large amounts of energy.

mods.draconicevolution.energy_core.setInnerBlock(7, blockstate('minecraft:stone', 1))
mods.draconicevolution.energy_core.setOuterBlock(2, blockstate('minecraft:diamond_block'))
mods.draconicevolution.energy_core.setOuterBlock(7, blockstate('minecraft:diamond_block'))

// Fusion:
// Consumes items and power from up to 54 pedestals of at least a given tier pointing towards a Fusion Crafting Core
// containing a catalyst to produce an output item.

mods.draconicevolution.fusion.removeByCatalyst(item('draconicevolution:chaos_shard'))
// mods.draconicevolution.fusion.removeAll()

mods.draconicevolution.fusion.recipeBuilder()
    .catalyst(item('minecraft:diamond'))
    .input(ore('ingotIron'), ore('ingotIron'), item('minecraft:dirt'), item('minecraft:grass'), item('minecraft:grass'), item('minecraft:dirt'), ore('ingotGold'), ore('ingotGold'))
    .output(item('minecraft:nether_star'))
    .energy(10)
    .tier(1)
    .register()

mods.draconicevolution.fusion.recipeBuilder()
    .catalyst(item('minecraft:diamond'))
    .input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'))
    .output(item('minecraft:nether_star'))
    .energy(100000)
    .tierChaotic()
    .register()


