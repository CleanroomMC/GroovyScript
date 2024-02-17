// MODS_LOADED: pyrotech

import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.AnvilRecipe;

println 'mod \'pyrotech\' detected, running script'

mods.pyrotech.barrel.recipeBuilder()
    .input(
            item('minecraft:diamond'),
            item('minecraft:diamond'),
            item('minecraft:diamond'),
            item('minecraft:emerald')
    )
    .fluidInput(fluid('water') * 1000)
    .fluidOutput(fluid('amongium') * 1000)
    .duration(1000)
    .name('diamond_emerald_and_water_to_amongium') // required
    .register()

// mods.pyrotech.barrel.removeByOutput(fluid('freckleberry_wine') * 1000)

mods.pyrotech.campfire.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .duration(400)
    .name('diamond_campfire_to_emerald') // required
    .register()

mods.pyrotech.choppingblock.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .chops(25)
    .quantity(2)
    .name('diamond_to_emerald_chopping_block') // required
    .register()

mods.pyrotech.compactingbin.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .toolUses(5)
    .name('diamond_to_emerald_compacting_bin') // required
    .register()


mods.pyrotech.compostbin.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald') * 4)
    .compostValue(25)
    .name('diamond_to_emerald_compost_bin') // required
    .register()

mods.pyrotech.compostbin.removeByInput(item('minecraft:golden_carrot'))

mods.pyrotech.crudedryingrack.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .dryTime(260)
    .name('diamond_to_emerald_crude_drying_rack') // required
    .register()

mods.pyrotech.crudedryingrack.removeByInput(item('minecraft:wheat'))

mods.pyrotech.dryingrack.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .dryTime(260)
    .name('iron_to_gold_drying_rack') // required
    .register()

mods.pyrotech.dryingrack.removeByInput(item('pyrotech:material', 12))

mods.pyrotech.kiln.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:gold_ingot'))
    .burnTime(400)
    .failureChance(1f)
    .failureOutputs(
            item('minecraft:wheat'),
            item('minecraft:carrot'),
            item('minecraft:sponge')
    )
    .name('iron_to_gold_kiln_with_failure_items') // required
    .register()

mods.pyrotech.kiln.removeByOutput(item('pyrotech:bucket_clay'))

mods.pyrotech.anvil.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:emerald'))
    .hits(5)
    .type(AnvilRecipe.EnumType.HAMMER) // can replace with PICKAXE
    .tier(AnvilRecipe.EnumTier.GRANITE) // can replace with IRONCLAD or OBSIDIAN
    .name('diamond_to_emerald_anvil')
    .register()

mods.pyrotech.anvil.removeByOutput(item('minecraft:stone_slab', 3) * 2)

mods.pyrotech.soakingpot.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidInput(fluid('amongium') * 125)
    .output(item('minecraft:emerald'))
    .time(400)
    .campfireRequired(true)
    .name('diamond_to_emerald_with_amongium_soaking_pot') // required
    .register()

mods.pyrotech.soakingpot.removeByOutput(item('pyrotech:material', 54))



