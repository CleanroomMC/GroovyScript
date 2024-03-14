
// Auto generated groovyscript example file
// MODS_LOADED: botania

import net.minecraft.potion.PotionEffect
import net.minecraft.util.text.TextFormatting

println 'mod \'botania\' detected, running script'

// Petal Apothecary:
// Converts item inputs into an item output consuming water and a seed.

mods.botania.apothecary.removeByInput(ore('runeFireB'))
mods.botania.apothecary.removeByInputs(ore('petalYellow'), ore('petalBrown'))
mods.botania.apothecary.removeByOutput(item('botania:specialflower').withNbt(['type': 'puredaisy']))
// mods.botania.apothecary.removeAll()

mods.botania.apothecary.recipeBuilder()
    .input(ore('blockGold'), ore('ingotIron'), item('minecraft:apple'))
    .output(item('minecraft:golden_apple'))
    .register()



// Brew Effect:
// Creates a custom brew, but not a recipe for the brew.

// mods.botania.brew.removeAll()

mods.botania.brew.brewBuilder()
    .key('groovy_example_brew')
    .name('Groovy Brew')
    .color(0x00FFFF)
    .cost(100)
    .effect(new PotionEffect(potion('minecraft:strength'), 1800, 3), new PotionEffect(potion('minecraft:speed'), 1800, 2), new PotionEffect(potion('minecraft:weakness'), 3600, 1))
    .incense(true)
    .bloodPendant(true)
    .register()


// Brew Recipe:
// Converts a non-infused Managlass Vial, Alfglass Flask, Incense Stick, or Tainted Blood Pendant into one infused to hold
// the given brew at the cost of item inputs and mana.

mods.botania.brew_recipe.removeByInput(item('minecraft:iron_ingot'))
mods.botania.brew_recipe.removeByOutput(brew('allure'))
mods.botania.brew_recipe.removeByOutput('speed')
// mods.botania.brew_recipe.removeAll()

mods.botania.brew_recipe.recipeBuilder()
    .input(item('minecraft:clay'), ore('ingotGold'), ore('gemDiamond'))
    .brew(brew('absorption'))
    .register()


// Elven Trade:
// Convert in any number of item inputs into an item output.

mods.botania.elven_trade.removeByInputs(ore('ingotManasteel'))
mods.botania.elven_trade.removeByOutputs(item('botania:dreamwood'))
// mods.botania.elven_trade.removeAll()

mods.botania.elven_trade.recipeBuilder()
    .input(ore('ingotGold'), ore('ingotIron'))
    .output(item('botania:manaresource:7'))
    .register()



// Magnet:
// Add or remove items from the magnet blacklist


mods.botania.magnet.addToBlacklist(item('minecraft:diamond'))

// Mana Infusion:
// Toss an item into a mana pool with an optional catalyst blockstate below the pool.

mods.botania.mana_infusion.removeByCatalyst(blockstate('botania:alchemycatalyst'))
mods.botania.mana_infusion.removeByInput(item('minecraft:ender_pearl'))
mods.botania.mana_infusion.removeByOutput(item('botania:managlass'))
// mods.botania.mana_infusion.removeAll()

mods.botania.mana_infusion.recipeBuilder()
    .input(ore('ingotGold'))
    .output(item('botania:manaresource', 1))
    .mana(500)
    .catalyst(blockstate('minecraft:stone'))
    .register()



// Orechid:
// Converts stone blocks into one of a few ore blocks at the cost of mana.

// mods.botania.orechid.removeByOutput(ore('oreQuartz'))
// mods.botania.orechid.removeByOutput(ore('oreEmerald'))
mods.botania.orechid.removeByOutput('oreCoal')
// mods.botania.orechid.removeAll()

mods.botania.orechid.add(ore('blockGold'), 1800)
mods.botania.orechid.add(ore('oreEmerald'), 1350)

// Orechid Ignem:
// Converts netherrack blocks into one of a few ore blocks at the cost of mana.

// mods.botania.orechid_ignem.removeByOutput(ore('oreQuartz'))
// mods.botania.orechid_ignem.removeByOutput(ore('oreEmerald'))
mods.botania.orechid_ignem.removeByOutput('oreQuartz')
// mods.botania.orechid_ignem.removeAll()

mods.botania.orechid_ignem.add(ore('blockGold'), 1800)
mods.botania.orechid_ignem.add(ore('oreEmerald'), 1350)

// Pure Daisy:
// Convert a given block to another blockstate after a period of time.

mods.botania.pure_daisy.removeByInput(blockstate('minecraft:water'))
mods.botania.pure_daisy.removeByInput(ore('logWood'))
mods.botania.pure_daisy.removeByOutput(blockstate('botania:livingrock'))
// mods.botania.pure_daisy.removeAll()

mods.botania.pure_daisy.recipeBuilder()
    .input(ore('plankWood'))
    .output(blockstate('minecraft:clay'))
    .time(5)
    .register()



// Rune Altar:
// Converts a items inputs into an item ouput at the cost of mana when a Livingrock item is thrown atop the altar and right
// clicked with a Wand of the Forest.

mods.botania.rune_altar.removeByInput(ore('runeEarthB'))
mods.botania.rune_altar.removeByInputs(ore('feather'), ore('string'))
mods.botania.rune_altar.removeByOutput(item('botania:rune:1'))
// mods.botania.rune_altar.removeAll()

mods.botania.rune_altar.recipeBuilder()
    .input(ore('gemEmerald'), item('minecraft:apple'))
    .output(item('minecraft:diamond'))
    .mana(500)
    .register()



// Lexicon Knowledge:
// Creates a new type of knowledge that Lexica Botania entries may be gated with. Can only be created.

def newType = mods.botania.knowledge.add('newType', TextFormatting.RED, true)

// Lexicon Category:
// Category creates a new entry on the front page of the Lexica Botania.

mods.botania.category.remove('botania.category.alfhomancy')
mods.botania.category.removeCategory('botania.category.misc')
// mods.botania.category.removeAll()

mods.botania.category.add('test', resource('minecraft:textures/items/apple.png'))
mods.botania.category.add('first', resource('minecraft:textures/items/clay_ball.png'), 100)

// Lexicon Page:
// Page creates a new page to be used in entries.

mods.botania.page.removeByEntry('botania.entry.runeAltar')
// mods.botania.page.removeAll()

// mods.botania.page.createBrewingPage('groovy.exampleBrewingPage', 'bottomText', 'bottomText', mods.botania.brewrecipe.recipeBuilder().input(item('minecraft:clay'), ore('ingotGold'), ore('gemDiamond')).brew(brew('absorption')).register())
mods.botania.page.createCraftingPage('groovy.exampleCraftingPage', 'minecraft:clay')
// mods.botania.page.createElvenTradePage('groovy.exampleElvenTradePage', mods.botania.elventrade.recipeBuilder().input(ore('ingotGold'), ore('ingotIron')).output(item('botania:manaresource:7')).register())
mods.botania.page.createEntityPage('groovy.exampleEntityPage', 5, entity('minecraft:wither_skeleton'))
mods.botania.page.createEntityPage('groovy.exampleEntityPage', 100, 'minecraft:wither_skeleton')
mods.botania.page.createImagePage('groovy.exampleImagePage', 'minecraft:textures/items/apple.png')
// mods.botania.page.createInfusionPage('groovy.exampleInfusionPage', mods.botania.manainfusion.recipeBuilder().input(ore('ingotGold')).output(item('botania:manaresource', 1)).mana(500).catalyst(blockstate('minecraft:stone')).register())
mods.botania.page.createLoreTextPage('groovy.exampleLoreTextPage')
// mods.botania.page.createPetalPage('groovy.examplePetalPage', mods.botania.apothecary.recipeBuilder().input(ore('blockGold'), ore('ingotIron'), item('minecraft:apple')).output(item('minecraft:golden_apple')).register())
// mods.botania.page.createRunePage('groovy.exampleRunePage', mods.botania.runealtar.recipeBuilder().input(ore('gemEmerald'), item('minecraft:apple')).output(item('minecraft:diamond')).mana(500).register())
mods.botania.page.createTextPage('groovy.exampleTextPage')

// Lexicon Entry:
// Entry creates a new entry in a given category.

mods.botania.entry.remove('botania.entry.flowers')
mods.botania.entry.removeEntry('botania.entry.apothecary')
// mods.botania.entry.removeAll()

mods.botania.entry.entryBuilder()
    .name('test_entry')
    .icon(ore('blockIron'))
    .category('test')
    .knowledgeType(newType)
    .page(mods.botania.lexicon.page.createTextPage('groovy.exampleTextPage'))
    .register()




