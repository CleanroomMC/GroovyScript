
// Auto generated groovyscript example file
// MODS_LOADED: astralsorcery

import hellfirepvp.astralsorcery.common.constellation.MoonPhase
import net.minecraft.util.math.MathHelper

println 'mod \'astralsorcery\' detected, running script'

// Chalice Interaction:
// When two chalices containing different fluids are placed nearby, fluid may be consumed to produce an output itemstack.

// mods.astralsorcery.chalice_interaction.removeByInput(fluid('astralsorcery.liquidstarlight'))
mods.astralsorcery.chalice_interaction.removeByInput(fluid('water'), fluid('lava'))
mods.astralsorcery.chalice_interaction.removeByOutput(item('minecraft:ice'))
// mods.astralsorcery.chalice_interaction.removeAll()

mods.astralsorcery.chalice_interaction.recipeBuilder()
    .output(item('astralsorcery:blockmarble'))
    .fluidInput(fluid('water') * 10)
    .fluidInput(fluid('astralsorcery.liquidstarlight') * 30)
    .register()



// Constellation:
// Create a custom Constellation.

mods.astralsorcery.constellation.remove(constellation('bootes'))
mods.astralsorcery.constellation.removeConstellationMapEffect(constellation('discidia'))
mods.astralsorcery.constellation.removeSignatureItems(constellation('discidia'))
// mods.astralsorcery.constellation.removeAll()
// mods.astralsorcery.constellation.removeAllConstellationMapEffect()
// mods.astralsorcery.constellation.removeAllSignatureItems()

mods.astralsorcery.constellation.constellationBuilder()
    .major()
    .name('square')
    .color(0xE01903)
    .connection(12, 2, 2, 2)
    .connection(12, 12, 12, 2)
    .connection(2, 12, 12, 12)
    .connection(2, 2, 2, 12)
    .register()

mods.astralsorcery.constellation.constellationBuilder()
    .minor()
    .name('slow')
    .connection(10, 5, 5, 5)
    .connection(5, 10, 5, 5)
    .connection(3, 3, 3, 3)
    .phase(MoonPhase.FULL)
    .register()

mods.astralsorcery.constellation.constellationMapEffectBuilder()
    .constellation(constellation('square'))
    .enchantmentEffect(enchantment('minecraft:luck_of_the_sea'), 1, 3)
    .potionEffect(potion('minecraft:luck'), 1, 2)
    .register()

mods.astralsorcery.constellation.signatureItems()
    .constellation(constellation('square'))
    .addItem(ore('gemDiamond'))
    .addItem(item('minecraft:water_bucket'))
    .addItem(item('minecraft:rabbit_foot'))
    .addItem(item('minecraft:fish'))
    .register()



// Fountain:
// Adds virtual aquifers that can be accessed via the Evershifting Fountain's Necromantic Prime.

mods.astralsorcery.fountain.remove(fluid('lava'))
// mods.astralsorcery.fountain.removeAll()

mods.astralsorcery.fountain.chanceHelper()
    .fluid(fluid('astralsorcery.liquidstarlight'))
    .rarity(10000000)
    .minimumAmount(4000000)
    .variance(1000000)
    .register()



// Grindstone:
// Converts an item into an itemstack with a chance of getting twice the amount after right clicking the grindstone based
// on weight.

mods.astralsorcery.grindstone.removeByInput(item('minecraft:redstone_ore'))
mods.astralsorcery.grindstone.removeByOutput(ore('dustIron'))
// mods.astralsorcery.grindstone.removeAll()

mods.astralsorcery.grindstone.recipeBuilder()
    .input(ore('blockDiamond'))
    .output(item('minecraft:clay'))
    .weight(1)
    .secondaryChance(1.0F)
    .register()

mods.astralsorcery.grindstone.recipeBuilder()
    .input(item('minecraft:stone'))
    .output(item('minecraft:cobblestone'))
    .weight(5)
    .register()



// Infusion Altar:
// Consumes buckets of Liquid Starlight when interacted with by a Resonating Wand to convert input items into output
// itemstacks after a time.

mods.astralsorcery.infusion_altar.removeByInput(item('minecraft:diamond_ore'))
mods.astralsorcery.infusion_altar.removeByOutput(item('minecraft:iron_ingot'))
// mods.astralsorcery.infusion_altar.removeAll()

mods.astralsorcery.infusion_altar.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .consumption(1f)
    .chalice(false)
    .consumeMultiple(true)
    .time(10)
    .register()

mods.astralsorcery.infusion_altar.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()


// Light Transmutation:
// Converts an input Block or IBlockState into an output IBlockState after being sent a given amount of starlight, with the
// ability to require a specific constellation of starlight.

mods.astralsorcery.light_transmutation.removeByInput(block('minecraft:netherrack'))
mods.astralsorcery.light_transmutation.removeByInput(blockstate('minecraft:sandstone'))
mods.astralsorcery.light_transmutation.removeByOutput(block('minecraft:lapis_block'))
mods.astralsorcery.light_transmutation.removeByOutput(blockstate('minecraft:cake'))
// mods.astralsorcery.light_transmutation.removeAll()

mods.astralsorcery.light_transmutation.recipeBuilder()
    .input(block('minecraft:stone'))
    .output(block('astralsorcery:blockmarble'))
    .cost(100.0)
    .constellation(constellation('armara'))
    .inputDisplayStack(item('minecraft:stone'))
    .outputDisplayStack(item('minecraft:dye:15').withNbt([display:[Name:'Marble']]))
    .register()

mods.astralsorcery.light_transmutation.recipeBuilder()
    .input(blockstate('minecraft:pumpkin'))
    .output(blockstate('minecraft:diamond_block'))
    .cost(0)
    .register()


// Lightwell:
// Converts an input item into fluid, with a chance at breaking every time fluid is produced. The amount of fluid produced
// per interval can be increased via starlight.

mods.astralsorcery.lightwell.removeByCatalyst(item('minecraft:ice'))
mods.astralsorcery.lightwell.removeByInput(item('minecraft:packed_ice'))
mods.astralsorcery.lightwell.removeByOutput(fluid('lava'))
// mods.astralsorcery.lightwell.removeAll()

mods.astralsorcery.lightwell.recipeBuilder()
    .catalyst(item('minecraft:stone'))
    .output(fluid('astralsorcery.liquidstarlight'))
    .productionMultiplier(1.0F)
    .shatterMultiplier(15.0F)
    .catalystColor(16725260)
    .register()

mods.astralsorcery.lightwell.recipeBuilder()
    .catalyst(item('minecraft:obsidian'))
    .output(fluid('astralsorcery.liquidstarlight'))
    .productionMultiplier(1.0F)
    .shatterMultiplier(15.0F)
    .register()



// Perk Tree:
// Create a custom perk with a custom effect, at a given location.

mods.astralsorcery.perk_tree.remove('astralsorcery:mec_inc_ms_2')

mods.astralsorcery.perk_tree.movePerk(mods.astralsorcery.perktree.getPerk('astralsorcery:magnet_ats_reach'), 30, 30)

// Perk Tree Config:
// Control the Perk level cap and XP formula.

mods.astralsorcery.perk_tree_config.setLevelCap(50)
mods.astralsorcery.perk_tree_config.setXpFunction({ int i, long prev -> prev + 1000L + MathHelper.lfloor(Math.pow(2.0, i / 2.0F + 3)) })

// Research Pages:
// Add custom Research Pages to the Astral Sorcery Book.

mods.astralsorcery.research.disconnectNodes('MY_TEST_RESEARCH', 'ALTAR1')
mods.astralsorcery.research.removeNode('CPAPER')

mods.astralsorcery.research.researchBuilder()
    .name('MY_TEST_RESEARCH')
    .point(5,5)
    .icon(item('minecraft:pumpkin'))
    .discovery()
    .page(mods.astralsorcery.research.pageBuilder().textPage('GROOVYSCRIPT.RESEARCH.PAGE.TEST'))
    .page(mods.astralsorcery.research.pageBuilder().emptyPage())
    .connectionFrom('ALTAR1')
    .register()

mods.astralsorcery.research.researchBuilder()
    .name('MY_TEST_RESEARCH2')
    .point(5,5)
    .icon(item('minecraft:pumpkin'))
    .constellation()
    .page(mods.astralsorcery.research.pageBuilder().textPage('GROOVYSCRIPT.RESEARCH.PAGE.TEST2'))
    .page(mods.astralsorcery.research.pageBuilder().constellationRecipePage(item('minecraft:pumpkin')))
    .register()


mods.astralsorcery.research.connectNodes('MY_TEST_RESEARCH2', 'ENHANCED_COLLECTOR')
mods.astralsorcery.research.moveNode('SOOTYMARBLE', 5, 6)

// Starlight Altar:
// Allows creation of shaped recipes in the Astral Sorcery Crafting Altar chain.

// mods.astralsorcery.starlight_altar.removeAll()

mods.astralsorcery.starlight_altar.discoveryRecipeBuilder()
    .output(item('minecraft:water_bucket'))
    .row('   ')
    .row(' B ')
    .row('   ')
    .key('B', item('minecraft:bucket'))
    .starlight(500)
    .craftTime(10)
    .register()


mods.astralsorcery.starlight_altar.constellationRecipeBuilder()
    .output(item('minecraft:pumpkin'))
    .matrix('ss ss',
            's   s',
            '  d  ',
            's   s',
            'ss ss')
    .key('s', item('minecraft:pumpkin_seeds'))
    .key('d', ore('dirt'))
    .register()

mods.astralsorcery.starlight_altar.traitRecipeBuilder()
    .output(item('astralsorcery:itemrockcrystalsimple').setSize(300).setPurity(50).setCutting(50))
    .matrix('sssss',
            'sgggs',
            'sgdgs',
            'sgggs',
            'sssss')
    .key('s', item('minecraft:pumpkin'))
    .key('g', ore('treeLeaves'))
    .key('d', item('minecraft:diamond_block'))
    .outerInput(item('astralsorcery:blockmarble'))
    .outerInput(ore('ingotAstralStarmetal'))
    .outerInput(fluid('astralsorcery.liquidstarlight') * 1000)
    .outerInput(ore('treeSapling'))
    .constellation(constellation('discidia'))
    .register()


// Aevitas Perk Registry:
// Having the Stone Enrichment perk will convert nearby stone blocks into random ores.

mods.astralsorcery.aevitas_perk_registry.remove(ore('oreDiamond'))
// mods.astralsorcery.aevitas_perk_registry.removeAll()

mods.astralsorcery.aevitas_perk_registry.add(ore('blockDiamond'), 10000)

// Mineralis Ritual Registry:
// Using a mineralis ritual will convert nearby stone blocks into random ores.

mods.astralsorcery.mineralis_ritual_registry.remove(ore('oreDiamond'))
// mods.astralsorcery.mineralis_ritual_registry.removeAll()

mods.astralsorcery.mineralis_ritual_registry.add(ore('blockDiamond'), 10000)

// Trash Perk Registry:
// Having the Trash to Treasure perk turns items the player drops in the list defined in the config at
// 'perks/key_void_trash/DropList' into a chance at random ores.

mods.astralsorcery.trash_perk_registry.remove(ore('oreDiamond'))
// mods.astralsorcery.trash_perk_registry.removeAll()

mods.astralsorcery.trash_perk_registry.add(ore('blockDiamond'), 10000)

// Treasure Shrine Registry:
// When the block in the middle of a Treasure Shrine structure is broken, a random ore from this list will replace it until
// the Treasure Shrine is exhausted.

mods.astralsorcery.treasure_shrine_registry.remove(ore('oreDiamond'))
// mods.astralsorcery.treasure_shrine_registry.removeAll()

mods.astralsorcery.treasure_shrine_registry.add(ore('blockDiamond'), 10000)

