import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextFormatting

def newType = mods.botania.Knowledge.add('newType', TextFormatting.RED, true)

mods.botania.ElvenTrade.removeByInputs(ore('ingotManasteel'))
mods.botania.ElvenTrade.recipeBuilder()
        .input(ore('ingotGold'), ore('ingotIron'))
        .output(item('botania:manaresource', 7))
        .register()

mods.botania.ManaInfusion.recipeBuilder()
        .input(ore('ingotGold'))
        .output(item('botania:manaresource', 1))
        .mana(500)
        .catalyst(blockstate('minecraft:stone'))
        .register()
mods.botania.ManaInfusion.removeByInput(item('minecraft:ender_pearl'))

mods.botania.PureDaisy.add(blockstate('minecraft:iron_block'), blockstate('minecraft:gold_block'), 20)
mods.botania.PureDaisy.removeByInput(blockstate('minecraft:water'))
mods.botania.PureDaisy.removeByInput(ore('logWood'))

mods.botania.Apothecary.removeByInput(ore('petalYellow'), ore('petalBrown'))
mods.botania.Apothecary.recipeBuilder()
        .output(item('minecraft:golden_apple'))
        .input(ore('blockGold'))
        .input(ore('ingotIron'))
        .input(item('minecraft:apple'))
        .register()

mods.botania.Orechid.removeByOutput(ore('oreEmerald'))
mods.botania.Orechid.add(ore('oreEmerald'), 1350)
mods.botania.OrechidIgnem.add(ore('blockGold'), 1800)

mods.botania.Magnet.addToBlacklist(item('minecraft:diamond'))

def myRecipe = mods.botania.Brew.recipeBuilder()
        .input(ore('ingotIron'))
        .input(ore('ingotGold'))
        .input(ore('gemDiamond'))
        .output(brew('absorption'))
        .register()
mods.botania.Brew.removeByInput(item('minecraft:iron_ingot'))

mods.botania.RuneAltar.removeByInput(ore('runeEarthB'))
mods.botania.RuneAltar.recipeBuilder()
        .output(item('minecraft:diamond'))
        .mana(500)
        .input(ore('gemEmerald'))
        .input(item('minecraft:apple'))
        .register()

mods.botania.Lexicon.Category.add('test', new ResourceLocation('minecraft', 'textures/items/apple.png'))
def myPage = mods.botania.Lexicon.Page.createTextPage('botania.testPage')
def rPage = mods.botania.Lexicon.Page.createBrewingPage('brewtest', 'bottomText', myRecipe)
mods.botania.Lexicon.Entry.entryBuilder()
        .name('test_entry')
        .icon(ore('blockIron'))
        .category('test')
        .page(myPage)
        .page(rPage)
        .knowledgeType(newType)
        .register()