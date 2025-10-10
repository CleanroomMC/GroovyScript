
// Auto generated groovyscript example file
// MODS_LOADED: prodigytech

log 'mod \'prodigytech\' detected, running script'

// Atomic Reshaper:
// Uses Hot Air and Primordium to convert items. Can have a weighted random based output.

mods.prodigytech.atomic_reshaper.removeByInput(ore('paper'))
// mods.prodigytech.atomic_reshaper.removeAll()

mods.prodigytech.atomic_reshaper.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:emerald_block'))
    .primordium(10)
    .time(50)
    .register()

mods.prodigytech.atomic_reshaper.recipeBuilder()
    .input(item('minecraft:gold_block'))
    .output(item('minecraft:diamond_block'), 10)
    .output(item('minecraft:carrot'), 3)
    .primordium(7)
    .register()


// Explosion Furnace:
// Uses an explosive, a dampener, and an optional reagent to convert items. The power value of all recipes, all explosives,
// and all dampeners should be close to avoid an efficiency loss.

mods.prodigytech.explosion_furnace.removeByOutput(item('prodigytech:ferramic_ingot'))
// mods.prodigytech.explosion_furnace.removeAll()

mods.prodigytech.explosion_furnace.recipeBuilder()
    .input(ore('ingotGold'), item('minecraft:diamond'))
    .craftPerReagent(8)
    .power(160)
    .output(item('minecraft:emerald_block'))
    .register()

mods.prodigytech.explosion_furnace.recipeBuilder()
    .input(item('minecraft:stone'))
    .power(160)
    .output(item('minecraft:glowstone'))
    .register()


// Explosion Furnace Additives:
// Turn an item into an explosive or into a dampener when inserted into the Explosion Furnace.

mods.prodigytech.explosion_furnace_additives.removeDampener(ore('dustAsh'))
mods.prodigytech.explosion_furnace_additives.removeExplosive(ore('gunpowder'))
// mods.prodigytech.explosion_furnace_additives.removeAllDampeners()
// mods.prodigytech.explosion_furnace_additives.removeAllExplosives()

mods.prodigytech.explosion_furnace_additives.addDampener(item('minecraft:stone'), 50)
mods.prodigytech.explosion_furnace_additives.addExplosive(item('minecraft:cobblestone'), 50)

// Heat Sawmill:
// Wood processing machine with 1 input, 2 outputs and an optional chance for the 2nd output.

mods.prodigytech.heat_sawmill.removeByInput(ore('plankWood'))
// mods.prodigytech.heat_sawmill.removeAll()

mods.prodigytech.heat_sawmill.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .time(50)
    .register()

mods.prodigytech.heat_sawmill.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:coal'))
    .register()

mods.prodigytech.heat_sawmill.recipeBuilder()
    .input(item('minecraft:iron_block'))
    .output(item('minecraft:emerald'), item('minecraft:clay'))
    .register()

mods.prodigytech.heat_sawmill.recipeBuilder()
    .input(item('minecraft:gold_block'))
    .output(item('minecraft:emerald'), item('minecraft:nether_star'))
    .secondaryChance(0.25)
    .time(50)
    .register()


// Magnetic Reassembler:
// A simple 1 to 1 processing machine for dusts.

mods.prodigytech.magnetic_reassembler.removeByInput(item('minecraft:gravel'))
// mods.prodigytech.magnetic_reassembler.removeAll()

mods.prodigytech.magnetic_reassembler.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .time(50)
    .register()

mods.prodigytech.magnetic_reassembler.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:coal'))
    .register()


// Ore Refinery:
// Ore processing machine with 1 input, 2 outputs and an optional chance for the 2nd output.

mods.prodigytech.ore_refinery.removeByInput(ore('oreLapis'))
// mods.prodigytech.ore_refinery.removeAll()

mods.prodigytech.ore_refinery.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .time(50)
    .register()

mods.prodigytech.ore_refinery.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:coal'))
    .register()

mods.prodigytech.ore_refinery.recipeBuilder()
    .input(item('minecraft:iron_block'))
    .output(item('minecraft:emerald'), item('minecraft:clay'))
    .register()

mods.prodigytech.ore_refinery.recipeBuilder()
    .input(item('minecraft:gold_block'))
    .output(item('minecraft:emerald'), item('minecraft:nether_star'))
    .secondaryChance(0.25)
    .time(50)
    .register()


// Primordialis Reactor:
// Turns organic matter into Primordium.

mods.prodigytech.primordialis_reactor.remove(ore('sugarcane'))
// mods.prodigytech.primordialis_reactor.removeAll()

mods.prodigytech.primordialis_reactor.add(item('minecraft:diamond'))

// Rotary Grinder:
// A simple 1 to 1 processing machine making dusts.

mods.prodigytech.rotary_grinder.removeByInput(item('minecraft:gravel'))
// mods.prodigytech.rotary_grinder.removeAll()

mods.prodigytech.rotary_grinder.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .time(50)
    .register()

mods.prodigytech.rotary_grinder.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:coal'))
    .register()


// Solderer:
// Performs recipes using Gold Dust, has a recipe catalyst, and uses up Circuit Boards and an optional extra input for each
// recipe.

mods.prodigytech.solderer.removeByAdditive(item('minecraft:iron_ingot'))
mods.prodigytech.solderer.removeByOutput(item('prodigytech:circuit_refined'))
mods.prodigytech.solderer.removeByPattern(item('prodigytech:pattern_circuit_refined'))
// mods.prodigytech.solderer.removeAll()
// mods.prodigytech.solderer.removeWithoutAdditive()

mods.prodigytech.solderer.recipeBuilder()
    .pattern(item('minecraft:clay'))
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .gold(5)
    .time(100)
    .register()

mods.prodigytech.solderer.recipeBuilder()
    .pattern(item('minecraft:coal_block'))
    .output(item('minecraft:nether_star'))
    .gold(75)
    .register()


// Zorra Altar:
// Allows over-enchanting Zorrasteel equipment.

mods.prodigytech.zorra_altar.addEnchantment('sword', enchantment('minecraft:power'), 10)
mods.prodigytech.zorra_altar.addEnchantment('stick', enchantment('minecraft:knockback'), 20)
mods.prodigytech.zorra_altar.removeEnchantment('sword', enchantment('minecraft:sharpness'))

