
// Auto generated groovyscript example file
// MODS_LOADED: appliedenergistics2

import appeng.capabilities.Capabilities

println 'mod \'appliedenergistics2\' detected, running script'

// P2P Attunement:
// Controls using specific items, any items from a mod, or any items with a Capability to convert a P2P into a specific
// tunnel type.

mods.appliedenergistics2.attunement.remove(Capabilities.FORGE_ENERGY, tunnel('fe_power'))
mods.appliedenergistics2.attunement.remove(item('minecraft:lever'), tunnel('redstone'))
mods.appliedenergistics2.attunement.remove('thermaldynamics', tunnel('fe_power'))
mods.appliedenergistics2.attunement.removeByTunnel(tunnel('item'))
// mods.appliedenergistics2.attunement.removeAll()

mods.appliedenergistics2.attunement.add(Capabilities.FORGE_ENERGY, tunnel('item'))
mods.appliedenergistics2.attunement.add(item('minecraft:clay'), tunnel('item'))
mods.appliedenergistics2.attunement.add('thermaldynamics', tunnel('redstone'))

// Cannon Ammo:
// Item and weight, where weight is a factor in how much damage is dealt.

mods.appliedenergistics2.cannon_ammo.remove(item('minecraft:gold_nugget'))
// mods.appliedenergistics2.cannon_ammo.removeAll()

mods.appliedenergistics2.cannon_ammo.add(item('minecraft:clay'), 10000)

// Grinder:
// Converts an item into one item, with up to two additional items as chance byproducts after a number of turns.

mods.appliedenergistics2.grinder.removeByInput(item('minecraft:gold_ingot'))
mods.appliedenergistics2.grinder.removeByOutput(item('minecraft:quartz'))
// mods.appliedenergistics2.grinder.removeAll()

mods.appliedenergistics2.grinder.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'), item('minecraft:gold_ingot'), item('minecraft:diamond'))
    .turns(1)
    .chance1(0.5)
    .chance2(0.3)
    .register()

mods.appliedenergistics2.grinder.recipeBuilder()
    .input(item('minecraft:stone'))
    .output(item('minecraft:clay') * 4)
    .turns(10)
    .register()


// Inscriber:
// Converts an item into another item, requiring either one or two additional items as either catalysts or ingredients.

mods.appliedenergistics2.inscriber.removeByOutput(item('appliedenergistics2:material:59'))
// mods.appliedenergistics2.inscriber.removeAll()

mods.appliedenergistics2.inscriber.recipeBuilder()
    .input(ore('blockGlass'))
    .output(item('minecraft:diamond'))
    .top(item('minecraft:diamond'))
    .bottom(item('minecraft:diamond'))
    .inscribe()
    .register()

mods.appliedenergistics2.inscriber.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .top(item('minecraft:diamond'))
    .register()


// Spatial Storage Allowed Tile Entities:
// Either the class itself or its String name to add or remove from the Tile Entities allowed in Spatial Storage.

mods.appliedenergistics2.spatial.remove('net.minecraft.tileentity.TileEntityChest')
// mods.appliedenergistics2.spatial.removeAll()

mods.appliedenergistics2.spatial.add('net.minecraft.tileentity.TileEntityStructure')

