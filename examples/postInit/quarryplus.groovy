
// Auto generated groovyscript example file
// MODS_LOADED: quarryplus

log.info 'mod \'quarryplus\' detected, running script'

// Workbench Plus:
// Converts up to 27 itemstacks into an output itemstack at the cost of power.

mods.quarryplus.workbench_plus.removeByOutput(item('quarryplus:quarry'))
// mods.quarryplus.workbench_plus.removeAll()

mods.quarryplus.workbench_plus.recipeBuilder()
    .output(item('minecraft:nether_star'))
    .input(item('minecraft:diamond'),item('minecraft:gold_ingot'))
    .energy(10000)
    .register()


