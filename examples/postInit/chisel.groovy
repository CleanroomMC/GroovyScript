
// Auto generated groovyscript example file
// MODS_LOADED: chisel

println 'mod \'chisel\' detected, running script'

// Carving:
// Sets a group of items any item can be converted between freely, in world and in a GUI

// mods.chisel.carving.removeAll()
mods.chisel.carving.removeGroup('blockDiamond')
mods.chisel.carving.removeVariation('antiblock', item('chisel:antiblock:3'))
mods.chisel.carving.removeVariation('antiblock', item('chisel:antiblock:15'))

mods.chisel.carving.addGroup('demo')
mods.chisel.carving.addVariation('demo', item('chisel:antiblock:3'))
mods.chisel.carving.addVariation('demo', item('minecraft:sea_lantern'))
mods.chisel.carving.addVariation('demo', item('minecraft:diamond_block'))

mods.chisel.carving.setSound('demo', sound('minecraft:block.glass.break'))

