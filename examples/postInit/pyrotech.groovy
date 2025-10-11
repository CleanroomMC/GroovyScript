
// Auto generated groovyscript example file
// MODS_LOADED: pyrotech

log.info 'mod \'pyrotech\' detected, running script'

// Anvil:
// Converts an item to a new item by hitting it with a hammer or pickaxe.

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

// Bloomery:
// Converts item to a new item or a 'bloom' which are items that needs to be hit with hammer on anvil to get the output
// with varying amounts.

mods.pyrotech.bloomery.removeByInput(item('minecraft:gold_ore'))
// mods.pyrotech.bloomery.removeByOutput(item('minecraft:iron_nugget'))
// mods.pyrotech.bloomery.removeAll()

mods.pyrotech.bloomery.recipeBuilder()
    .input(item('minecraft:iron_block'))
    .bloom(item('minecraft:apple'))
    .failureChance(0.0F)
    .slag(item('minecraft:carrot'))
    .inherit(true)
    .name('metal_vegetation')
    .register()

mods.pyrotech.bloomery.recipeBuilder()
    .input(item('minecraft:noteblock'))
    .output(item('minecraft:record_13'))
    .experience(0.25F)
    .tierIronclad()
    .bloomYield(1, 1)
    .burnTime(2000)
    .failureChance(0.5F)
    .failureOutput(item('minecraft:record_11'), 1)
    .inherit(true)
    .name('recipe_for_soundphiles')
    .register()

mods.pyrotech.bloomery.recipeBuilder()
    .input(item('minecraft:sponge'))
    .output(item('minecraft:sponge'))
    .bloomYield(2, 5)
    .typePickaxe()
    .langKey(item('minecraft:stick').getTranslationKey())
    .inherit(true)
    .name('sponge_duplication')
    .register()

mods.pyrotech.bloomery.recipeBuilder()
    .input(item('minecraft:birch_boat'))
    .bloom(item('minecraft:dark_oak_boat'))
    .tierObsidian()
    .failureChance(0.1)
    .failureOutput(item('minecraft:spruce_boat'), 5)
    .failureOutput(item('minecraft:jungle_boat'), 2)
    .failureOutput(item('minecraft:boat'), 1)
    .name('boat_smelting')
    .register()

mods.pyrotech.bloomery.recipeBuilder()
    .input(item('minecraft:sand'))
    .output(item('minecraft:glass'))
    .bloomYield(3, 5)
    .experience(0.1F)
    .burnTime(4000)
    .tierGranite()
    .tierObsidian()
    .anvilHit(2)
    .typePickaxe()
    .failureChance(0.05)
    .failureOutput(item('minecraft:nether_star'), 1)
    .failureOutput(item('minecraft:gold_ingot'), 10)
    .name('glasswork')
    .register()


mods.pyrotech.bloomery.add('loreming_the_ipsum', item('minecraft:redstone'), item('minecraft:lava_bucket'), false)
mods.pyrotech.bloomery.add('cooking_a_story', item('minecraft:written_book'), item('minecraft:book'), 200, true)
mods.pyrotech.bloomery.addBloom('cyanide', item('minecraft:poisonous_potato'), item('minecraft:potato'), true)

// Refractory Crucible:
// Converts item into a liquid.

mods.pyrotech.brick_crucible.removeByInput(item('minecraft:gravel'))
mods.pyrotech.brick_crucible.removeByOutput(fluid('water') * 125)
// mods.pyrotech.brick_crucible.removeAll()

mods.pyrotech.brick_crucible.recipeBuilder()
    .input(item('minecraft:vine'))
    .fluidOutput(fluid('water') * 250)
    .burnTime(60)
    .name('water_from_vine')
    .register()


mods.pyrotech.brick_crucible.add('lava_from_obsidian', ore('obsidian'), fluid('lava') * 1000, 2000)

// Refractory Kiln:
// Converts an item into a new item or one of the failure outputs if the recipe fails.

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
// Converts item into a new item.

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

// Refractory Sawmill:
// Converts an item into a new item with an item with durability and drops wood chips on given amount of time.

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
// Converts item into a new item on given amount of time.

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

// Mechanical Compacting Bin:
// Converts given amount of item into new item.

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

// Pit Burning:
// Converts a block in world to an item and fluid by burning.

mods.pyrotech.pit_burn.removeByInput(item('minecraft:coal_block'))
mods.pyrotech.pit_burn.removeByOutput(item('minecraft:coal', 1) * 10)
// mods.pyrotech.pit_burn.removeAll()

mods.pyrotech.pit_burn.recipeBuilder()
    .input(item('minecraft:cauldron'))
    .output(item('minecraft:cobblestone'))
    .fluidOutput(fluid('water') * 50)
    .burnStages(6)
    .burnTime(1200)
    .name('water_from_cauldron')
    .register()

mods.pyrotech.pit_burn.recipeBuilder()
    .input(item('minecraft:soul_sand'))
    .output(item('minecraft:sand'))
    .fluidOutput(fluid('lava') * 200)
    .requiresRefractoryBlocks(true)
    .burnStages(2)
    .burnTime(600)
    .failureChance(0.25F)
    .failureOutput(item('minecraft:gravel') * 2)
    .failureOutput(item('minecraft:dirt') * 3)
    .name('lava_to_sand')
    .register()

mods.pyrotech.pit_burn.recipeBuilder()
    .input(blockstate('minecraft:sponge',
                      'wet=true'))
    .output(item('minecraft:sponge'))
    .fluidOutput(fluid('water') * 25)
    .fluidLevelAffectsFailureChance(true)
    .burnStages(10)
    .burnTime(500)
    .name('sponge_dehydrating')
    .register()

mods.pyrotech.pit_burn.recipeBuilder()
    .input(item('minecraft:chest'))
    .output(item('minecraft:ender_chest'))
    .fluidOutput(fluid('lava') * 125)
    .fluidLevelAffectsFailureChance(true)
    .requiresRefractoryBlocks(true)
    .burnStages(4)
    .burnTime(2000)
    .name('chest_burning')
    .register()


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
// Converts an item and liquid into a new item. Can require a campfire below.

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

// Stone Crucible:
// Converts an item into a liquid.

mods.pyrotech.stone_crucible.removeByInput(item('minecraft:ice'))
mods.pyrotech.stone_crucible.removeByOutput(fluid('water') * 500)
// mods.pyrotech.stone_crucible.removeAll()

mods.pyrotech.stone_crucible.recipeBuilder()
    .input(ore('sugarcane'))
    .fluidOutput(fluid('water') * 500)
    .burnTime(1000)
    .inherit(true)
    .name('water_from_sugarcane')



mods.pyrotech.stone_crucible.add('water_from_cactus', ore('blockCactus'), fluid('water') * 1000, 600, true)

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

// Stone Sawmill:
// Converts an item into a new item with an item with durability and drops wood chips on given amount of time.

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
    .input(item('minecraft:stone_pickaxe'), item('pyrotech:sawmill_blade_bone'))
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

// Wither Forge:
// Converts item to a new item or a 'bloom' which are items that needs to be hit with hammer on anvil to get the output
// with varying amounts.

mods.pyrotech.wither_forge.removeByInput(item('minecraft:gold_ore'))
// mods.pyrotech.wither_forge.removeByOutput(item('minecraft:iron_nugget'))
// mods.pyrotech.wither_forge.removeAll()

mods.pyrotech.wither_forge.recipeBuilder()
    .input(item('minecraft:minecart'))
    .output(item('minecraft:furnace_minecart'))
    .slag(item('minecraft:iron_ingot'))
    .experience(0.8F)
    .bloomYield(1, 1)
    .burnTime(2000)
    .failureChance(0.5F)
    .failureOutput(item('minecraft:tnt_minecart'), 1)
    .name('minecart_smelting')
    .register()

mods.pyrotech.wither_forge.recipeBuilder()
    .input(item('minecraft:fishing_rod') | item('minecraft:carrot_on_a_stick'))
    .output(item('minecraft:cooked_fish'))
    .bloomYield(5, 8)
    .langKey(item('minecraft:fishing_rod').getTranslationKey())
    .name('fishing')
    .register()

mods.pyrotech.wither_forge.recipeBuilder()
    .input(item('minecraft:paper'))
    .bloom(item('minecraft:book'))
    .tierGranite()
    .tierObsidian()
    .failureChance(0.1F)
    .failureOutput(item('minecraft:milk_bucket'), 5)
    .failureOutput(item('minecraft:bone'), 2)
    .name('knowledge')
    .register()

mods.pyrotech.wither_forge.recipeBuilder()
    .input(item('minecraft:comparator'))
    .output(item('minecraft:redstone'))
    .bloomYield(12, 15)
    .experience(0.6F)
    .burnTime(4000)
    .tierGranite()
    .tierIronclad()
    .anvilHit(5)
    .typePickaxe()
    .failureChance(0.15F)
    .failureOutput(item('minecraft:glowstone_dust'), 5)
    .failureOutput(item('minecraft:sugar'), 4)
    .name('comparator_melting')
    .register()


mods.pyrotech.wither_forge.add('flower_pot', item('minecraft:flower_pot'), item('minecraft:clay_ball'))
mods.pyrotech.wither_forge.add('hoopify', item('minecraft:hopper') * 4, item('minecraft:chest'), 60)
mods.pyrotech.wither_forge.addBloom('quartz_recipe', item('minecraft:quartz') * 3, ore('oreQuartz'))
mods.pyrotech.wither_forge.addBloom('feathery', item('minecraft:feather'), 10, 15, item('minecraft:chicken'), 200, 0.1F, 0.25F, item('minecraft:cooked_chicken'))

// Worktable:
// Crafting table which asks you to hit it with a tool to craft stuff.

mods.pyrotech.worktable.removeByOutput(item('pyrotech:iron_hunters_knife'))
// mods.pyrotech.worktable.removeAll()

mods.pyrotech.worktable.shapedBuilder()
    .name('irons_to_dirts')
    .output(item('minecraft:dirt') * 8)
    .shape([[item('minecraft:iron_ingot'),item('minecraft:iron_ingot'),item('minecraft:iron_ingot')],
           [item('minecraft:iron_ingot'),null,item('minecraft:iron_ingot')],
           [item('minecraft:iron_ingot'),item('minecraft:iron_ingot'),item('minecraft:iron_ingot')]])
    .replaceByName()
    .register()

mods.pyrotech.worktable.shapedBuilder()
    .name(resource('minecraft:sea_lantern'))
    .output(item('minecraft:clay'))
    .shape([[ore('blockRedstone')],
           [ore('blockRedstone')],
           [ore('blockRedstone')]])
    .replaceByName()
    .register()

mods.pyrotech.worktable.shapedBuilder()
    .output(item('minecraft:nether_star'))
    .row('TXT')
    .row('X X')
    .row('!X!')
    .key('T', item('minecraft:gravel'))
    .key('X', item('minecraft:clay').reuse())
    .key('!', item('minecraft:gunpowder').transform({ _ -> item('minecraft:diamond') }))
    .tool(item('minecraft:diamond_sword'), 5)
    .register()

mods.pyrotech.worktable.shapedBuilder()
    .output(item('minecraft:clay_ball') * 3)
    .shape('S S',
           ' G ',
           'SWS')
    .key([S: ore('ingotIron').reuse(), G: ore('gemDiamond'), W: fluid('water') * 1000])
    .tool(item('minecraft:diamond_axe'), 3)
    .register()

mods.pyrotech.worktable.shapedBuilder()
    .name('gold_duplication_with_tnt')
    .output(item('minecraft:gold_block'))
    .row('!!!')
    .row('!S!')
    .row('!!!')
    .key([S: ore('blockGold').reuse(), '!': item('minecraft:tnt').transform(item('minecraft:diamond'))])
    .tool(item('minecraft:iron_shovel'), 2)
    .register()

mods.pyrotech.worktable.shapedBuilder()
    .output(item('minecraft:clay'))
    .row(' B')
    .key('B', item('minecraft:glass_bottle'))
    .tool(item('minecraft:stone_sword'), 3)
    .register()

mods.pyrotech.worktable.shapedBuilder()
    .output(item('minecraft:clay'))
    .row(' 1 ')
    .row(' 0 ')
    .row(' 1 ')
    .key('1', item('minecraft:iron_sword'))
    .key('0', item('minecraft:diamond_sword').withNbt([display:[Name:'Sword with Specific NBT data']]))
    .tool(item('minecraft:iron_axe'), 4)
    .register()

mods.pyrotech.worktable.shapelessBuilder()
    .output(item('minecraft:string'))
    .input([item('minecraft:cobblestone'),item('minecraft:feather'),item('minecraft:gold_ingot')])
    .register()

mods.pyrotech.worktable.shapelessBuilder()
    .name('precious_to_clay')
    .output(item('minecraft:clay'))
    .input([item('minecraft:emerald'),item('minecraft:iron_ore'),item('minecraft:gold_ingot')])
    .register()

mods.pyrotech.worktable.shapelessBuilder()
    .name(resource('example:resource_location2'))
    .output(item('minecraft:stone'))
    .input([item('minecraft:gold_ore'), item('minecraft:gold_ingot')])
    .register()

mods.pyrotech.worktable.shapelessBuilder()
    .output(item('minecraft:ender_eye'))
    .input([item('minecraft:ender_pearl'),item('minecraft:bowl')])
    .replace()
    .tool(item('minecraft:iron_sword'), 4)
    .register()

mods.pyrotech.worktable.shapelessBuilder()
    .name('minecraft:pink_dye_from_pink_tulp')
    .output(item('minecraft:clay'))
    .input([item('minecraft:stick')])
    .replaceByName()
    .tool(item('minecraft:iron_pickaxe'), 2)
    .register()

mods.pyrotech.worktable.shapelessBuilder()
    .name(resource('minecraft:pink_dye_from_peony'))
    .output(item('minecraft:coal'))
    .input([item('minecraft:stone'), item('minecraft:iron_ingot')])
    .replaceByName()
    .tool(item('minecraft:stone_axe'), 2)
    .register()


// mods.pyrotech.worktable.addShaped(item('minecraft:gold_block'), item('minecraft:diamond_pickaxe'), 2, [[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[null, null, null],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
// mods.pyrotech.worktable.addShaped(item('minecraft:gold_block'), [[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[null, null, null],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
// mods.pyrotech.worktable.addShaped(resource('example:resource_location'), item('minecraft:clay'), item('minecraft:iron_shovel'), 2, [[item('minecraft:cobblestone')],[item('minecraft:nether_star')],[item('minecraft:cobblestone')]])
// mods.pyrotech.worktable.addShaped(resource('example:resource_location'), item('minecraft:clay'), [[item('minecraft:cobblestone')],[item('minecraft:nether_star')],[item('minecraft:cobblestone')]])
// mods.pyrotech.worktable.addShaped('gold_v_to_clay', item('minecraft:clay'), item('minecraft:iron_pickaxe'), 3, [[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[null,item('minecraft:gold_ingot'),null]])
// mods.pyrotech.worktable.addShaped('gold_v_to_clay', item('minecraft:clay'), [[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[null,item('minecraft:gold_ingot'),null]])
// mods.pyrotech.worktable.addShapeless(item('minecraft:clay'), item('minecraft:stone_shovel'), 3, [item('minecraft:cobblestone'),item('minecraft:nether_star'),item('minecraft:gold_ingot')])
// mods.pyrotech.worktable.addShapeless(item('minecraft:clay'), [item('minecraft:cobblestone'),item('minecraft:nether_star'),item('minecraft:gold_ingot')])
// mods.pyrotech.worktable.addShapeless(resource('example:resource_location2'), item('minecraft:clay'), item('minecraft:stone_shovel'), 3, [item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
// mods.pyrotech.worktable.addShapeless(resource('example:resource_location2'), item('minecraft:clay'), [item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
// mods.pyrotech.worktable.addShapeless('precious_to_clay', item('minecraft:clay'), item('minecraft:iron_shovel'), 2, [item('minecraft:diamond'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')])
// mods.pyrotech.worktable.addShapeless('precious_to_clay', item('minecraft:clay'), [item('minecraft:diamond'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')])
// mods.pyrotech.worktable.replaceShaped(item('minecraft:chest'), item('minecraft:iron_axe') | item('minecraft:stone_axe'), 3, [[ore('logWood'),ore('logWood'),ore('logWood')],[ore('logWood'),null,ore('logWood')],[ore('logWood'),ore('logWood'),ore('logWood')]])
// mods.pyrotech.worktable.replaceShaped(item('minecraft:chest'), [[ore('logWood'),ore('logWood'),ore('logWood')],[ore('logWood'),null,ore('logWood')],[ore('logWood'),ore('logWood'),ore('logWood')]])
// mods.pyrotech.worktable.replaceShaped(resource('minecraft:sea_lantern'), item('minecraft:diamond_pickaxe'), 3, item('minecraft:clay'), [[item('minecraft:glowstone')],[item('minecraft:glowstone')],[item('minecraft:glowstone')]])
// mods.pyrotech.worktable.replaceShaped(resource('minecraft:sea_lantern'), item('minecraft:clay'), [[item('minecraft:glowstone')],[item('minecraft:glowstone')],[item('minecraft:glowstone')]])
// mods.pyrotech.worktable.replaceShaped('gold_to_diamonds', item('minecraft:diamond') * 8, item('minecraft:diamond_pickaxe'), 4, [[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
// mods.pyrotech.worktable.replaceShaped('gold_to_diamonds', item('minecraft:diamond') * 8, [[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
// mods.pyrotech.worktable.replaceShapeless(item('minecraft:ender_eye'), item('minecraft:shears'), 3, [item('minecraft:ender_pearl'),item('minecraft:nether_star')])
// mods.pyrotech.worktable.replaceShapeless(item('minecraft:ender_eye'), [item('minecraft:ender_pearl'),item('minecraft:nether_star')])
// mods.pyrotech.worktable.replaceShapeless(resource('minecraft:pink_dye_from_peony'), item('minecraft:clay'), item('minecraft:stone_axe'), 2, [item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
// mods.pyrotech.worktable.replaceShapeless(resource('minecraft:pink_dye_from_peony'), item('minecraft:clay'), [item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
// mods.pyrotech.worktable.replaceShapeless('minecraft:pink_dye_from_pink_tulp', item('minecraft:iron_axe'), 2, item('minecraft:clay'), [item('minecraft:nether_star')])
// mods.pyrotech.worktable.replaceShapeless('minecraft:pink_dye_from_pink_tulp', item('minecraft:clay'), [item('minecraft:nether_star')])

