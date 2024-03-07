
// MODS_LOADED: astralsorcery
println 'mod \'astralsorcery\' detected, running script'

import net.minecraft.util.math.MathHelper

// Constellation bracket handler:
// Major (Bright) Constellations:
constellation('aevitas')
constellation('armara')
constellation('discidia')
constellation('evorsio')
constellation('vicio')
// Weak (Dim) Constellations:
constellation('bootes')
constellation('fornax')
constellation('horologium')
constellation('lucerna')
constellation('mineralis')
constellation('octans')
constellation('pelotrio')
// Minor (Faint) Constellations:
constellation('alcara')
constellation('gelu')
constellation('ulteria')
constellation('vorux')


// ItemStack Mixin:
// When interacting with a rock crystal item, you can use the following methods to specify data about it.
item('astralsorcery:itemrockcrystalsimple')
    .setSize(300) // Size is increased via sitting in liquid starlight and decreased by increasing cutting. From 0 - 400 for normal and 900 for Celestial.
    .setPurity(50) // Purity is increased when at max size in liquid starlight, consuming the starlight and creating a new crystal. From 0-100.
    .setCutting(50) // Cutting is increased by using the Grindstone with a rock crystal and decreased by increasing size. From 0-100.
    .setFracturation(0) // Fracturation is increased by being used on a Ritual Pedestal, breaking at 100, and decreased by increasing size. From 0-100.
    .setSizeOverride(-1) // Overrides the size value if anything other than -1
    .tuneTo(constellation('discidia')) // Major or Weak constellation, relevant for the Ritual Pedestal.
    .setTrait(constellation('vorux')) // Dim constellation, relevant for the Ritual Pedestal.


// Perk Tree Config:
// Control the Perk level cap (between 1 and 100) and the XP formula per level.
mods.astralsorcery.perktreeconfig.setLevelCap(50)
// i = level number, prev = prior level xp cost.
mods.astralsorcery.perktreeconfig.setXpFunction{ int i, long prev -> prev + 1000L + MathHelper.lfloor(Math.pow(2.0, i / 2.0F + 3)) }


// Constellation:
// Create a custom constellation.
mods.astralsorcery.constellation.constellationBuilder()
    .major() // Mutually exclusive, designates the constellation as major.
  //.weak() // Mutually exclusive, designates the constellation as weak.
  //.minor() // Mutually exclusive, designates the constellation as minor.
    .name('square')
    .color(0xE01903) // Optional int, color of the constellation. (Default based on major/weak/minor)
    .connection(12, 2, 2, 2) // Any number of connections, draws the constellation
    .connection(12, 12, 12, 2)
    .connection(2, 12, 12, 12)
    .connection(2, 2, 2, 12)
  //.phase(MoonPhase.FULL) // Only required for minor constellations, MoonPhase... or [MoonPhase]
    .register()

mods.astralsorcery.constellation.remove(constellation('bootes'))
//mods.astralsorcery.constellation.removeAll()


// Adjust the enchantment or potion effects related to a constellation.
mods.astralsorcery.constellation.constellationMapEffectBuilder()
    .constellation(constellation('square'))
    .enchantmentEffect(enchantment('luck_of_the_sea'), 1, 3)
    .potionEffect(potion('luck'), 1,2)
    .register()

mods.astralsorcery.constellation.removeConstellationMapEffect(constellation('discidia'))
//mods.astralsorcery.constellation.removeAllConstellationMapEffect()

// Set the signature items used for a constellation's mantle or paper.
mods.astralsorcery.constellation.signatureItems()
    .constellation(constellation('square'))
    .addItem(ore('gemDiamond'))
    .addItem(item('minecraft:water_bucket'))
    .addItem(item('minecraft:rabbit_foot'))
    .addItem(item('minecraft:fish'))
    .register()

mods.astralsorcery.constellation.removeSignatureItems(constellation('discidia'))
//mods.astralsorcery.constellation.removeAllSignatureItems()


// Perk Tree:
// Create a custom perk with a custom effect, at a given location
mods.astralsorcery.perktree.attributePerkBuilder()
    .name("this_is_fun")
    .point(28, 28)
    .connection("astralsorcery:magnet_ats_reach")
    .modifier(mods.astralsorcery.perktree.effectBuilder()
        .modifier(3)
        .mode(2)
        .type("ATTR_TYPE_REACH"))
    .register()

mods.astralsorcery.perktree.movePerk(mods.astralsorcery.perktree.getPerk("astralsorcery:magnet_ats_reach"), 30, 30)

mods.astralsorcery.perktree.remove("astralsorcery:magnet_ats_reach")


// Add custom research pages
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

mods.astralsorcery.research.removeNode('CPAPER')

mods.astralsorcery.research.disconnectNodes('MY_TEST_RESEARCH', 'ALTAR1')



// Starlight Altar:
// Allows creation of shaped recipes in the Astral Sorcery Crafting Altar chain.
mods.astralsorcery.starlightaltar.discoveryRecipeBuilder()
    .output(item('minecraft:water_bucket'))
    .row('   ')
    .row(' B ')
    .row('   ')
    .key('B', item('minecraft:bucket'))
    .starlight(1) // Optional int, amount of starlight the recipe requires. Has a cap per tier of table at 1000, 2000, 4000, 8000 for Luminous, Starlight, Celestial, and Iridescent respectively. (Default 0)
    .craftTime(10) // Optional int, how long the craft will take to complete in ticks. (Default 1)
    .register()

mods.astralsorcery.starlightaltar.constellationRecipeBuilder()
    .output(item('minecraft:pumpkin'))
    .matrix('ss ss',
        's   s',
        '  d  ',
        's   s',
        'ss ss')
    .key('s', item('minecraft:pumpkin_seeds'))
    .key('d', ore('dirt'))
    .register()

mods.astralsorcery.starlightaltar.traitRecipeBuilder()
    .output(item('astralsorcery:itemrockcrystalsimple').setSize(300).setPurity(50).setCutting(50))
    .matrix('sssss',
        'sgggs',
        'sgdgs',
        'sgggs',
        'sssss')
    .key('s', item('minecraft:pumpkin'))
    .key('g', ore('treeLeaves'))
    .key('d', item('minecraft:diamond_block'))
    .outerInput(item('astralsorcery:blockmarble')) // Items which are places on nearby relays once the craft has started.
    .outerInput(ore('ingotAstralStarmetal'))
    .outerInput(fluid('astralsorcery.liquidstarlight') * 1000)
    .outerInput(ore('treeSapling'))
    .constellation(constellation('discidia'))
    .register()

mods.astralsorcery.starlightaltar.removeByOutput(item('astralsorcery:itemarchitectwand'))
//mods.astralsorcery.starlightaltar.removeAll()


// Lightwell:
// Converts an input item into fluid, with a chance at breaking every time fluid is produced. The amount of fluid produced per interval can be increased via starlight.
mods.astralsorcery.lightwell.recipeBuilder()
    .catalyst(item('minecraft:stone'))
    .output(fluid('astralsorcery.liquidstarlight'))
    .productionMultiplier(1.0F) // Optional float, base amount of output produced per tick. (Default 0)
    .shatterMultiplier(15.0F) // Optional float, how likely the catalyst is to shatter when producing fluid, with higher being less likely but never 0%. (Default 0)
    .catalystColor(16725260) // Optional int, color code for particles. (Default null)
    .register()

mods.astralsorcery.lightwell.recipeBuilder()
    .catalyst(item('minecraft:obsidian'))
    .output(fluid('astralsorcery.liquidstarlight'))
    .productionMultiplier(1.0F)
    .shatterMultiplier(15.0F)
    .register()

mods.astralsorcery.lightwell.removeByCatalyst(item('minecraft:ice'))
mods.astralsorcery.lightwell.removeByInput(item('minecraft:packed_ice'))
mods.astralsorcery.lightwell.removeByOutput(fluid('lava'))
//mods.astralsorcery.lightwell.removeAll()


// Infusion Altar:
// Consumes buckets of Liquid Starlight when interacted with by a Resonating Wand to convert input items into output itemstacks after a time.
mods.astralsorcery.infusionaltar.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .consumption(1f) // Optional float, chance to consume a bucket of Liquid Starlight from around the altar. (Default 0.05f)
    .chalice(false) // Optional boolean, allows the recipe time to be reduced by *0.3 if a chalice is nearby if true. (Default true)
    .consumeMultiple(true) // Optional boolean, when consuming liquid starlight, consume all 12 source blocks instead of just one. (Default false)
    .time(10) // Optional integer, how long the recipe takes to complete in ticks. (Default 200)
    .register()

mods.astralsorcery.infusionaltar.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()

mods.astralsorcery.infusionaltar.removeByInput(item('minecraft:diamond_ore'))
mods.astralsorcery.infusionaltar.removeByOutput(item('minecraft:iron_ingot'))
//mods.astralsorcery.infusionaltar.removeAll()


// Grindstone:
// Converts an item into an itemstack with a chance of getting twice the amount after right clicking the grindstone based on weight.
mods.astralsorcery.grindstone.recipeBuilder()
    .input(ore('blockDiamond'))
    .output(item('minecraft:clay'))
    .weight(1) // Optional int, how likely to craft the recipe per right click (1/weight chance). (Default 0)
    .secondaryChance(1.0F) // Optional int, how likely to double the output. (Default 0)
    .register()

mods.astralsorcery.grindstone.recipeBuilder()
    .input(item('minecraft:stone'))
    .output(item('minecraft:cobblestone'))
    .weight(5)
    .register()

mods.astralsorcery.grindstone.removeByInput(item('minecraft:redstone_ore'))
mods.astralsorcery.grindstone.removeByOutput(ore('dustIron'))
//mods.astralsorcery.grindstone.removeAll()


// Light Transmutation:
// Converts an input Block or IBlockState into an output IBlockState after being sent a given amount of starlight, with the ability to require a specific constellation of starlight.
mods.astralsorcery.lighttransmutation.recipeBuilder()
    .input(block('minecraft:stone'))
    .output(block('astralsorcery:blockmarble'))
    .cost(100.0)
    .constellation(constellation('armara')) // Optional IWeakConstellation, a Major or Weak Constellation that the celestial crystal sending starlight must be attuned to. (Default: any)
    .inputDisplayStack(item('minecraft:stone')) // Optional itemstack, what item to be displayed as input in JEI. (Default: input)
    .outputDisplayStack(item('minecraft:dye:15').withNbt([display:[Name:'Marble']])) // Optional itemstack, what item to be displayed as output in JEI. (Default: output)
    .register()

mods.astralsorcery.lighttransmutation.recipeBuilder()
    .input(blockstate('minecraft:pumpkin')) // Will only convert a pumpkin facing north. Block should be used instead if this behavior is undesired.
    .output(blockstate('minecraft:diamond_block'))
    .cost(0)
    .register()

mods.astralsorcery.lighttransmutation.removeByInput(blockstate('minecraft:sandstone'))
mods.astralsorcery.lighttransmutation.removeByInput(block('minecraft:netherrack'))
mods.astralsorcery.lighttransmutation.removeByOutput(blockstate('minecraft:cake'))
mods.astralsorcery.lighttransmutation.removeByOutput(block('minecraft:lapis_block'))
//mods.astralsorcery.lighttransmutation.removeAll()


// Chalice Interaction:
// When two chalices containing different fluids are placed nearby, fluid may be consumed to produce an output itemstack.
mods.astralsorcery.chaliceinteraction.recipeBuilder()
    .output(item('astralsorcery:blockmarble'))
    .fluidInput(fluid('water') * 10)
    .fluidInput(fluid('astralsorcery.liquidstarlight') * 30)
    .register()

mods.astralsorcery.chaliceinteraction.removeByInput(fluid('water'), fluid('lava'))
//mods.astralsorcery.chaliceinteraction.removeByInput(fluid('astralsorcery.liquidstarlight'))
mods.astralsorcery.chaliceinteraction.removeByOutput(item('minecraft:ice'))
//mods.astralsorcery.chaliceinteraction.removeAll()


// Fountian:
// Adds virtual aquifers that can be accessed via the Evershifting Fountain's Necromantic Prime
mods.astralsorcery.fountain.chanceHelper()
    .fluid(fluid('astralsorcery.liquidstarlight'))
    .rarity(10000000)
    .minimumAmount(4000000)
    .variance(1000000)
    .register()

mods.astralsorcery.fountain.remove(fluid('lava'))
//mods.astralsorcery.fountain.removeAll()


// Mineralis Ritual Registry:
// Using a mineralis ritual will convert nearby stone blocks into random ores.
mods.astralsorcery.mineralisRitualRegistry.add(ore('blockDiamond'), 10000)

mods.astralsorcery.mineralisRitualRegistry.remove(ore('oreDiamond'))
//mods.astralsorcery.mineralisRitualRegistry.removeAll()


// Aevitas Perk Registry:
// Having the Stone Enrichment perk will convert nearby stone blocks into random ores.
mods.astralsorcery.aevitasPerkRegistry.add(ore('blockDiamond'), 10000)

mods.astralsorcery.aevitasPerkRegistry.remove(ore('oreDiamond'))
//mods.astralsorcery.aevitasPerkRegistry.removeAll()


// Trash Perk Registry:
// Having the Trash to Treasure perk turns items the player drops in the list defined in the config at 'perks/key_void_trash/DropList' into a chance at random ores.
mods.astralsorcery.trashPerkRegistry.add(ore('blockDiamond'), 10000)

mods.astralsorcery.trashPerkRegistry.remove(ore('oreDiamond'))
//mods.astralsorcery.trashPerkRegistry.removeAll()


// Treasure Shrine Registry:
// When the block in the middle of a Treasure Shrine structure is broken, a random ore from this list will replace it until the Treasure Shrine is exhausted.
mods.astralsorcery.treasureShrineRegistry.add(ore('blockDiamond'), 10000)

mods.astralsorcery.treasureShrineRegistry.remove(ore('oreDiamond'))
//mods.astralsorcery.treasureShrineRegistry.removeAll()
