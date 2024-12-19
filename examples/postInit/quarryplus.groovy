
// Auto generated groovyscript example file
// MODS_LOADED: quarryplus

log.info 'mod \'quarryplus\' detected, running script'

// Work bench:
// Workbench for crafting items using electricity.

mods.quarryplus.work_bench_plus.removeByOutput(item('quarryplus:quarry'))
// mods.quarryplus.work_bench_plus.removeAll()

mods.quarryplus.work_bench_plus.recipeBuilder()
    .output(item('minecraft:nether_star'))
    .input(item('minecraft:diamond'),item('minecraft:gold_ingot'))
    .energy(10000)
    .register()


