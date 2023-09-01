
// MODS_LOADED: chisel
println 'mod \'chisel\' detected, running script'

// Carving
mods.chisel.carving.addGroup('demo')
mods.chisel.carving.removeGroup('blockDiamond')

mods.chisel.carving.removeVariation('antiblock', item('chisel:antiblock:3'))
mods.chisel.carving.removeVariation('antiblock', item('chisel:antiblock:15'))
mods.chisel.carving.addVariation('demo', item('minecraft:diamond_block'))
mods.chisel.carving.addVariation('demo', item('chisel:antiblock:3'))
mods.chisel.carving.addVariation('demo', item('minecraft:sea_lantern'))

// Set the sound of the Variation
mods.chisel.carving.setSound('demo', sound('block.glass.break'))

// You cannot addVariation/removeVariation to chisel groups based on the oredict, you have to modify the oredict directly.
oredict.add('blockCoal', item('chisel:antiblock:15'))
oredict.remove('blockCoal', item('minecraft:coal_block'))

// Can also run multiple operations on a group, creating the group if it didnt exist prior:
mods.chisel.carving.carvingGroup('valentines')
    .remove(item('chisel:valentines'), item('chisel:valentines:1'), item('chisel:valentines:2'), item('chisel:valentines:3'))
    .add(item('minecraft:grass'), item('minecraft:diamond_ore'))
    .sound(sound('block.anvil.destroy'))
