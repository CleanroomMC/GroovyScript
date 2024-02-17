
// Auto generated groovyscript example file
// MODS_LOADED: pyrotech

println 'mod \'pyrotech\' detected, running script'

// groovyscript.wiki.pyrotech.anvil.title:
// groovyscript.wiki.pyrotech.anvil.description

mods.pyrotech.anvil.removeByOutput(item('minecraft:stone_slab', 3))

mods.pyrotech.anvil.recipeBuilder()
    .input(item('minecraft:diamond') * 4)
    .output(item('minecraft:emerald') * 2)
    .hits(5)
    .type(AnvilRecipe.EnumType.HAMMER)
    .tier(AnvilRecipe.EnumTier.GRANITE)
    .name('diamond_to_emerald_granite_anvil')
    .register()

mods.pyrotech.anvil.recipeBuilder()
    .input(item('minecraft:diamond') * 8)
    .output(item('minecraft:nether_star') * 1)
    .hits(10)
    .type(AnvilRecipe.EnumType.PICKAXE)
    .tier(AnvilRecipe.EnumTier.IRONCLAD)
    .name('diamond_to_nether_star_ironclad_anvil')
    .register()

mods.pyrotech.anvil.recipeBuilder()
    .input(item('minecraft:diamond') * 4)
    .output(item('minecraft:gold_ingot') * 16)
    .hits(5)
    .type(AnvilRecipe.EnumType.PICKAXE)
    .tier(AnvilRecipe.EnumTier.OBSIDIAN)
    .name('diamond_to_gold_obsidian_anvil')
    .register()


// groovyscript.wiki.pyrotech.barrel.title:
// groovyscript.wiki.pyrotech.barrel.description

mods.pyrotech.barrel.removeByOutput(fluid('freckleberry_wine') * 1000)

mods.pyrotech.barrel.recipeBuilder()
    .input(item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:emerald'))
    .fluidInput(fluid('water') * 1000)
    .fluidOutput(fluid('amongium') * 1000)
    .duration(1000)
    .name('diamond_emerald_and_water_to_amongium')
    .register()
    .register()


// groovyscript.wiki.pyrotech.campfire.title:
// groovyscript.wiki.pyrotech.campfire.description

mods.pyrotech.campfire.removeByInput(item('minecraft:porkchop'))
mods.pyrotech.campfire.removeByOutput(item('minecraft:cooked_porkchop'))

mods.pyrotech.campfire.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .duration(400)
    .name('diamond_campfire_to_emerald')
    .register()


// groovyscript.wiki.pyrotech.chopping_block.title:
// groovyscript.wiki.pyrotech.chopping_block.description

mods.pyrotech.chopping_block.removeByInput(item('minecraft:log2'))
mods.pyrotech.chopping_block.removeByOutput(item('minecraft:planks', 4))

mods.pyrotech.chopping_block.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .chops(25)
    .quantity(2)
    .name('diamond_to_emerald_chopping_block')
    .register()


// groovyscript.wiki.pyrotech.compacting_bin.title:
// groovyscript.wiki.pyrotech.compacting_bin.description

mods.pyrotech.compacting_bin.removeByInput(item('minecraft:snowball'))
mods.pyrotech.compacting_bin.removeByOutput(item('minecraft:bone_block'))

mods.pyrotech.compacting_bin.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .toolUses(5)
    .name('diamond_to_emerald_compacting_bin')
    .register()


// groovyscript.wiki.pyrotech.compost_bin.title:
// groovyscript.wiki.pyrotech.compost_bin.description

mods.pyrotech.compost_bin.removeByInput(item('minecraft:golden_carrot'))

mods.pyrotech.compost_bin.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald') * 4)
    .compostValue(25)
    .name('diamond_to_emerald_compost_bin')
    .register()


// groovyscript.wiki.pyrotech.crude_drying_rack.title:
// groovyscript.wiki.pyrotech.crude_drying_rack.description

mods.pyrotech.crude_drying_rack.removeByInput(item('minecraft:wheat'))

mods.pyrotech.crude_drying_rack.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .dryTime(260)
    .name('diamond_to_emerald_crude_drying_rack')
    .register()


// groovyscript.wiki.pyrotech.drying_rack.title:
// groovyscript.wiki.pyrotech.drying_rack.description

mods.pyrotech.drying_rack.removeByInput(item('minecraft:wheat'))

mods.pyrotech.drying_rack.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .dryTime(260)
    .name('iron_to_gold_drying_rack')
    .register()


// groovyscript.wiki.pyrotech.kiln.title:
// groovyscript.wiki.pyrotech.kiln.description

mods.pyrotech.kiln.removeByOutput(item('pyrotech:bucket_clay'))

mods.pyrotech.kiln.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .burnTime(400)
    .failureChance(1f)
    .failureOutputs(item('minecraft:wheat'),item('minecraft:carrot'),item('minecraft:sponge'))
    .name('iron_to_gold_kiln_with_failure_items')
    .register()


// groovyscript.wiki.pyrotech.soaking_pot.title:
// groovyscript.wiki.pyrotech.soaking_pot.description

mods.pyrotech.soaking_pot.removeByOutput(item('pyrotech:material', 54))

mods.pyrotech.soaking_pot.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidInput(fluid('amongium') * 125)
    .output(item('minecraft:emerald'))
    .time(400)
    .campfireRequired(true)
    .name('diamond_to_emerald_with_amongium_soaking_pot')


// groovyscript.wiki.pyrotech.tanning_rack.title:
// groovyscript.wiki.pyrotech.tanning_rack.description

mods.pyrotech.tanning_rack.removeByInput(item('minecraft:wheat'))

mods.pyrotech.tanning_rack.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .dryTime(260)
    .name('iron_to_gold_drying_rack')
    .register()


