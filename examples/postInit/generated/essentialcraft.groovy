
// Auto generated groovyscript example file
// MODS_LOADED: essentialcraft

log 'mod \'essentialcraft\' detected, running script'

// Demon Trade:
// Adds an item that can be sold to Demons to obtain Ackronite. Note that each demon that spawns has a random item that it
// can accept, and will not accept any other item.

mods.essentialcraft.demon_trade.remove(entity('minecraft:enderman'))
mods.essentialcraft.demon_trade.remove(item('minecraft:nether_star'))
// mods.essentialcraft.demon_trade.removeAll()

mods.essentialcraft.demon_trade.add(entity('minecraft:chicken'))
mods.essentialcraft.demon_trade.add(item('minecraft:diamond'))

// Magician Table:
// A 5-slot processing machine using MRU. Can be upgraded with various plates to increase its speed.

mods.essentialcraft.magician_table.removeByOutput(item('essentialcraft:genitem'))
// mods.essentialcraft.magician_table.removeAll()

mods.essentialcraft.magician_table.recipeBuilder()
    .input(item('minecraft:diamond'), ore('ingotGold'), ore('ingotGold'), ore('stickWood'), ore('stickWood'))
    .output(item('minecraft:iron_ingot'))
    .mru(500)
    .register()


// Magmatic Smeltery:
// A machine used to quadruple ores using MRU and lava. Also adds the same recipes for Magmatic Furnace, which is used to
// double ores using MRU.

mods.essentialcraft.magmatic_smeltery.removeByInput(ore('oreIron'))
mods.essentialcraft.magmatic_smeltery.removeByInput('oreDiamond')
// mods.essentialcraft.magmatic_smeltery.removeAll()

mods.essentialcraft.magmatic_smeltery.recipeBuilder()
    .input('blockIron')
    .output('ingotGold')
    .factor(3)
    .color(0x0000ff)
    .register()


// Mithriline Furnace:
// Converts various items into other items using ESPE.

mods.essentialcraft.mithriline_furnace.removeByInput(ore('dustGlowstone'))
mods.essentialcraft.mithriline_furnace.removeByOutput(item('minecraft:emerald'))
// mods.essentialcraft.mithriline_furnace.removeAll()

mods.essentialcraft.mithriline_furnace.recipeBuilder()
    .input(item('minecraft:coal_block') * 3)
    .output(item('minecraft:diamond_block'))
    .espe(500)
    .register()


// Radiating Chamber:
// Combines two items together using MRU to obtain a third item. Can optionally require a specific range of MRU balance to
// execute the recipe.

mods.essentialcraft.radiating_chamber.removeByOutput(item('essentialcraft:genitem', 42))
// mods.essentialcraft.radiating_chamber.removeAll()

mods.essentialcraft.radiating_chamber.recipeBuilder()
    .input(item('minecraft:nether_star'), item('minecraft:stone'))
    .output(item('minecraft:beacon'))
    .time(100)
    .mruPerTick(10.0f)
    .upperBalance(1.5f)
    .lowerBalance(0.25f)
    .register()


// Wind Rune:
// Transforms various items using ESPE.

mods.essentialcraft.wind_rune.removeByInput(item('minecraft:diamond'))
mods.essentialcraft.wind_rune.removeByOutput(item('essentialcraft:air_potion'))
// mods.essentialcraft.wind_rune.removeAll()

mods.essentialcraft.wind_rune.recipeBuilder()
    .input(item('minecraft:gold_block'))
    .output(item('minecraft:diamond_block'))
    .espe(500)
    .register()


