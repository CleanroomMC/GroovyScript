
// Auto generated groovyscript example file
// MODS_LOADED: pyrotech

log.info 'mod \'pyrotech\' detected, running script'

// Anvil:
// When using hammer or pickaxe it can convert items.

mods.pyrotech.anvil.removeByOutput(item('minecraft:stone_slab', 3))
// mods.pyrotech.anvil.removeAll()

mods.pyrotech.anvil.recipeBuilder()
    .input(item('minecraft:diamond') * 4)
    .output(item('minecraft:emerald') * 2)
    .hits(5)
    .typeHammer()
    .tierGranite()
    .name('diamond_to_emerald_granite_anvil')
    .register()

mods.pyrotech.anvil.recipeBuilder()
    .input(item('minecraft:diamond') * 8)
    .output(item('minecraft:nether_star') * 1)
    .hits(10)
    .typePickaxe()
    .tierIronclad()
    .name('diamond_to_nether_star_ironclad_anvil')
    .register()

mods.pyrotech.anvil.recipeBuilder()
    .input(item('minecraft:diamond') * 4)
    .output(item('minecraft:gold_ingot') * 16)
    .hits(5)
    .typePickaxe()
    .tierObsidian()
    .name('diamond_to_gold_obsidian_anvil')
    .register()


mods.pyrotech.anvil.add('iron_to_clay', ore('ingotIron'), item('minecraft:clay_ball'), 9, 'granite', 'hammer')

// Barrel:
// Over time converts a fluid with four items into a new fluid.

mods.pyrotech.barrel.removeByOutput(fluid('freckleberry_wine') * 1000)
// mods.pyrotech.barrel.removeAll()

mods.pyrotech.barrel.recipeBuilder()
    .input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:emerald'))
    .fluidInput(fluid('water') * 1000)
    .fluidOutput(fluid('amongium') * 1000)
    .duration(1000)
    .name('diamond_emerald_and_water_to_amongium')
    .register()


mods.pyrotech.barrel.add('iron_dirt_water_to_lava', ore('ingotIron'), ore('ingotIron'), item('minecraft:dirt'), item('minecraft:dirt'), fluid('water'), fluid('lava'), 1000)

// Refractory Kiln:
// Converts an item into a new one by burning it. Has a chance to fail.

mods.pyrotech.brick_kiln.removeByOutput(item('pyrotech:bucket_clay'))
// mods.pyrotech.brick_kiln.removeAll()

mods.pyrotech.brick_kiln.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .burnTime(400)
    .failureChance(1f)
    .failureOutput(item('minecraft:wheat'), item('minecraft:carrot'), item('minecraft:sponge'))
    .name('iron_to_gold_kiln_with_failure_items_brick')
    .register()


mods.pyrotech.brick_kiln.add('clay_to_iron_brick', item('minecraft:clay_ball') * 5, item('minecraft:iron_ingot'), 1200, 0.5f, item('minecraft:dirt'), item('minecraft:cobblestone'))

// Refractory Oven:
// When powered by burning fuel can convert items.

mods.pyrotech.brick_oven.removeByInput(item('minecraft:porkchop'))
mods.pyrotech.brick_oven.removeByOutput(item('minecraft:cooked_porkchop'))
// mods.pyrotech.brick_oven.removeAll()

mods.pyrotech.brick_oven.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .duration(400)
    .name('diamond_campfire_to_emerald_brick')
    .register()


mods.pyrotech.brick_oven.add('apple_to_dirt_brick', item('minecraft:apple'), item('minecraft:dirt'), 1000)

// Campfire:
// When powered by burning logs can convert items.

mods.pyrotech.campfire.removeByInput(item('minecraft:porkchop'))
mods.pyrotech.campfire.removeByOutput(item('minecraft:cooked_porkchop'))
// mods.pyrotech.campfire.removeAll()

mods.pyrotech.campfire.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .duration(400)
    .name('diamond_campfire_to_emerald')
    .register()


mods.pyrotech.campfire.add('apple_to_dirt', item('minecraft:apple'), item('minecraft:dirt'), 1000)

// Chopping Block:
// When using a axe it can convert items.

mods.pyrotech.chopping_block.removeByInput(item('minecraft:log2'))
mods.pyrotech.chopping_block.removeByOutput(item('minecraft:planks', 4))
// mods.pyrotech.chopping_block.removeAll()

mods.pyrotech.chopping_block.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .chops(25, 1)
    .chops(20, 1)
    .chops(15, 1)
    .chops(10, 2)
    .name('diamond_to_emerald_chopping_block')
    .register()


// Compacting Bin:
// When using a shovel it can convert items.

mods.pyrotech.compacting_bin.removeByInput(item('minecraft:snowball'))
mods.pyrotech.compacting_bin.removeByOutput(item('minecraft:bone_block'))
// mods.pyrotech.compacting_bin.removeAll()

mods.pyrotech.compacting_bin.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .toolUses(5)
    .name('diamond_to_emerald_compacting_bin')
    .register()


mods.pyrotech.compacting_bin.add('iron_to_clay', ore('ingotIron') * 5, item('minecraft:clay_ball') * 20, 9)

// Compost Bin:
// Can convert multiple items into a new one when its full.

mods.pyrotech.compost_bin.removeByInput(item('minecraft:golden_carrot'))
// mods.pyrotech.compost_bin.removeAll()

mods.pyrotech.compost_bin.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald') * 4)
    .compostValue(25)
    .name('diamond_to_emerald_compost_bin')
    .register()


mods.pyrotech.compost_bin.add('iron_to_clay2', ore('ingotIron') * 5, item('minecraft:clay_ball') * 20, 2)

// Crude Drying Rack:
// Converts an item over time into a new one.

mods.pyrotech.crude_drying_rack.removeByInput(item('minecraft:wheat'))
// mods.pyrotech.crude_drying_rack.removeAll()

mods.pyrotech.crude_drying_rack.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .dryTime(260)
    .name('diamond_to_emerald_crude_drying_rack')
    .register()


mods.pyrotech.crude_drying_rack.add('apple_to_dirt', item('minecraft:apple'), item('minecraft:dirt'), 1200)

// Drying Rack:
// Converts an item over time into a new one.

mods.pyrotech.drying_rack.removeByInput(item('minecraft:wheat'))
// mods.pyrotech.drying_rack.removeAll()

mods.pyrotech.drying_rack.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .dryTime(260)
    .name('iron_to_gold_drying_rack')
    .register()


mods.pyrotech.drying_rack.add('apple_to_dirt', item('minecraft:apple'), item('minecraft:dirt'), 1200)

// Pit Kiln:
// Converts an item into a new one by burning it. Has a chance to fail.

mods.pyrotech.pit_kiln.removeByOutput(item('pyrotech:bucket_clay'))
// mods.pyrotech.pit_kiln.removeAll()

mods.pyrotech.pit_kiln.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .burnTime(400)
    .failureChance(1f)
    .failureOutput(item('minecraft:wheat'), item('minecraft:carrot'), item('minecraft:sponge'))
    .name('iron_to_gold_kiln_with_failure_items')
    .register()


mods.pyrotech.pit_kiln.add('clay_to_iron', item('minecraft:clay_ball') * 5, item('minecraft:iron_ingot'), 1200, 0.5f, [item('minecraft:dirt'), item('minecraft:cobblestone')])

// Soaking Pot:
// Converts an item into a new one by soaking it in a liquid. Can require a campfire.

mods.pyrotech.soaking_pot.removeByOutput(item('pyrotech:material', 54))
// mods.pyrotech.soaking_pot.removeAll()

mods.pyrotech.soaking_pot.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidInput(fluid('amongium') * 125)
    .output(item('minecraft:emerald'))
    .time(400)
    .campfireRequired(true)
    .name('diamond_to_emerald_with_amongium_soaking_pot')
    .register()


mods.pyrotech.soaking_pot.add('dirt_to_apple', item('minecraft:dirt'), fluid('water'), item('minecraft:apple'), 1200)

// Stone Kiln:
// Converts an item into a new one by burning it. Has a chance to fail.

mods.pyrotech.stone_kiln.removeByOutput(item('pyrotech:bucket_clay'))
// mods.pyrotech.stone_kiln.removeAll()

mods.pyrotech.stone_kiln.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .burnTime(400)
    .failureChance(1f)
    .failureOutput(item('minecraft:wheat'), item('minecraft:carrot'), item('minecraft:sponge'))
    .name('iron_to_gold_kiln_with_failure_items_stone')
    .register()


mods.pyrotech.stone_kiln.add('clay_to_iron_stone', item('minecraft:clay_ball') * 5, item('minecraft:iron_ingot'), 1200, 0.5f, item('minecraft:dirt'), item('minecraft:cobblestone'))

// Stone Oven:
// When powered by burning fuel can convert items.

mods.pyrotech.stone_oven.removeByInput(item('minecraft:porkchop'))
mods.pyrotech.stone_oven.removeByOutput(item('minecraft:cooked_porkchop'))
// mods.pyrotech.stone_oven.removeAll()

mods.pyrotech.stone_oven.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .duration(400)
    .name('diamond_campfire_to_emerald_stone')
    .register()


mods.pyrotech.stone_oven.add('apple_to_dirt_stone', item('minecraft:apple'), item('minecraft:dirt'), 1000)

// Tanning Rack:
// Converts an item over time into a new one.

mods.pyrotech.tanning_rack.removeByInput(item('minecraft:wheat'))
// mods.pyrotech.tanning_rack.removeAll()

mods.pyrotech.tanning_rack.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .dryTime(260)
    .name('iron_to_gold_drying_rack')
    .register()


mods.pyrotech.tanning_rack.add('apple_to_dirt', item('minecraft:apple'), item('minecraft:dirt'), 1200, item('minecraft:clay_ball'))

