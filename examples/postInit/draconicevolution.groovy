
if (!isLoaded('draconicevolution')) return
println 'mod \'draconicevolution\' detected, running script'

// Fusion:
// Consumes items and power from up to 54 pedestals of at least a given tier pointing towards a Fusion Crafting Core containing a catalyst to produce an output item.
mods.draconicevolution.fusion.recipeBuilder()
    .catalyst(item('minecraft:diamond'))
    .input(ore('ingotIron'), ore('ingotIron'), item('minecraft:dirt'), item('minecraft:grass'), item('minecraft:grass'), item('minecraft:dirt'), ore('ingotGold'), ore('ingotGold'))
    .output(item('minecraft:nether_star'))
    .energy(10) // Energy cost per item. Optional, default 1000000 (1 million)
    .tier(1) // Optional, default 0 (basic)
    .register()

mods.draconicevolution.fusion.recipeBuilder()
    .catalyst(item('minecraft:diamond'))
    .input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'))
    .output(item('minecraft:nether_star'))
    .energy(100000)
    //.tierNormal() // Alias for tier(0)
    //.tierBasic() // Alias for tier(0)
    //.tierWyvern() // Alias for tier(1)
    //.tierDraconic() // Alias for tier(2)
    .tierChaotic() // Alias for tier(3)
    .register()

mods.draconicevolution.fusion.removeByCatalyst(item('draconicevolution:chaos_shard'))
//mods.draconicevolution.fusion.removeAll()
