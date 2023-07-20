import net.minecraft.util.math.MathHelper

mods.astralsorcery.StarlightAltar.discoveryRecipeBuilder()
        .output(item('minecraft:water_bucket'))
        .row('   ')
        .row(' B ')
        .row('   ')
        .key('B', item('minecraft:bucket'))
        .starlight(1)
        .craftTime(1)
        .register()

mods.astralsorcery.StarlightAltar.constellationRecipeBuilder()
        .output(item('minecraft:pumpkin'))
        .matrix('ss ss',
                's   s',
                '  d  ',
                's   s',
                'ss ss')
        .key('s', item('minecraft:pumpkin_seeds'))
        .key('d', ore('dirt'))
        .starlight(0)
        .craftTime(0)
        .register()

mods.astralsorcery.StarlightAltar.traitRecipeBuilder()
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
        .starlight(0)
        .craftTime(0)
        .register()

mods.astralsorcery.StarlightAltar.removeByOutput(item('astralsorcery:itemarchitectwand'))

mods.astralsorcery.Lightwell.removeByOutput(fluid('lava'))

mods.astralsorcery.Lightwell.recipeBuilder()
        .catalyst(item('minecraft:stone'))
        .output(fluid('lava'))
        .productionMultiplier(1.0F)
        .shatterMultiplier(15.0F)
        .register()

mods.astralsorcery.Lightwell.recipeBuilder()
        .catalyst(item('minecraft:obsidian'))
        .output(fluid('lava'))
        .productionMultiplier(1.0F)
        .shatterMultiplier(15.0F)
        .catalystColor(16725260)
        .register()

mods.astralsorcery.InfusionAltar.recipeBuilder()
        .input(item('minecraft:dirt'))
        .output(item('minecraft:pumpkin'))
        .register()

mods.astralsorcery.InfusionAltar.removeByOutput(item('minecraft:iron_ingot'))

mods.astralsorcery.Grindstone.recipeBuilder()
        .input(item('minecraft:stone'))
        .output(item('minecraft:cobblestone'))
        .weight(1)
        .register()

mods.astralsorcery.Grindstone.recipeBuilder()
        .input(ore('cropPumpkin'))
        .output(item('minecraft:pumpkin_seeds'))
        .weight(1)
        .secondaryChance(1.0F)
        .register()

mods.astralsorcery.Grindstone.removeByOutput(ore('dustIron'))
mods.astralsorcery.Grindstone.removeByOutput(item('minecraft:redstone'))

mods.astralsorcery.LightTransmutation.removeByOutput(blockstate('minecraft:cake'))
mods.astralsorcery.LightTransmutation.removeByOutput(block('minecraft:lapis_block'))

mods.astralsorcery.LightTransmutation.recipeBuilder()
        .input(block('minecraft:stone'))
        .output(block('astralsorcery:blockmarble'))
        .cost(100.0)
        .register()

mods.astralsorcery.LightTransmutation.recipeBuilder()
        .input(blockstate('minecraft:pumpkin'))
        .output(blockstate('minecraft:diamond_block'))
        .cost(400.0)
        .register()

mods.astralsorcery.ChaliceInteraction.removeByOutput(item('minecraft:ice'))

mods.astralsorcery.ChaliceInteraction.recipeBuilder()
        .result(item('astralsorcery:blockmarble'))
        .component(fluid('water') * 10)
        .component(fluid('astralsorcery.liquidstarlight') * 30)
        .register()

mods.astralsorcery.Constellation.constellationBuilder()
        .major()
        .name('square')
        .color(0xE01903)
        .connection(12, 2, 2, 2)
        .connection(12, 12, 12, 2)
        .connection(2, 12, 12, 12)
        .connection(2, 2, 2, 12)
        .register()

mods.astralsorcery.Constellation.remove(constellation('bootes'))

mods.astralsorcery.Constellation.constellationMapEffectBuilder()
        .constellation(constellation('square'))
        .enchantmentEffect(enchantment('luck_of_the_sea'), 1, 3)
        .potionEffect(potion('luck'), 1,2)
        .register()

mods.astralsorcery.Constellation.removeConstellationMapEffect(constellation('discidia'))

mods.astralsorcery.Constellation.signatureItems()
        .constellation(constellation('square'))
        .addItem(ore('gemDiamond'))
        .addItem(item('minecraft:water_bucket'))
        .addItem(item('minecraft:rabbit_foot'))
        .addItem(item('minecraft:fish'))
        .register()

mods.astralsorcery.PerkTree.attributePerkBuilder()
        .name("this_is_fun")
        .point(28, 28)
        .connection("astralsorcery:magnet_ats_reach")
        .modifier(mods.astralsorcery.PerkTree.effectBuilder()
                .modifier(3)
                .mode(2)
                .type("ATTR_TYPE_REACH"))
        .register()

mods.astralsorcery.PerkTree.movePerk(mods.astralsorcery.PerkTree.getPerk("astralsorcery:magnet_ats_reach"), 30, 30)

mods.astralsorcery.PerkTree.remove("astralsorcery:magnet_ats_reach")

mods.astralsorcery.Research.researchBuilder()
        .name('MY_TEST_RESEARCH')
        .point(5,5)
        .icon(item('minecraft:pumpkin'))
        .discovery()
        .page(mods.astralsorcery.Research.pageBuilder().textPage('GROOVYSCRIPT.RESEARCH.PAGE.TEST'))
        .page(mods.astralsorcery.Research.pageBuilder().emptyPage())
        .connectionFrom('ALTAR1')
        .register()

mods.astralsorcery.Research.researchBuilder()
        .name('MY_TEST_RESEARCH2')
        .point(5,5)
        .icon(item('minecraft:pumpkin'))
        .constellation()
        .page(mods.astralsorcery.Research.pageBuilder().textPage('GROOVYSCRIPT.RESEARCH.PAGE.TEST2'))
        .page(mods.astralsorcery.Research.pageBuilder().constellationRecipePage(item('minecraft:pumpkin')))
        .register()

mods.astralsorcery.Research.connectNodes('MY_TEST_RESEARCH2', 'ENHANCED_COLLECTOR')

mods.astralsorcery.Research.moveNode('SOOTYMARBLE', 5, 6)

mods.astralsorcery.Research.removeNode('CPAPER')

mods.astralsorcery.Research.disconnectNodes('MY_TEST_RESEARCH', 'ALTAR1')

mods.astralsorcery.Fountain.remove(fluid('lava'))

mods.astralsorcery.Fountain.chanceHelper()
        .fluid(fluid('astralsorcery.liquidstarlight'))
        .rarity(10000000)
        .minimumAmount(4000000)
        .variance(1000000)
        .register()

mods.astralsorcery.aevitasPerkOreChance.remove(ore('oreCoal'))
mods.astralsorcery.aevitasPerkOreChance.remove(ore('oreIron'))
mods.astralsorcery.aevitasPerkOreChance.remove(ore('oreLapis'))
mods.astralsorcery.aevitasPerkOreChance.remove(ore('oreRedstone'))
mods.astralsorcery.aevitasPerkOreChance.remove(ore('oreGold'))
mods.astralsorcery.aevitasPerkOreChance.remove(ore('oreEmerald'))

mods.astralsorcery.aevitasPerkOreChance.add(ore('oreQuartz'), 1000)

mods.astralsorcery.PerkTreeConfig.setLevelCap(50)
mods.astralsorcery.PerkTreeConfig.setXpFunction{ int i, long prev -> prev + 1000L + MathHelper.lfloor(Math.pow(2.0, i / 2.0F + 3)) }
