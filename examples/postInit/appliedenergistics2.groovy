/*
NO_RUN
NO_RELOAD
MODS_LOADED: appliedenergistics2
SIDE: client
*/

// MODS_LOADED: appliedenergistics2
println 'mod \'appliedenergistics2\' detected, running script'

// Can be access via either `appliedenergistics2` or `ae2`

// Bracket Handlers

// Get the P2P Tunnel type from the enum. Case insensitive.
tunnel('me')
tunnel('ic2_power')
tunnel('fe_power')
tunnel('gteu_power')
tunnel('redstone')
tunnel('fluid')
tunnel('item')
tunnel('light')
tunnel('bundled_redstone')
tunnel('computer_message')


// Inscriber:
// Converts an item into another item, requiring either one or two additional items as either catalysts or ingredients.
mods.appliedenergistics2.inscriber.recipeBuilder()
    .input(ore('blockGlass'))
    .output(item('minecraft:diamond'))
    .top(item('minecraft:diamond')) // Optional, ItemStack. Either top or bottom must be defined.
    .bottom(item('minecraft:diamond')) // Optional, ItemStack. Either top or bottom must be defined.
    .inscribe() // Disable consumption of the top/bottom items.
    .register()

mods.appliedenergistics2.inscriber.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .top(item('minecraft:diamond'))
    .register()

mods.appliedenergistics2.inscriber.removeByOutput(item('appliedenergistics2:material:59'))
//mods.appliedenergistics2.inscriber.removeAll()


// Grinder:
// Converts an item into one item, with up to two additional items as chance byproducts after a number of turns.
mods.appliedenergistics2.grinder.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'), item('minecraft:gold_ingot'), item('minecraft:diamond')) // Up to 3 outputs can be defined. The first is 100%, the second is chance1, the third is chance2.
    .turns(1)
    .chance1(0.5) // Optional float, defaults to 1.0f.
    .chance2(0.3) // Optional float, defaults to 1.0f.
    .register()

mods.appliedenergistics2.grinder.recipeBuilder()
    .input(item('minecraft:stone'))
    .output(item('minecraft:clay') * 4)
    .turns(10)
    .register()

mods.appliedenergistics2.grinder.removeByInput(item('minecraft:gold_ingot'))
mods.appliedenergistics2.grinder.removeByOutput(item('minecraft:quartz'))
//mods.appliedenergistics2.grinder.removeAll()


// Spatial Storage Allowed Tile Entities:
// Either the class itself or its String name to add or remove from the Tile Entities allowed in Spatial Storage.
mods.appliedenergistics2.spatial.add('net.minecraft.tileentity.TileEntityStructure') // Adds the Structure Block to the allowed Tile Entities

mods.appliedenergistics2.spatial.remove('net.minecraft.tileentity.TileEntityChest') // Removes the vanilla Chest from the allowed Tile Entities
//mods.appliedenergistics2.spatial.removeAll()


// Cannon Ammo: (alias: Cannon)
// Item and weight, where weight is a factor in how much damage is dealt.
mods.appliedenergistics2.cannonammo.add(item('minecraft:clay'), 10000)

mods.appliedenergistics2.cannonammo.remove(item('minecraft:gold_nugget'))
//mods.appliedenergistics2.cannonammo.removeAll()


// P2P Attunement:
// Item and tunnel type, modid and tunnel type, or capability and tunnel type to add to allowed items to convert P2P tunnels.
mods.appliedenergistics2.attunement.removeByTunnel(tunnel('item')) // Remove all ways to create the given tunnel

mods.appliedenergistics2.attunement.add(item('minecraft:clay'), tunnel('item')) // item + tunnel
mods.appliedenergistics2.attunement.remove(item('minecraft:lever'), tunnel('redstone')) // item + tunnel

//mods.appliedenergistics2.attunement.remove('thermaldynamics', tunnel('fe_power')) // modid + tunnel
//mods.appliedenergistics2.attunement.add('thermaldynamics', tunnel('redstone')) // modid + tunnel

// Must be imported via `appeng.capabilities.Capabilities`
//mods.appliedenergistics2.attunement.remove(Capabilities.FORGE_ENERGY, tunnel('fe_power')) // capability + tunnel
//mods.appliedenergistics2.attunement.add(Capabilities.FORGE_ENERGY, tunnel('item')) // capability + tunnel

//mods.appliedenergistics2.attunement.removeAll()
