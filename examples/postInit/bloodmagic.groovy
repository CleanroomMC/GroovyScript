
// MODS_LOADED: bloodmagic
println 'mod \'bloodmagic\' detected, running script'


// Blood Altar:
// Converts an input item into an output itemstack, draining life essence from the altar at a base rate and requiring at least a specific tier.
mods.bloodmagic.bloodaltar.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .tier(0) // Optional int, required tier of the altar. Maximum of either 5 or 6 depending on the config general/enableTierSixEvenThoughThereIsNoContent. (Default 0)
    .drainRate(5)
    .syphon(10)
    .consumeRate(5)
    .register()

mods.bloodmagic.bloodaltar.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .minimumTier(3)
    .drainRate(100)
    .syphon(50000)
    .consumeRate(500)
    .register()

mods.bloodmagic.bloodaltar.removeByInput(item('minecraft:ender_pearl'))
mods.bloodmagic.bloodaltar.removeByOutput(item('bloodmagic:slate:4'))
//mods.bloodmagic.bloodaltar.removeAll()


// Alchemy Array:
// Converts two items into an output itemstack by using Arcane Ashes in-world. Has a configurable texture for the animation.
mods.bloodmagic.alchemyarray.recipeBuilder()
    .input(item('minecraft:diamond'))
    .catalyst(item('bloodmagic:slate:1'))
    .output(item('minecraft:gold_ingot'))
    .texture('bloodmagic:textures/models/AlchemyArrays/LightSigil.png') // Optional String/ResourceLocation of a texture to use as the animation. (Default 'bloodmagic:textures/models/AlchemyArrays/WIPArray.png')
    .register()

mods.bloodmagic.alchemyarray.recipeBuilder()
    .input(item('minecraft:clay'))
    .catalyst(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .register()

mods.bloodmagic.alchemyarray.removeByInput(item('bloodmagic:component:13'))
mods.bloodmagic.alchemyarray.removeByCatalyst(item('bloodmagic:slate:2'))
mods.bloodmagic.alchemyarray.removeByInputAndCatalyst(item('bloodmagic:component:7'), item('bloodmagic:slate:1'))
mods.bloodmagic.alchemyarray.removeByOutput(item('bloodmagic:sigil_void'))
//mods.bloodmagic.alchemyarray.removeAll()


// Tartaric Forge:
// Converts up to 4 input items into an output itemstack, requiring a Tartaric gem with a minimum amount of souls, and consuming some.
mods.bloodmagic.tartaricforge.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .drain(5)
    .minimumSouls(10)
    .register()

mods.bloodmagic.tartaricforge.recipeBuilder()
    .input(item('minecraft:gold_ingot'), item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .soulDrain(200) // Alias to drain
    .minimumSouls(500)
    .register()

mods.bloodmagic.tartaricforge.removeByInput(item('minecraft:cauldron'), item('minecraft:stone'), item('minecraft:dye:4'), item('minecraft:diamond'))
mods.bloodmagic.tartaricforge.removeByInput(item('minecraft:gunpowder'), item('minecraft:redstone'))
mods.bloodmagic.tartaricforge.removeByOutput(item('bloodmagic:demon_crystal'))
//mods.bloodmagic.tartaricforge.removeAll()


// Alchemy Table:
// Converts up to 6 input items into an output itemstack, with configurable time, minimum tier of Blood Orb required, and Life Essence drained from the Orb network.
mods.bloodmagic.alchemytable.recipeBuilder()
    .input(item('minecraft:diamond'), item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .ticks(100)
    .minimumTier(2) // Optional int, tier of the Blood Orb inside the table. Maximum of either 5 or 6 depending on the config general/enableTierSixEvenThoughThereIsNoContent. (Default 0)
    .syphon(500)
    .register()

mods.bloodmagic.alchemytable.recipeBuilder()
    .input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('bloodmagic:slate'), item('bloodmagic:slate'))
    .output(item('minecraft:clay'))
    .time(2000) // Alias to ticks
    .tier(5) // Alias to minimumTier
    .drain(25000) // Alias to syphon
    .register()

mods.bloodmagic.alchemytable.removeByInput(item('minecraft:nether_wart'), item('minecraft:gunpowder'))
mods.bloodmagic.alchemytable.removeByOutput(item('minecraft:sand'))
//mods.bloodmagic.alchemytable.removeAll()


// Tranquility:
// Blocks in the area around the Tranquility Altar provide tranquility up to the Altar's cap, with reduced effect the more of a particular type of Tranquility is provided.
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

mods.bloodmagic.tranquility.remove(blockstate('minecraft:netherrack'), 'FIRE')
mods.bloodmagic.tranquility.remove(block('minecraft:dirt'), 'EARTHEN')
//mods.bloodmagic.tranquility.removeAll()


// Sacrificial:
// How much Life Essence is gained when using the Sacrificial Dagger on a mob.
mods.bloodmagic.sacrificial.recipeBuilder()
    .entity('minecraft:enderman')
    .value(1000)
    .register()

mods.bloodmagic.sacrificial.remove('minecraft:villager')
//mods.bloodmagic.sacrificial.removeAll()


// Meteor:
// Throwing an input catalyst atop an activated Mark of the Falling Tower Ritual will spawn a meteor made of the given components, size, explosion strength, and Life Essence cost.
mods.bloodmagic.meteor.recipeBuilder()
    .catalyst(item('minecraft:gold_ingot'))
    .component(ore('oreIron'), 10)
    .component(ore('oreDiamond'), 10)
    .component(ore('stone'), 70)
    .radius(7)
    .explosionStrength(10)
    .cost(1000) // Optional int, Life Essence cost of the ritual. (Default 1000000)
    .register()

mods.bloodmagic.meteor.recipeBuilder()
    .catalyst(item('minecraft:clay'))
    .component('blockClay', 10)
    .radius(20)
    .explosionStrength(20)
    .register()

mods.bloodmagic.meteor.remove(item('minecraft:diamond_block'))
mods.bloodmagic.meteor.removeByInput(item('minecraft:gold_block'))
mods.bloodmagic.meteor.removeByCatalyst(item('minecraft:iron_block'))
//mods.bloodmagic.meteor.removeAll()
