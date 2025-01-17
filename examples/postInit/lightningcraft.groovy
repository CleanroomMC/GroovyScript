
// Auto generated groovyscript example file
// MODS_LOADED: lightningcraft

log.info 'mod \'lightningcraft\' detected, running script'

// Lightning Crusher:
// Consumes LE to convert 1 input itemstack into an output itemstack.

mods.lightningcraft.crusher.removeByInput(item('minecraft:saddle'))
mods.lightningcraft.crusher.removeByOutput(item('minecraft:redstone'))
// mods.lightningcraft.crusher.removeAll()

mods.lightningcraft.crusher.recipeBuilder()
    .input(item('minecraft:diamond_block'))
    .output(item('minecraft:nether_star'))
    .register()


// Lightning Infusion Table:
// Consumes LE to convert up to 5 input itemstacks into an output itemstack.

mods.lightningcraft.infusion.removeByOutput(item('minecraft:diamond'))
// mods.lightningcraft.infusion.removeAll()

mods.lightningcraft.infusion.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:iron_ingot'), item('minecraft:iron_ingot'))
    .output(item('minecraft:nether_star'))
    .le(500)
    .register()

mods.lightningcraft.infusion.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:potion').withNbt(['Potion': 'minecraft:leaping']))
    .output(item('minecraft:diamond_block'))
    .le(200)
    .register()


