
// Auto generated groovyscript example file
// MODS_LOADED: armorplus

log.info 'mod \'armorplus\' detected, running script'

// Champion Bench:
// A normal crafting recipe, but with a 9x9 grid and in the Champion Bench.

// mods.armorplus.champion_bench.removeByOutput()
// mods.armorplus.champion_bench.removeAll()


mods.armorplus.champion_bench.shapedBuilder()
    .output(item('minecraft:stone') * 64)
    .matrix('DLLLLLDDD',
            '  DNIGIND',
            'DDDNIGIND',
            '  DLLLLLD')
    .key('D', item('minecraft:diamond'))
    .key('L', item('minecraft:redstone'))
    .key('N', item('minecraft:stone'))
    .key('I', item('minecraft:iron_ingot'))
    .key('G', item('minecraft:gold_ingot'))
    .register()


mods.armorplus.champion_bench.shapelessBuilder()
    .output(item('minecraft:clay') * 32)
    .input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'))
    .register()


// High-Tech Bench:
// A normal crafting recipe, but with a 5x5 grid and in the High-Tech Bench.

mods.armorplus.high_tech_bench.removeByOutput(item('armorplus:emerald_helmet'))
// mods.armorplus.high_tech_bench.removeAll()


mods.armorplus.high_tech_bench.shapedBuilder()
    .output(item('minecraft:diamond') * 32)
    .matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]])
    .register()


mods.armorplus.high_tech_bench.shapelessBuilder()
    .output(item('minecraft:clay') * 8)
    .input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))
    .register()


// Lava Infuser:
// Convert input itemstack to output itemstack over a second, with the ability to reward a configurable amount of
// experience based on the output itemstack. Consumes lava at a rate of 1 bucket per 10 seconds.

mods.armorplus.lava_infuser.removeByInput(item('armorplus:lava_crystal'))
mods.armorplus.lava_infuser.removeByOutput(item('armorplus:lava_infused_obsidian'))
// mods.armorplus.lava_infuser.removeAll()

mods.armorplus.lava_infuser.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 2)
    .register()

mods.armorplus.lava_infuser.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .experience(5.0d)
    .register()


// Ultimate Bench:
// A normal crafting recipe, but with a 7x7 grid and in the Ultimate Bench.

mods.armorplus.ultimate_bench.removeByOutput(item('armorplus:the_ultimate_helmet'))
// mods.armorplus.ultimate_bench.removeAll()


mods.armorplus.ultimate_bench.shapedBuilder()
    .output(item('minecraft:diamond'))
    .matrix('BXXXBX')
    .mirrored()
    .key('B', item('minecraft:stone'))
    .key('X', item('minecraft:gold_ingot'))
    .register()


mods.armorplus.ultimate_bench.shapelessBuilder()
    .output(item('minecraft:stone') * 64)
    .input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))
    .register()


// WorkBench:
// A normal crafting recipe, but with a 3x3 grid and in the WorkBench.

mods.armorplus.work_bench.removeByOutput(item('armorplus:the_gift_of_the_gods'))
// mods.armorplus.work_bench.removeAll()


mods.armorplus.work_bench.shapedBuilder()
    .output(item('minecraft:stone') * 8)
    .matrix('BXX')
    .mirrored()
    .key('B', item('minecraft:stone'))
    .key('X', item('minecraft:gold_ingot'))
    .register()


mods.armorplus.work_bench.shapelessBuilder()
    .output(item('minecraft:clay') * 8)
    .input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))
    .register()


