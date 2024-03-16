
// Auto generated groovyscript example file
// MODS_LOADED: bloodmagic

println 'mod \'bloodmagic\' detected, running script'

// Alchemy Array:
// Converts two items into an output itemstack by using Arcane Ashes in-world. Has a configurable texture for the
// animation.

mods.bloodmagic.alchemy_array.removeByCatalyst(item('bloodmagic:slate:2'))
mods.bloodmagic.alchemy_array.removeByInput(item('bloodmagic:component:13'))
mods.bloodmagic.alchemy_array.removeByInputAndCatalyst(item('bloodmagic:component:7'), item('bloodmagic:slate:1'))
mods.bloodmagic.alchemy_array.removeByOutput(item('bloodmagic:sigil_void'))
// mods.bloodmagic.alchemy_array.removeAll()

mods.bloodmagic.alchemy_array.recipeBuilder()
    .input(item('minecraft:diamond'))
    .catalyst(item('bloodmagic:slate:1'))
    .output(item('minecraft:gold_ingot'))
    .texture('bloodmagic:textures/models/AlchemyArrays/LightSigil.png')
    .register()

mods.bloodmagic.alchemy_array.recipeBuilder()
    .input(item('minecraft:clay'))
    .catalyst(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .register()



// Alchemy Table:
// Converts up to 6 input items into an output itemstack, with configurable time, minimum tier of Blood Orb required, and
// Life Essence drained from the Orb network.

mods.bloodmagic.alchemy_table.removeByInput(item('minecraft:nether_wart'), item('minecraft:gunpowder'))
mods.bloodmagic.alchemy_table.removeByOutput(item('minecraft:sand'))
// mods.bloodmagic.alchemy_table.removeAll()

mods.bloodmagic.alchemy_table.recipeBuilder()
    .input(item('minecraft:diamond'), item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .ticks(100)
    .minimumTier(2)
    .syphon(500)
    .register()

mods.bloodmagic.alchemy_table.recipeBuilder()
    .input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('bloodmagic:slate'), item('bloodmagic:slate'))
    .output(item('minecraft:clay'))
    .time(2000)
    .tier(5)
    .drain(25000)
    .register()



// Blood Altar:
// Converts an input item into an output itemstack, draining life essence from the altar at a base rate and requiring at
// least a specific tier.

mods.bloodmagic.blood_altar.removeByInput(item('minecraft:ender_pearl'))
mods.bloodmagic.blood_altar.removeByOutput(item('bloodmagic:slate:4'))
// mods.bloodmagic.blood_altar.removeAll()

mods.bloodmagic.blood_altar.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .minimumTier(0)
    .drainRate(5)
    .syphon(10)
    .consumeRate(5)
    .register()

mods.bloodmagic.blood_altar.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .tier(3)
    .drainRate(100)
    .syphon(50000)
    .consumeRate(500)
    .register()



// Meteor:
// Throwing an input catalyst atop an activated Mark of the Falling Tower Ritual will spawn a meteor made of the given
// components, size, explosion strength, and Life Essence cost.

mods.bloodmagic.meteor.remove(item('minecraft:diamond_block'))
mods.bloodmagic.meteor.removeByCatalyst(item('minecraft:iron_block'))
mods.bloodmagic.meteor.removeByInput(item('minecraft:gold_block'))
// mods.bloodmagic.meteor.removeAll()

mods.bloodmagic.meteor.recipeBuilder()
    .catalyst(item('minecraft:gold_ingot'))
    .component(ore('oreIron'), 10)
    .component(ore('oreDiamond'), 10)
    .component(ore('stone'), 70)
    .radius(7)
    .explosionStrength(10)
    .cost(1000)
    .register()

mods.bloodmagic.meteor.recipeBuilder()
    .catalyst(item('minecraft:clay'))
    .component('blockClay', 10)
    .radius(20)
    .explosionStrength(20)
    .register()



// Sacrificial:
// How much Life Essence is gained when using the Sacrificial Dagger on a mob.

mods.bloodmagic.sacrificial.remove(entity('minecraft:villager'))
mods.bloodmagic.sacrificial.remove(resource('minecraft:villager'))
mods.bloodmagic.sacrificial.remove('minecraft:villager')
// mods.bloodmagic.sacrificial.removeAll()

mods.bloodmagic.sacrificial.recipeBuilder()
    .entity('minecraft:enderman')
    .value(1000)
    .register()



// Tartaric Forge:
// Converts up to 4 input items into an output itemstack, requiring a Tartaric gem with a minimum amount of souls, and
// consuming some.

mods.bloodmagic.tartaric_forge.removeByInput(item('minecraft:gunpowder'), item('minecraft:redstone'))
mods.bloodmagic.tartaric_forge.removeByInput(item('minecraft:cauldron'), item('minecraft:stone'), item('minecraft:dye:4'), item('minecraft:diamond'))
mods.bloodmagic.tartaric_forge.removeByOutput(item('bloodmagic:demon_crystal'))
// mods.bloodmagic.tartaric_forge.removeAll()

mods.bloodmagic.tartaric_forge.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .soulDrain(5)
    .minimumSouls(10)
    .register()

mods.bloodmagic.tartaric_forge.recipeBuilder()
    .input(item('minecraft:gold_ingot'), item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .drain(200)
    .minimumSouls(500)
    .register()



// Tranquility:
// Blocks in the area around the Tranquility Altar provide tranquility up to the Altar's cap, with reduced effect the more
// of a particular type of Tranquility is provided.

mods.bloodmagic.tranquility.remove(block('minecraft:dirt'), 'EARTHEN')
mods.bloodmagic.tranquility.remove(blockstate('minecraft:netherrack'), 'FIRE')
// mods.bloodmagic.tranquility.removeAll()

mods.bloodmagic.tranquility.recipeBuilder()
    .block(block('minecraft:obsidian'))
    .tranquility('LAVA')
    .value(10)
    .register()

mods.bloodmagic.tranquility.recipeBuilder()
    .block(block('minecraft:obsidian'))
    .tranquility('WATER')
    .value(10)
    .register()

mods.bloodmagic.tranquility.recipeBuilder()
    .blockstate(blockstate('minecraft:obsidian'))
    .tranquility('LAVA')
    .value(500)
    .register()



