
// Imports must happen above all other code.

import net.minecraftforge.event.entity.living.EnderTeleportEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraft.util.text.TextComponentString

def ore_iron = ore('ingotIron')
def item_iron = item('minecraft:iron_ingot')
log.info(item_iron in ore_iron) // true
log.info(item_iron in item_iron) // true
log.info(ore_iron in item_iron) // false
log.info(item_iron << ore_iron) // true
log.info((item_iron * 3) << ore_iron) // false
log.info(ore_iron >> item_iron) // true
log.info(ore_iron >> (item_iron * 3)) // false

// Crafting recipes are typically created via recipe builder, but also have shorthand versions for some common uses.
// Here are a series of examples, with the shorthand and corresponding recipe builder:

//crafting.addShaped(item('minecraft:gold_block'), [[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[null, null, null],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
crafting.shapedBuilder()
    .output(item('minecraft:gold_block'))
    .shape([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[null, null, null],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
    .register()

//crafting.addShaped('gold_v_to_clay', item('minecraft:clay'), [[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[null,item('minecraft:gold_ingot'),null]])
crafting.shapedBuilder()
    .name('gold_v_to_clay')
    .output(item('minecraft:clay'))
    .shape([[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[null,item('minecraft:gold_ingot'),null]])
    .register()

//crafting.addShaped(resource('example:resource_location'), item('minecraft:clay'), [[item('minecraft:cobblestone')],[item('minecraft:nether_star')],[item('minecraft:cobblestone')]])
crafting.shapedBuilder()
    .name(resource('example:resource_location'))
    .output(item('minecraft:clay'))
    .shape([[item('minecraft:cobblestone')],[item('minecraft:nether_star')],[item('minecraft:cobblestone')]])
    .register()

//crafting.addShapeless(item('minecraft:clay'), [item('minecraft:cobblestone'),item('minecraft:nether_star'),item('minecraft:gold_ingot')])
crafting.shapelessBuilder()
    .output(item('minecraft:clay'))
    .input([item('minecraft:cobblestone'),item('minecraft:nether_star'),item('minecraft:gold_ingot')])
    .register()

//crafting.addShapeless('precious_to_clay', item('minecraft:clay'), [item('minecraft:diamond'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')])
crafting.shapelessBuilder()
    .name('precious_to_clay')
    .output(item('minecraft:clay'))
    .input([item('minecraft:diamond'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')])
    .register()

//crafting.addShapeless(resource('example:resource_location2'), item('minecraft:clay'), [item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
crafting.shapelessBuilder()
    .name(resource('example:resource_location2'))
    .output(item('minecraft:clay'))
    .input([item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
    .register()

//crafting.replaceShapeless(item('minecraft:ender_eye'), [item('minecraft:ender_pearl'),item('minecraft:nether_star')])
crafting.shapelessBuilder()
    .output(item('minecraft:ender_eye'))
    .input([item('minecraft:ender_pearl'),item('minecraft:nether_star')])
    .replace()
    .register()

//crafting.replaceShapeless('minecraft:pink_dye_from_pink_tulp', item('minecraft:clay'), [item('minecraft:nether_star')])
crafting.shapelessBuilder()
    .name('minecraft:pink_dye_from_pink_tulp')
    .output(item('minecraft:clay'))
    .input([item('minecraft:nether_star')])
    .replaceByName()
    .register()

//crafting.replaceShapeless(resource('minecraft:pink_dye_from_peony'), item('minecraft:clay'), [item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
crafting.shapelessBuilder()
    .name(resource('minecraft:pink_dye_from_peony'))
    .output(item('minecraft:clay'))
    .input([item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
    .replaceByName()
    .register()

//crafting.replaceShaped(item('minecraft:chest'), [[ore('logWood'),ore('logWood'),ore('logWood')],[ore('logWood'),null,ore('logWood')],[ore('logWood'),ore('logWood'),ore('logWood')]])
crafting.shapedBuilder()
    .output(item('minecraft:chest'))
    .shape([[ore('logWood'),ore('logWood'),ore('logWood')],[ore('logWood'),null,ore('logWood')],[ore('logWood'),ore('logWood'),ore('logWood')]])
    .replace()
    .register()

//crafting.replaceShaped('gold_to_diamonds', item('minecraft:diamond') * 8, [[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
crafting.shapedBuilder()
    .name('gold_to_diamonds')
    .output(item('minecraft:diamond') * 8)
    .shape([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
    .replaceByName()
    .register()

//crafting.replaceShaped(resource('minecraft:sea_lantern'), item('minecraft:clay'), [[item('minecraft:glowstone')],[item('minecraft:glowstone')],[item('minecraft:glowstone')]])
crafting.shapedBuilder()
    .name(resource('minecraft:sea_lantern'))
    .output(item('minecraft:clay'))
    .shape([[item('minecraft:glowstone')],[item('minecraft:glowstone')],[item('minecraft:glowstone')]])
    .replaceByName()
    .register()


// The recipe builder also has some additional features, including
// The abilty to input a string and a set of keys, significantly improving readability.

crafting.shapedBuilder()
    .name('nether_star_from_clay_and_tnt')
    .output(item('minecraft:nether_star'))
    .row('TXT')
    .row('X X') // The key space (' ') is set to Ingredient.EMPTY (and cannot be changed)
    .row('!X!') // WARNING: All rows must be the same length.
    .key('T', item('minecraft:tnt')) // Keys are case sensitive
    .key('X', item('minecraft:clay').reuse()) // Reuse returns the item instead of consuming it
    .key('!', item('minecraft:tnt').transform({ _ -> item('minecraft:diamond') })) // Transforms the item into a diamond
    .register()

// A map of keys can also be passed in. The map can contain any IIngredient, including those with transforms or otherwise
def presetKeys = [
    X: item('minecraft:clay'),
    T: item('minecraft:tnt'),
    D: item('minecraft:diamond'),
    S: ore('netherStar').reuse(),
    '!': item('minecraft:tnt').transform({ _ -> item('minecraft:diamond') }),
    G: ore('ingotGold'),
    W: fluid('water') * 1000, // Any tank that contains >= 1000 mb and can be reduced by 1000.
    '0': item('minecraft:diamond_sword').withNbt([display:[Name:'Sword with Specific NBT data']])
]

crafting.shapedBuilder()
    .output(item('minecraft:clay_ball') * 3)
    .shape('S S', // Shape allows a series of either comma separated strings or an array of strings
           ' G ',
           'SWS')
    .key(presetKeys)
    .register()

crafting.shapedBuilder()
    .name('nether_star_duplication_with_tnt')
    .output(item('minecraft:nether_star'))
    .row('!!!')
    .row('!S!')
    .row('!!!')
    .key(presetKeys)
    .register()

crafting.shapedBuilder()
    .output(item('minecraft:clay'))
    .row('   ')
    .row(' 0 ') // Requires the item be in exactly the center of the 3x3 grid.
    .row('   ')
    .key(presetKeys)
    .register()


crafting.remove('minecraft:mossy_stonebrick') // Remove the entry with the recipe ID
crafting.remove(resource('minecraft:stonebrick'))
crafting.removeByOutput(item('minecraft:gold_ingot')) // Remove all recipes with the output
//crafting.removeByInput(item('minecraft:iron_ingot')) // Remove all recipes containing the ingredient as an input
//crafting.removeAll()


// Furnace
//furnace.add(ore('ingotIron'), item('minecraft:diamond')) // exp has a default value of 0.1
furnace.add(item('minecraft:nether_star'), item('minecraft:clay') * 64, 13)

furnace.recipeBuilder()
    .input(ore('ingotGold'))
    .output(item('minecraft:nether_star'))
    .exp(0.5) // Optional float, xp gained per recipe completion. Default 0.1f
    .register()

furnace.removeByInput(item('minecraft:clay'))
furnace.removeByOutput(item('minecraft:brick'))
//furnace.removeAll()


// OreDictionary (OreDict)
oredict.add('ingotGold', item('minecraft:nether_star'))
oredict.add('netherStar', item('minecraft:gold_ingot'))
oredict.remove('netherStar', item('minecraft:nether_star'))

oredict.clear('plankWood') // Note that any recipes using this oredict will silently die
oredict.removeAll('ingotIron')
//oredict.removeAll()


// Starting inventory
player.testingStartingItems = false // Enable this to have the items be given every time you join the world. Use in testing only.
player.replaceDefaultInventory = true // Enable this to replace any existing items with GroovyScript's starting inventory items.
player.setStartingItems(true, // Boolean determines if items are added to specific slots, with true meaning the items are slot specific.
    item('minecraft:clay').withNbt([display:[Name:'Hotbar']]), null, null, null, null, null, null, null, null,
    item('minecraft:clay').withNbt([display:[Name:'Top row of inventory']]), null, null, null, null, null, null, null, null,
    item('minecraft:clay').withNbt([display:[Name:'Middle row of inventory']]), null, null, null, null, null, null, null, null,
    item('minecraft:clay').withNbt([display:[Name:'Bottom row of inventory']]), null, null, null, null, null, null, null, null,
    item('minecraft:diamond_boots'), item('minecraft:diamond_leggings'), item('minecraft:diamond_chestplate'), item('minecraft:diamond_helmet'),
    item('minecraft:clay').withNbt([display:[Name:'Offhand']])
)

// Items can also be added to specific slots individually
//player.addStartingItem(item('minecraft:diamond_boots'), 36) // Note that this will error if the slot has already been set to an item
// 0-8 is hotbar, 9-17 is top row, 18-26 is middle row, 26-35 is bottom row, 36 is boots, 37 is leggings, 38 is chestplate, 39 is helmet, and 40 is offhand.

// Or added to any slot in the inventory
player.addStartingItem(item('minecraft:clay_ball'))
player.addStartingItem(item('minecraft:gold_ingot'))
player.addStartingItem(item('minecraft:diamond'))
player.addStartingItem(item('minecraft:nether_star'))
player.addStartingItem(item('minecraft:water_bucket'))

// Text formatting bracket handler
// Colors: BLACK (0), DARK_BLUE (1), DARK_GREEN (2), DARK_AQUA (3), DARK_RED (4), DARK_PURPLE (5), GOLD (6), GRAY (7), DARK_GRAY (8),BLUE (9), GREEN (10), AQUA (11), RED (12), LIGHT_PURPLE (13), YELLOW (14), WHITE (15)
// Emphasis: OBFUSCATED, BOLD, STRIKETHROUGH, UNDERLINE, ITALIC
// Clears formatting: RESET (-1)

// Note: only one text format can be set at a time. To have multiple, use a different way.
rarity.set(textformat('AQUA'), item('minecraft:diamond'))
// And item mixin
item('minecraft:clay').setRarity(textformat('BOLD'))

// Removes text formatting from the name.
rarity.set(textformat('RESET'), item('minecraft:enchanted_book'))
item('minecraft:golden_apple').setRarity(textformat('-1'))


// Use eventManager.listen and listen to the desired event.
/*eventManager.listen({ BlockEvent.BreakEvent event -> {
    event.setCanceled(true) // Many events can be canceled.
    event.player.sendMessage(new TextComponentString("${event.getState().getBlock().getLocalizedName()} Block was prevent from being broken"))
}})*/

// The outer parentheses and inner curly braces are optional.
eventManager.listen { EnderTeleportEvent event ->
    event.setAttackDamage 19.5f
}
