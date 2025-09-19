
// Auto generated groovyscript example file
// MODS_LOADED: pyrotech

log 'mod \'pyrotech\' detected, running script'

// Anvil:
// When using hammer or pickaxe it can convert items.

mods.pyrotech.anvil.removeByInput(item('pyrotech:material:37'))
mods.pyrotech.anvil.removeByOutput(item('minecraft:stone_slab:3') * 2)
// mods.pyrotech.anvil.removeAll()

mods.pyrotech.anvil.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald') * 2)
    .hits(8)
    .typeHammer()
    .tierGranite()
    .name('diamond_to_emerald_granite_anvil')
    .register()

mods.pyrotech.anvil.recipeBuilder()
    .input(item('minecraft:bedrock'))
    .output(item('minecraft:nether_star') * 1)
    .hits(10)
    .typePickaxe()
    .tierIronclad()
    .inherit(true)
    .name('bedrock_to_nether_star')
    .register()

mods.pyrotech.anvil.recipeBuilder()
    .input(item('minecraft:gold_block'))
    .output(item('minecraft:gold_ingot') * 16)
    .hits(5)
    .typePickaxe()
    .tierObsidian()
    .name('gold_block_to_gold_obsidian_anvil')
    .register()


mods.pyrotech.anvil.add('flint_from_gravel', ore('gravel'), item('minecraft:flint'), 5, 'granite', 'pickaxe', true)

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

mods.pyrotech.brick_kiln.removeByInput(item('minecraft:cobblestone'))
mods.pyrotech.brick_kiln.removeByOutput(item('pyrotech:bucket_clay'))
// mods.pyrotech.brick_kiln.removeAll()

mods.pyrotech.brick_kiln.recipeBuilder()
    .input(item('minecraft:fish'))
    .output(item('minecraft:cooked_fish'))
    .burnTime(200000)
    .failureChance(0.99f)
    .failureOutput(item('minecraft:dragon_egg'), item('minecraft:dragon_breath'))
    .name('meaning_of_life')
    .register()


mods.pyrotech.brick_kiln.add('beetroot_soup', item('minecraft:beetroot'), item('minecraft:beetroot_soup'), 1200, 0.1f, item('minecraft:beetroot_seeds'))

// Refractory Oven:
// When powered by burning fuel can convert items.

mods.pyrotech.brick_oven.removeByInput(item('minecraft:porkchop'))
mods.pyrotech.brick_oven.removeByOutput(item('minecraft:cooked_porkchop'))
// mods.pyrotech.brick_oven.removeAll()

mods.pyrotech.brick_oven.recipeBuilder()
    .input(item('minecraft:chorus_fruit'))
    .output(item('minecraft:chorus_fruit_popped'))
    .duration(800)
    .name('chorus_fruit_whats_popping')
    .register()


mods.pyrotech.brick_oven.add('lead_poisoning', item('minecraft:slime_ball'), item('minecraft:lead') * 16, 1000)

// groovyscript.wiki.pyrotech.brick_sawmill.title:
// groovyscript.wiki.pyrotech.brick_sawmill.description.

mods.pyrotech.brick_sawmill.removeByInput(item('minecraft:planks:1'))
mods.pyrotech.brick_sawmill.removeByOutput(item('pyrotech:material:23'))
// mods.pyrotech.brick_sawmill.removeAll()

mods.pyrotech.brick_sawmill.recipeBuilder()
    .input(item('minecraft:golden_helmet'))
    .output(item('minecraft:gold_ingot') * 2)
    .duration(1500)
    .woodChips(5)
    .name('golden_helmet_recycling')
    .register()


mods.pyrotech.brick_sawmill.add('glowstone_to_dust', item('minecraft:glowstone'), item('pyrotech:sawmill_blade_stone'), item('minecraft:glowstone_dust'), 200, 0)
mods.pyrotech.brick_sawmill.add('bed_to_wool', item('minecraft:bed'), item('minecraft:wool') * 3, 500, 3)

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

mods.pyrotech.chopping_block.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .inherit(true)
    .name('iron_to_gold_chopping_block')
    .register()


// Compacting Bin:
// When using a shovel it can convert items.

mods.pyrotech.compacting_bin.removeByInput(item('minecraft:snowball'))
mods.pyrotech.compacting_bin.removeByOutput(item('minecraft:bone_block'))
// mods.pyrotech.compacting_bin.removeAll()

mods.pyrotech.compacting_bin.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .hits(5, 4, 3, 2)
    .inherit(true)
    .name('diamond_to_emerald')
    .register()

mods.pyrotech.compacting_bin.recipeBuilder()
    .input(item('minecraft:slime_ball') * 9)
    .output(item('minecraft:slime'))
    .name('slime_compacting')
    .register()


mods.pyrotech.compacting_bin.add('iron_to_clay', ore('ingotIron') * 5, item('minecraft:clay_ball') * 20, false, 9, 7, 6, 6)

// Compost Bin:
// Can convert multiple items into a new one when its full.

mods.pyrotech.compost_bin.removeByInput(item('minecraft:golden_carrot'))
// mods.pyrotech.compost_bin.removeByOutput(item('pyrotech:mulch') * 4)
// mods.pyrotech.compost_bin.removeAll()

mods.pyrotech.compost_bin.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald') * 4)
    .compostValue(25)
    .name('diamond_to_emerald_compost_bin')
    .register()


mods.pyrotech.compost_bin.add('iron_to_clay2', ore('ingotIron'), item('minecraft:clay_ball') * 20, 2)

// Crude Drying Rack:
// Converts an item over time into a new one.

mods.pyrotech.crude_drying_rack.removeByInput(item('minecraft:wheat'))
mods.pyrotech.crude_drying_rack.removeByOutput(item('minecraft:paper'))
// mods.pyrotech.crude_drying_rack.removeAll()

mods.pyrotech.crude_drying_rack.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .dryTime(260)
    .name('diamond_to_emerald_crude_drying_rack')
    .register()

mods.pyrotech.crude_drying_rack.recipeBuilder()
    .input(item('minecraft:glowstone_dust'))
    .output(item('minecraft:redstone'))
    .dryTime(1000)
    .inherit(true)
    .name('glowstone_to_redstone')
    .register()


mods.pyrotech.crude_drying_rack.add('apple_to_dirt', item('minecraft:apple'), item('minecraft:dirt'), 1200, true)

// Drying Rack:
// Converts an item over time into a new one.

mods.pyrotech.drying_rack.removeByInput(item('minecraft:wheat'))
mods.pyrotech.drying_rack.removeByOutput(item('minecraft:sponge'))
// mods.pyrotech.drying_rack.removeAll()

mods.pyrotech.drying_rack.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .dryTime(260)
    .name('iron_to_gold_drying_rack')
    .register()

mods.pyrotech.drying_rack.recipeBuilder()
    .input(item('minecraft:ender_eye'))
    .output(item('minecraft:ender_pearl'))
    .dryTime(500)
    .inherit(true)
    .name('ender_eye_to_ender_pearl')
    .register()


mods.pyrotech.drying_rack.add('apple_to_dirt', item('minecraft:apple'), item('minecraft:dirt'), 1200, true)

// groovyscript.wiki.pyrotech.mechanical_compacting_bin.title:
// groovyscript.wiki.pyrotech.mechanical_compacting_bin.description.

mods.pyrotech.mechanical_compacting_bin.removeByInput(item('minecraft:snowball'))
mods.pyrotech.mechanical_compacting_bin.removeByOutput(item('minecraft:bone_block'))
// mods.pyrotech.mechanical_compacting_bin.removeAll()

mods.pyrotech.mechanical_compacting_bin.recipeBuilder()
    .hits(2, 2, 1, 1)
    .input(item('minecraft:melon') * 8)
    .output(item('minecraft:melon_block'))
    .name('melon_compacting')
    .register()


mods.pyrotech.mechanical_compacting_bin.add('wheat_to_hay_block', ore('cropWheat') * 9, item('minecraft:hay_block'))
mods.pyrotech.mechanical_compacting_bin.add('gold_to_wheat', ore('ingotGold') * 4, item('minecraft:wheat') * 64, 4, 4, 3, 2)

// Pit Kiln:
// Converts an item into a new one by burning it. Has a chance to fail.

mods.pyrotech.pit_kiln.removeByInput(item('pyrotech:bucket_refractory_unfired'))
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

mods.pyrotech.pit_kiln.recipeBuilder()
    .input(item('minecraft:record_11'))
    .output(item('minecraft:record_13'))
    .burnTime(200)
    .failureChance(0f)
    .inherit(true)
    .name('record_11_to_record_13')
    .register()


mods.pyrotech.pit_kiln.add('brick_to_iron', item('minecraft:brick'), item('minecraft:iron_ingot'), 1200, true, 0.5f, item('minecraft:dirt'), item('minecraft:cobblestone'))

// Soaking Pot:
// Converts an item into a new one by soaking it in a liquid. Can require a campfire.

mods.pyrotech.soaking_pot.removeByInput(item('pyrotech:hide_washed'))
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

mods.pyrotech.stone_kiln.removeByInput(item('minecraft:sand'))
mods.pyrotech.stone_kiln.removeByOutput(item('pyrotech:bucket_clay'))
// mods.pyrotech.stone_kiln.removeAll()

mods.pyrotech.stone_kiln.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .burnTime(800)
    .failureChance(0.6f)
    .failureOutput(item('minecraft:egg'), item('minecraft:fish'))
    .name('diamond_to_emerald_with_failure_outputs')
    .register()

mods.pyrotech.stone_kiln.recipeBuilder()
    .input(item('minecraft:compass'))
    .output(item('minecraft:clock'))
    .burnTime(1200)
    .failureChance(0f)
    .inherit(true)
    .name('compass_to_clock')
    .register()


mods.pyrotech.stone_kiln.add('clay_to_iron_stone', item('minecraft:clay_ball'), item('minecraft:iron_ingot'), 1200, true, 0.5f, item('minecraft:dirt'), item('minecraft:cobblestone'))

// Stone Oven:
// When powered by burning fuel can convert items.

mods.pyrotech.stone_oven.removeByInput(item('minecraft:porkchop'))
mods.pyrotech.stone_oven.removeByOutput(item('minecraft:cooked_porkchop'))
// mods.pyrotech.stone_oven.removeAll()

mods.pyrotech.stone_oven.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .duration(400)
    .inherit(true)
    .name('diamond_campfire_to_emerald_stone')
    .register()


mods.pyrotech.stone_oven.add('sand_to_dirt', item('minecraft:sand'), item('minecraft:dirt'), 1000, true)

// groovyscript.wiki.pyrotech.stone_sawmill.title:
// groovyscript.wiki.pyrotech.stone_sawmill.description.

mods.pyrotech.stone_sawmill.removeByInput(item('minecraft:planks:1'))
mods.pyrotech.stone_sawmill.removeByOutput(item('pyrotech:material:23'))
// mods.pyrotech.stone_sawmill.removeAll()

mods.pyrotech.stone_sawmill.recipeBuilder()
    .input(item('minecraft:sign'))
    .output(item('minecraft:planks:0') * 2)
    .duration(200)
    .woodChips(5)
    .inherit(true)
    .name('wood_from_sign')
    .register()

mods.pyrotech.stone_sawmill.recipeBuilder()
    .input(item('minecraft:stone_pickaxe'), ore('blockIron'))
    .output(item('minecraft:iron_pickaxe'))
    .duration(5000)
    .name('stone_pickaxe_upgrade')
    .register()


mods.pyrotech.stone_sawmill.add('apple_to_gapple_with_golden_blade', item('minecraft:apple'), item('pyrotech:sawmill_blade_bone'), item('minecraft:golden_apple'), 2000, 0, false)
mods.pyrotech.stone_sawmill.add('stone_to_cobblestone', ore('stone'), item('minecraft:cobblestone'), 500, 0, true)

// Tanning Rack:
// Converts an item over time into a new one.

mods.pyrotech.tanning_rack.removeByInput(item('minecraft:wheat'))
mods.pyrotech.tanning_rack.removeByOutput(item('minecraft:leather'))
// mods.pyrotech.tanning_rack.removeAll()

mods.pyrotech.tanning_rack.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .dryTime(260)
    .name('iron_to_gold_drying_rack')
    .register()


mods.pyrotech.tanning_rack.add('apple_to_dirt', item('minecraft:apple'), item('minecraft:dirt'), 1200, item('minecraft:clay_ball'))
