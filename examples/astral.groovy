mods.astralsorcery.StarlightAltar.recipeBuilder()
        .output(item('minecraft:water_bucket'))
        .row('   ')
        .row(' B ')
        .row('   ')
        .key('B', item('minecraft:bucket'))
        .altarLevel(0)
        .starlight(1)
        .craftTime(1)
        .register()

mods.astralsorcery.StarlightAltar.recipeBuilder()
        .output(item('minecraft:pumpkin'))
        .matrix('ss ss',
                's   s',
                '  d  ',
                's   s',
                'ss ss')
        .key('s', item('minecraft:pumpkin_seeds'))
        .key('d', ore('dirt'))
        .setConstellationCraft()
        .starlight(0)
        .craftTime(0)
        .register()

mods.astralsorcery.StarlightAltar.recipeBuilder()
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
        .setTraitCraft()
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
        .chance(1)
        .register()

mods.astralsorcery.Grindstone.recipeBuilder()
        .input(ore('cropPumpkin'))
        .output(item('minecraft:pumpkin_seeds'))
        .chance(1)
        .doubleChance(1.0F)
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

mods.astralsorcery.LiquidInteraction.removeByOutput(item('minecraft:ice'))

mods.astralsorcery.LiquidInteraction.recipeBuilder()
        .result(item('astralsorcery:blockmarble'))
        .component(fluid('water') * 10)
        .component(fluid('astralsorcery.liquidstarlight') * 30)
        .chance(1.0F)
        .chance(1.0F)
        .register()

mods.astralsorcery.Constellation.constellationBuilder()
        .major()
        .name('square')
        .color('0xE01903')
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

mods.astralsorcery.StarlightAltar.recipeBuilder()
        .output(item('astralsorcery:itemcape').withNbt([astralsorcery: [constellationName: constellation('square').getUnlocalizedName()]]))
        .matrix('  s  ',
                '  d  ',
                'sdmds',
                '  d  ',
                '  s  ')
        .key('s', ore('dustAstralStarmetal'))
        .key('d', ore('gemDiamond'))
        .key('m', item('astralsorcery:itemcape'))
        .outerInput(ore('gemDiamond'))
        .outerInput(item('minecraft:water_bucket'))
        .outerInput(item('minecraft:rabbit_foot'))
        .outerInput(item('minecraft:fish'))
        .constellation(constellation('square'))
        .setTraitCraft()
        .starlight(0)
        .craftTime(0)
        .register()

mods.astralsorcery.StarlightAltar.recipeBuilder()
        .output(item('astralsorcery:itemconstellationpaper').withNbt([astralsorcery: [constellationName: constellation('square').getUnlocalizedName()]]))
        .matrix('  d  ',
                '  f  ',
                'dspsd',
                '  b  ',
                '  d  ')
        .key('p', item('astralsorcery:itemcraftingcomponent', 5))
        .key('d', ore('gemDiamond'))
        .key('f', item('minecraft:feather'))
        .key('s', ore('dustAstralStarmetal'))
        .key('b', ore('dyeBlack'))
        .outerInput(ore('gemDiamond'))
        .outerInput(item('minecraft:water_bucket'))
        .outerInput(item('minecraft:rabbit_foot'))
        .outerInput(item('minecraft:fish'))
        .setTraitCraft()
        .starlight(0)
        .craftTime(0)
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
