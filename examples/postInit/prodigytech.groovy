
// Auto generated groovyscript example file
// MODS_LOADED: prodigytech

println 'mod \'prodigytech\' detected, running script'

// groovyscript.wiki.prodigytech.heat_sawmill.title:
// groovyscript.wiki.prodigytech.heat_sawmill.description

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


// groovyscript.wiki.prodigytech.magnetic_reassembler.title:
// groovyscript.wiki.prodigytech.magnetic_reassembler.description

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


// groovyscript.wiki.prodigytech.ore_refinery.title:
// groovyscript.wiki.prodigytech.ore_refinery.description

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


// groovyscript.wiki.prodigytech.rotary_grinder.title:
// groovyscript.wiki.prodigytech.rotary_grinder.description

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


// groovyscript.wiki.prodigytech.solderer.title:
// groovyscript.wiki.prodigytech.solderer.description

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


// groovyscript.wiki.prodigytech.zorra_altar.title:
// groovyscript.wiki.prodigytech.zorra_altar.description

mods.prodigytech.zorra_altar.addEnchantment('sword', enchantment('minecraft:power'), 10)
mods.prodigytech.zorra_altar.addEnchantment('stick', enchantment('minecraft:knockback'), 20)
mods.prodigytech.zorra_altar.removeEnchantment('sword', enchantment('minecraft:sharpness'))

