
// MODS_LOADED: botania
println 'mod \'botania\' detected, running script'

import net.minecraft.potion.PotionEffect
import net.minecraft.util.text.TextFormatting

// Bracket Handlers
// Brew:
// Gets one of botania's unique brews. Default options:
brew('speed')
brew('strength')
brew('haste')
brew('healing')
brew('jumpBoost')
brew('regen')
brew('regenWeak')
brew('resistance')
brew('fireResistance')
brew('waterBreathing')
brew('invisibility')
brew('nightVision')
brew('absorption')

brew('allure')
brew('soulCross')
brew('featherfeet')
brew('emptiness')
brew('bloodthirst')
brew('overload')
brew('clear')

brew('warpWard') // Only if Thaumcraft is installed



// Elven Trade:
// Convert in any number of item inputs into an item output.
def recipeElvenTrade = mods.botania.elventrade.recipeBuilder()
    .input(ore('ingotGold'), ore('ingotIron'))
    .output(item('botania:manaresource:7'))
    .register()

mods.botania.elventrade.removeByInputs(ore('ingotManasteel'))
mods.botania.elventrade.removeByOutputs(item('botania:dreamwood'))
//mods.botania.elventrade.removeAll()

// Mana Infusion
// Toss an item into a mana pool with an optional catalyst blockstate below the pool.
def recipeInfusion = mods.botania.manainfusion.recipeBuilder()
    .input(ore('ingotGold'))
    .output(item('botania:manaresource', 1))
    .mana(500)
    .catalyst(blockstate('minecraft:stone'))
    .register()

mods.botania.manainfusion.removeByInput(item('minecraft:ender_pearl'))
mods.botania.manainfusion.removeByCatalyst(blockstate('botania:alchemycatalyst'))
mods.botania.manainfusion.removeByOutput(item('botania:managlass'))
//mods.botania.manainfusion.removeAll()

// Pure Daisy:
// Convert a given block to another blockstate after a period of time
mods.botania.puredaisy.recipeBuilder()
    .input(ore('plankWood')) // input must be a Block, IBlockState, Oredict, or a String representing an oredict
    .output(blockstate('minecraft:clay'))
    .time(5)
    .register()

mods.botania.puredaisy.add(blockstate('minecraft:iron_block'), blockstate('minecraft:gold_block'), 20)

mods.botania.puredaisy.removeByInput(blockstate('minecraft:water'))
mods.botania.puredaisy.removeByInput(ore('logWood'))
mods.botania.puredaisy.removeByOutput(blockstate('botania:livingrock'))
//mods.botania.puredaisy.removeAll()


// Petal Apothecary
// Converts item inputs into an item output consuming water and a seed.
def recipePetal = mods.botania.apothecary.recipeBuilder()
    .input(ore('blockGold'), ore('ingotIron'), item('minecraft:apple'))
    .output(item('minecraft:golden_apple'))
    .register()

mods.botania.apothecary.removeByInput(ore('runeFireB'))
mods.botania.apothecary.removeByInputs(ore('petalYellow'), ore('petalBrown'))
mods.botania.apothecary.removeByOutput(item('botania:specialflower').withNbt(["type": "puredaisy"]))
//mods.botania.apothecary.removeAll()

// Orechid:
// Converts stone blocks into one of a few ore blocks at the cost of mana
mods.botania.orechid.add(ore('oreEmerald'), 1350)

mods.botania.orechid.removeByOutput(ore('oreEmerald'))
mods.botania.orechid.removeByOutput('oreCoal')
//mods.botania.orechid.removeAll()

// Orechid Ignem:
// Converts netherrack blocks into one of a few ore blocks at the cost of mana
mods.botania.orechidignem.add(ore('blockGold'), 1800)

mods.botania.orechidignem.removeByOutput(ore('oreQuartz'))
//mods.botania.orechidignem.removeByOutput('oreQuartz')
//mods.botania.orechidignem.removeAll()

// Magnet:
// Add or remove items from the magnet blacklist.
mods.botania.magnet.addToBlacklist(item('minecraft:diamond'))

// Brew Effect:
// Creates a custom brew, but not a recipe for the brew.
mods.botania.brew.brewBuilder()
    .key('groovy_example_brew') // Must be a unique key
    .name('Groovy Brew')
    .color(0x00FFFF) // Optional, default 0xFFFFFF
    .cost(100) // Alias 'mana'
    .effect(new PotionEffect(potion('strength'), 1800, 3),
            new PotionEffect(potion('speed'), 1800, 2),
            new PotionEffect(potion('weakness'), 3600, 1))
    .incense(true) // Optional, default true. Controls if the Incense Stick can be infused
    .bloodPendant(true) // Optional, default true. Controls if the Tainted Blood Pendant can be infused
    .register()

// Brew Recipe:
// Converts a non-infused Managlass Vial, Alfglass Flask, Incense Stick, or Tainted Blood Pendant into one infused to hold the given brew at the cost of item inputs and mana.
def recipeBrewing = mods.botania.brewrecipe.recipeBuilder()
    .input(item('minecraft:clay'), ore('ingotGold'), ore('gemDiamond'))
    .brew(brew('absorption')) // Alias 'output'
    .register()

mods.botania.brewrecipe.removeByInput(item('minecraft:iron_ingot'))
mods.botania.brewrecipe.removeByOutput('speed')
mods.botania.brewrecipe.removeByOutput(brew('allure'))
//mods.botania.brewrecipe.removeAll()

// Rune Altar:
// Converts a items inputs into an item ouput at the cost of mana when a Livingrock item is thrown atop the altar and right clicked with a Wand of the Forest
def recipeRune = mods.botania.runealtar.recipeBuilder()
    .input(ore('gemEmerald'), item('minecraft:apple'))
    .output(item('minecraft:diamond'))
    .mana(500)
    .register()

mods.botania.runealtar.removeByInput(ore('runeEarthB'))
mods.botania.runealtar.removeByInputs(ore('feather'), ore('string'))
mods.botania.runealtar.removeByOutput(item('botania:rune:1'))
//mods.botania.runealtar.removeAll()


// Knowledge:
// Creates a new type of knowledge that Lexica Botania entries may be gated with.
// Can only be created, format id, color, autoUnlock
def newType = mods.botania.knowledge.add('newType', TextFormatting.RED, true)

// Lexicon:
// Manipulate the Lexica Botania.

// Category creates a new entry on the front page of the Lexica Botania.
mods.botania.lexicon.category.add('test', resource('minecraft:textures/items/apple.png'))
mods.botania.lexicon.category.add('first', resource('minecraft:textures/items/clay_ball.png'), 100)

mods.botania.lexicon.category.remove('botania.category.alfhomancy')
mods.botania.lexicon.category.removeCategory('botania.category.misc')
//mods.botania.lexicon.category.removeAll()

// Entry creates a new entry in a given category.
mods.botania.lexicon.entry.entryBuilder()
    .name('test_entry')
    .icon(ore('blockIron'))
    .category('test')
    .knowledgeType(newType) // Locks entry behind the given knowledge type. Also colors the entry name.
    .page(
        // Page creates a new page to be used in entries.
        mods.botania.lexicon.page.createTextPage('groovy.exampleTextPage'), // looks for localization at "groovy.exampleTextPage"
        mods.botania.lexicon.page.createLoreTextPage('groovy.exampleLoreTextPage'),
        mods.botania.lexicon.page.createImagePage('groovy.exampleImagePage', 'minecraft:textures/items/apple.png'),
        mods.botania.lexicon.page.createEntityPage('groovy.exampleEntityPage', 100, 'minecraft:wither_skeleton'),
        mods.botania.lexicon.page.createEntityPage('groovy.exampleEntityPage', 5, entity('minecraft:wither_skeleton')),
        mods.botania.lexicon.page.createCraftingPage('groovy.exampleCraftingPage', 'minecraft:clay'),
        mods.botania.lexicon.page.createBrewingPage('groovy.exampleBrewingPage', 'bottomText', recipeBrewing),
        mods.botania.lexicon.page.createInfusionPage('groovy.exampleInfusionPage', recipeInfusion),
        mods.botania.lexicon.page.createRunePage('groovy.exampleRunePage', recipeRune),
        mods.botania.lexicon.page.createPetalPage('groovy.examplePetalPage', recipePetal),
        mods.botania.lexicon.page.createElvenTradePage('groovy.exampleElvenTradePage', recipeElvenTrade))
    .register()

mods.botania.lexicon.entry.remove('botania.entry.flowers')
mods.botania.lexicon.entry.removeEntry('botania.entry.apothecary')
//mods.botania.lexicon.entry.removeAll()

// cont. from Page
mods.botania.lexicon.page.removeByEntry('botania.entry.runeAltar')
//mods.botania.lexicon.page.removeAll()
