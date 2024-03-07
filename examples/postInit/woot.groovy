
// Auto generated groovyscript example file
// MODS_LOADED: woot

import ipsis.woot.util.WootMobName

println 'mod \'woot\' detected, running script'

// Drops:
// Controls extra drops given by mobs. Chance and Size are both arrays 4 long, containing the values for levels 0/1/2/3
// levels of Looting.

mods.woot.drops.removeByEntity(entity('minecraft:ender_dragon'))
mods.woot.drops.removeByEntity('minecraft:ender_dragon')
mods.woot.drops.removeByEntity('minecraft:ender_dragon', '')
mods.woot.drops.removeByEntity(new WootMobName('minecraft:ender_dragon'))
mods.woot.drops.removeByOutput(item('minecraft:dragon_breath'))
// mods.woot.drops.removeAll()

mods.woot.drops.recipeBuilder()
    .name('minecraft:zombie')
    .output(item('minecraft:clay'))
    .chance(10, 30, 60, 100)
    .size(5, 10, 20, 50)
    .register()



// Mob Config:
// Control the default values or mob-specific values for a large number of effects, a full list can be found at
// `ipsis.woot.configuration.EnumConfigKey`. A full list can be viewed on
// [Github](https://github.com/Ipsis/Woot/blob/55e88f5a15d66cc987e676d665d20f4afbe008b8/src/main/java/ipsis/woot/configuration/EnumConfigKey.java#L14)

mods.woot.mob_config.remove('minecraft:wither_skeleton', 'spawn_units')
mods.woot.mob_config.remove('minecraft:wither')
// mods.woot.mob_config.removeAll()

mods.woot.mob_config.add('spawn_ticks', 100)
mods.woot.mob_config.add('minecraft:zombie', 'spawn_ticks', 1)

// Policy:
// Controls what entities can be farmed for what items via an entity blacklist, mod blacklist, item output blacklist, item
// output mod blacklist, and a mob whitelist.

mods.woot.policy.removeFromEntityBlacklist('twilightforest:naga')
mods.woot.policy.removeFromEntityModBlacklist('botania')
// mods.woot.policy.removeFromEntityWhitelist('minecraft:wither_skeleton')
// mods.woot.policy.removeFromGenerateOnlyList('minecraft:wither_skeleton')
// mods.woot.policy.removeFromItemBlacklist(item('minecraft:sugar'))
mods.woot.policy.removeFromItemModBlacklist('minecraft')
// mods.woot.policy.removeAllFromEntityBlacklist()
// mods.woot.policy.removeAllFromEntityModBlacklist()
// mods.woot.policy.removeAllFromEntityWhitelist()
// mods.woot.policy.removeAllFromGenerateOnlyList()
// mods.woot.policy.removeAllFromItemBlacklist()
// mods.woot.policy.removeAllFromItemModBlacklist()
// mods.woot.policy.removeAll()

mods.woot.policy.addToEntityBlacklist('minecraft:witch')
// mods.woot.policy.addToEntityModBlacklist('minecraft')
// mods.woot.policy.addToEntityWhitelist('minecraft:zombie')
mods.woot.policy.addToGenerateOnlyList('minecraft:skeleton')
mods.woot.policy.addToItemBlacklist(item('minecraft:gunpowder'))
mods.woot.policy.addToItemModBlacklist('woot')

// Spawning:
// Controls item/fluid costs of a given mob or the default cost.

mods.woot.spawning.remove(new WootMobName('minecraft:ender_dragon'))
mods.woot.spawning.removeByEntity(entity('minecraft:ender_dragon'))
mods.woot.spawning.removeByEntity('minecraft:ender_dragon')
mods.woot.spawning.removeByEntity('minecraft:ender_dragon', '')
mods.woot.spawning.removeByEntity(new WootMobName('minecraft:ender_dragon'))
// mods.woot.spawning.removeAll()

mods.woot.spawning.recipeBuilder()
    .name('minecraft:zombie')
    .input(item('minecraft:clay'))
    .fluidInput(fluid('water'))
    .register()

mods.woot.spawning.recipeBuilder()
    .defaultSpawnRecipe(true)
    .input(item('minecraft:gold_ingot'), item('minecraft:diamond'))
    .register()



// Stygian Iron Anvil:
// Has a catalyst (which may or may not be consumed) placed on the anvil, with the input items thrown atop the base.

mods.woot.stygian_iron_anvil.removeByBase(item('minecraft:iron_bars'))
mods.woot.stygian_iron_anvil.removeByOutput(item('woot:stygianironplate'))
// mods.woot.stygian_iron_anvil.removeAll()

mods.woot.stygian_iron_anvil.recipeBuilder()
    .input(item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:diamond'))
    .base(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .preserveBase(true)
    .register()

mods.woot.stygian_iron_anvil.recipeBuilder()
    .input(item('minecraft:diamond'), item('minecraft:gold_ingot'), item('minecraft:iron_ingot'), item('minecraft:diamond_block'), item('minecraft:gold_block'), item('minecraft:iron_bars'), item('minecraft:magma'))
    .base(item('minecraft:clay'))
    .output(item('minecraft:clay'))
    .preserveBase()
    .register()


