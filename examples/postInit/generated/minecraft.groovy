
// Auto generated groovyscript example file

log 'running Vanilla Minecraft example'

// Custom Commands:
// Create custom commands, either generally or specifically for the client.

minecraft.command.registerCommand('groovy_test', { server, sender, args -> sender.sendMessage('Hello from GroovyScript')})

// Crafting Table:
// A normal crafting recipe that takes place in the Vanilla Crafting Table, converting up to 9 items in a shapeless or
// specific shaped arrangement into an output itemstack.

crafting.remove(resource('minecraft:stonebrick'))
crafting.remove('minecraft:mossy_stonebrick')
crafting.removeByOutput(item('minecraft:gold_ingot'))
// crafting.removeAll()

crafting.shapedBuilder()
    .output(item('minecraft:nether_star'))
    .row('TXT')
    .row('X X')
    .row('!X!')
    .key('T', item('minecraft:tnt'))
    .key('X', item('minecraft:clay').reuse())
    .key('!', item('minecraft:tnt').transform({ _ -> item('minecraft:diamond') }))
    .register()

crafting.shapedBuilder()
    .output(item('minecraft:clay_ball') * 3)
    .shape('S S',
           ' G ',
           'SWS')
    .key([S: ore('netherStar').reuse(), G: ore('ingotGold'), W: fluid('water') * 1000])
    .register()

crafting.shapedBuilder()
    .name('nether_star_duplication_with_tnt')
    .output(item('minecraft:nether_star'))
    .row('!!!')
    .row('!S!')
    .row('!!!')
    .key([S: ore('netherStar').reuse(), '!': item('minecraft:tnt').transform(item('minecraft:diamond'))])
    .register()

crafting.shapedBuilder()
    .output(item('minecraft:clay'))
    .row(' B')
    .key('B', item('minecraft:glass_bottle'))
    .register()

crafting.shapedBuilder()
    .output(item('minecraft:clay'))
    .row('   ')
    .row(' 0 ')
    .row('   ')
    .key('0', item('minecraft:diamond_sword').withNbt([display:[Name:'Sword with Specific NBT data']]))
    .register()

crafting.shapedBuilder()
    .output(item('minecraft:gold_block'))
    .shape([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
           [null, null, null],
           [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
    .register()

crafting.shapedBuilder()
    .name('gold_v_to_clay')
    .output(item('minecraft:clay'))
    .shape([[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],
           [null,item('minecraft:stone_pickaxe').transformDamage(2).whenAnyDamage(),null]])
    .register()

crafting.shapedBuilder()
    .name(resource('example:resource_location'))
    .output(item('minecraft:clay'))
    .shape([[item('minecraft:cobblestone')],
           [item('minecraft:nether_star')],
           [item('minecraft:cobblestone')]])
    .register()

crafting.shapedBuilder()
    .output(item('minecraft:chest'))
    .shape([[ore('logWood'),ore('logWood'),ore('logWood')],
           [ore('logWood'),null,ore('logWood')],
           [ore('logWood'),ore('logWood'),ore('logWood')]])
    .replace()
    .register()

crafting.shapedBuilder()
    .name('gold_to_diamonds')
    .output(item('minecraft:diamond') * 8)
    .shape([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],
           [item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],
           [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
    .replaceByName()
    .register()

crafting.shapedBuilder()
    .name(resource('minecraft:sea_lantern'))
    .output(item('minecraft:clay'))
    .shape([[item('minecraft:glowstone')],
           [item('minecraft:glowstone')],
           [item('minecraft:glowstone')]])
    .replaceByName()
    .register()

crafting.shapelessBuilder()
    .output(item('minecraft:clay'))
    .input([item('minecraft:cobblestone'),item('minecraft:nether_star'),item('minecraft:gold_ingot')])
    .register()

crafting.shapelessBuilder()
    .name('precious_to_clay')
    .output(item('minecraft:clay'))
    .input([item('minecraft:diamond'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')])
    .register()

crafting.shapelessBuilder()
    .name(resource('example:resource_location2'))
    .output(item('minecraft:clay'))
    .input([item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
    .register()

crafting.shapelessBuilder()
    .output(item('minecraft:ender_eye'))
    .input([item('minecraft:ender_pearl'),item('minecraft:nether_star')])
    .replace()
    .register()

crafting.shapelessBuilder()
    .name('minecraft:pink_dye_from_pink_tulp')
    .output(item('minecraft:clay'))
    .input([item('minecraft:nether_star')])
    .replaceByName()
    .register()

crafting.shapelessBuilder()
    .name(resource('minecraft:pink_dye_from_peony'))
    .output(item('minecraft:clay'))
    .input([item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
    .replaceByName()
    .register()


// crafting.addShaped(item('minecraft:gold_block'), [[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[null, null, null],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
// crafting.addShaped(resource('example:resource_location'), item('minecraft:clay'), [[item('minecraft:cobblestone')],[item('minecraft:nether_star')],[item('minecraft:cobblestone')]])
// crafting.addShaped('gold_v_to_clay', item('minecraft:clay'), [[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[null,item('minecraft:gold_ingot'),null]])
// crafting.addShapeless(item('minecraft:clay'), [item('minecraft:cobblestone'),item('minecraft:nether_star'),item('minecraft:gold_ingot')])
// crafting.addShapeless(resource('example:resource_location2'), item('minecraft:clay'), [item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
// crafting.addShapeless('precious_to_clay', item('minecraft:clay'), [item('minecraft:diamond'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')])
// crafting.replaceShaped(item('minecraft:chest'), [[ore('logWood'),ore('logWood'),ore('logWood')],[ore('logWood'),null,ore('logWood')],[ore('logWood'),ore('logWood'),ore('logWood')]])
// crafting.replaceShaped(resource('minecraft:sea_lantern'), item('minecraft:clay'), [[item('minecraft:glowstone')],[item('minecraft:glowstone')],[item('minecraft:glowstone')]])
// crafting.replaceShaped('gold_to_diamonds', item('minecraft:diamond') * 8, [[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),null,item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]])
// crafting.replaceShapeless(item('minecraft:ender_eye'), [item('minecraft:ender_pearl'),item('minecraft:nether_star')])
// crafting.replaceShapeless(resource('minecraft:pink_dye_from_peony'), item('minecraft:clay'), [item('minecraft:cobblestone'), item('minecraft:gold_ingot')])
// crafting.replaceShapeless('minecraft:pink_dye_from_pink_tulp', item('minecraft:clay'), [item('minecraft:nether_star')])

// Furnace:
// Converts an input item into an output itemstack after a configurable amount of time, with the ability to give experience
// and using fuel to run. Can also convert the item in the fuel slot.

furnace.removeByInput(item('minecraft:clay:*'))
furnace.removeByOutput(item('minecraft:brick'))
furnace.removeFuelConversionBySmeltedStack(item('minecraft:sponge', 1))
// furnace.removeAll()
// furnace.removeAllFuelConversions()

furnace.recipeBuilder()
    .input(ore('ingotGold'))
    .output(item('minecraft:nether_star'))
    .exp(0.5)
    .register()


// furnace.add(ore('ingotIron'), item('minecraft:diamond'))
furnace.add(item('minecraft:nether_star'), item('minecraft:clay') * 64, 13)
furnace.add(item('minecraft:diamond'), item('minecraft:clay'), 2, 50)
furnace.addFuelConversion(item('minecraft:diamond'), item('minecraft:bucket').transform(item('minecraft:lava_bucket')))

// Default GameRules:
// Create or assign a default value to GameRules.

minecraft.game_rule.setWarnNewGameRule(true)

minecraft.game_rule.add(['mobGriefing': 'false', 'keepInventory': 'true'])
minecraft.game_rule.add('doDaylightCycle', 'false')

// Ore Dictionary:
// Manipulate the Ore Dictionary and what itemstacks are part of what oredicts.

// ore_dict.clear('plankWood')
ore_dict.remove('netherStar', item('minecraft:nether_star'))
// ore_dict.removeAll('ingotIron')
// ore_dict.removeAll()

ore_dict.add('ingotGold', item('minecraft:nether_star'))
ore_dict.add('netherStar', item('minecraft:gold_ingot'))

ore_dict.getOres(~/.*/)
ore_dict.getOres(~/.*Gold/)
ore_dict.getOres(~/.*or.*/)
ore_dict.getOres('ingot*')

// Starting Inventory:
// Sets the starting inventory of the player, including armor slots and offhand.

minecraft.player.setReplaceDefaultInventory(true)
// minecraft.player.setTestStartingItems(true)

minecraft.player.addStartingItem(item('minecraft:diamond'))
minecraft.player.addStartingItem(item('minecraft:clay_ball'))
minecraft.player.addStartingItem(item('minecraft:gold_ingot'))
minecraft.player.addStartingItem(item('minecraft:nether_star'))
minecraft.player.addStartingItem(item('minecraft:water_bucket'))
minecraft.player.setStartingItems(true, item('minecraft:clay').withNbt([display:[Name:'Hotbar']]), null, null, null, null, null, null, null, null, item('minecraft:clay').withNbt([display:[Name:'Top row of inventory']]), null, null, null, null, null, null, null, null, item('minecraft:clay').withNbt([display:[Name:'Middle row of inventory']]), null, null, null, null, null, null, null, null, item('minecraft:clay').withNbt([display:[Name:'Bottom row of inventory']]), null, null, null, null, null, null, null, null, item('minecraft:diamond_boots'), item('minecraft:diamond_leggings'), item('minecraft:diamond_chestplate'), item('minecraft:diamond_helmet'), item('minecraft:clay').withNbt([display:[Name:'Offhand']]))

// Rarity:
// Control the rarity of the item, which typically is the name color, to any standard Rarity or any TextFormatting code.

minecraft.rarity.set(textformat('AQUA'), item('minecraft:diamond'))
minecraft.rarity.set(textformat('RESET'), item('minecraft:enchanted_book'))

