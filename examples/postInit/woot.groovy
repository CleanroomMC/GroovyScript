//import ipsis.woot.configuration.EnumConfigKey
//import ipsis.woot.util.WootMobName

//import ipsis.woot.util.WootMobName

if (!isLoaded('woot')) return
println 'mod \'woot\' detected, running script'

// Note:
// Drops, Spawning, Policy, and Mob Config can also be controlled via .json config file
// Drops can also be modified via `custom_drops.json`,
// Spawning can also be modified via `factory_config.json`,
// Policy and Mob Config can also be modified via `factory_config.json`.


// Stygian Iron Anvil:
// Has a catalyst (which may or may not be consumed) placed on the anvil, with the input items thrown atop the base.
// The anvil must be above a Magma Block and then right clicked with a Hammer, converting the input items into the output item.
mods.woot.stygianironanvil.recipeBuilder()
    .input(item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:diamond'))
    .base(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .preserveBase(true) // Optional, boolean. Defaults to false
    .register()

mods.woot.anvil.recipeBuilder()
    .input(item('minecraft:diamond'),
            item('minecraft:gold_ingot'),
            item('minecraft:iron_ingot'),
            item('minecraft:diamond_block'),
            item('minecraft:gold_block'),
            item('minecraft:iron_bars'),
            item('minecraft:magma')) // Accepts more than 6 items, but JEI only displays the first 6.
    .base(item('minecraft:clay'))
    .output(item('minecraft:clay'))
    .preserveBase() // Toggle preserveBase
    .register()

mods.woot.anvil.removeByBase(item('minecraft:iron_bars'))
mods.woot.anvil.removeByOutput(item('woot:stygianironplate'))
//mods.woot.anvil.removeAll()


// Drops:
// Controls extra drops given by mobs. Chance and Size are both arrays 4 long, containing the values for levels 0/1/2/3 levels of Looting.
mods.woot.drops.recipeBuilder()
    .name('minecraft:zombie')
    .output(item('minecraft:clay'))
    .chance(10, 30, 60, 100)
    .size(5, 10, 20, 50)
    .register()

//mods.woot.drops.removeByEntity(new WootMobName('minecraft:ender_dragon'))
mods.woot.drops.removeByEntity(entity('minecraft:ender_dragon'))
mods.woot.drops.removeByEntity('minecraft:ender_dragon')
mods.woot.drops.removeByEntity('minecraft:ender_dragon', '') // NBT tag
mods.woot.drops.removeByOutput(item('minecraft:dragon_breath'))
//mods.woot.drops.removeAll()


// Spawning:
// Controls item/fluid costs of a given mob or the default cost.
mods.woot.spawning.recipeBuilder()
    .name('minecraft:zombie') // Optional, either a name must be defined or the recipe must be the "defaultSpawnRecipe"
    .input(item('minecraft:clay')) // up to 6 input items
    .fluidInput(fluid('water')) // up to 6 input fluids
    .register()

mods.woot.spawning.recipeBuilder()
    .defaultSpawnRecipe(true) // Optional, either a name must be defined or the recipe must be the "defaultSpawnRecipe"
    .input(item('minecraft:gold_ingot'), item('minecraft:diamond'))
    .register()


//mods.woot.spawning.remove(new WootMobName('minecraft:ender_dragon'))
//mods.woot.spawning.removeByEntity(new WootMobName('minecraft:ender_dragon'))
mods.woot.spawning.removeByEntity(entity('minecraft:ender_dragon'))
mods.woot.spawning.removeByEntity('minecraft:ender_dragon')
mods.woot.spawning.removeByEntity('minecraft:ender_dragon', '') // NBT tag
//mods.woot.spawning.removeAll()


// Policy:
// Controls what entities can be farmed for what items via an entity blacklist, mod blacklist, item output blacklist, item output mod blacklist, and a mob whitelist.
// Note: if the whitelist contains any entities, any entities not in the whitelist are banned (rendering EntityModBlacklist and EntityBlacklist superflous).
// GenerateOnlyList contains all entities which cannot be captured via shard, meaning the controller would need to be obtained a different way.
//mods.woot.policy.addToEntityModBlacklist('minecraft')
mods.woot.policy.addToEntityBlacklist('minecraft:witch') // Also takes a WootMobName
mods.woot.policy.addToItemModBlacklist('woot')
mods.woot.policy.addToItemBlacklist(item('minecraft:gunpowder'))
//mods.woot.policy.addToEntityWhitelist('minecraft:zombie') // Also takes a WootMobName
mods.woot.policy.addToGenerateOnlyList('minecraft:skeleton') // Also takes a WootMobName

mods.woot.policy.removeFromEntityModBlacklist('botania')
mods.woot.policy.removeFromEntityBlacklist('twilightforest:naga') // Also takes a WootMobName
//mods.woot.policy.removeFromItemModBlacklist('minecraft') // Note: has no default entries
//mods.woot.policy.removeFromItemBlacklist(item('minecraft:sugar')) // Note: has no default entries
//mods.woot.policy.removeFromEntityWhitelist('minecraft:wither_skeleton') // Note: has no default entries. Also takes a WootMobName
//mods.woot.policy.removeFromGenerateOnlyList('minecraft:wither_skeleton') // Note: has no default entries. Also takes a WootMobName

//mods.woot.policy.removeAllFromEntityModBlacklist()
//mods.woot.policy.removeAllFromEntityBlacklist()
//mods.woot.policy.removeAllFromItemModBlacklist() // Note: has no default entries
//mods.woot.policy.removeAllFromItemBlacklist() // Note: has no default entries
//mods.woot.policy.removeAllFromEntityWhitelist() // Note: has no default entries
//mods.woot.policy.removeAllFromGenerateOnlyList() // Note: has no default entries

//mods.woot.policy.removeAll()


// Mob Config:
// Control the default values or mob-specific values for a large number of effects, a full list can be found at
// ipsis.woot.configuration.EnumConfigKey. View on Github via the link:
// https://github.com/Ipsis/Woot/blob/55e88f5a15d66cc987e676d665d20f4afbe008b8/src/main/java/ipsis/woot/configuration/EnumConfigKey.java#L14

// Change the default Spawn Ticks interval to 100 (default 320)
mods.woot.mobconfig.add('spawn_ticks', 100)
// Change the Spawn Ticks interval for Zombies to 1 (default the global Spawn Ticks)
mods.woot.mobconfig.add('minecraft:zombie', 'spawn_ticks', 1)

// Remove the unique cost for Wither Skeletons, making it fallback to the default (default 1)
mods.woot.mobconfig.remove('minecraft:wither_skeleton', 'spawn_units')

// Remove all config values set for the Wither
mods.woot.mobconfig.remove('minecraft:wither')

//mods.woot.mobconfig.removeAll()


/* Mob-specific overrides for EnumConfigKey:
 * SPAWN_TICKS
 * KILL_COUNT
 * SPAWN_UNITS
 * DEATH_XP
 * MASS_FX
 * FACTORY_TIER
 * POWER_PER_UNIT
 * T1_POWER_TICK, T2_POWER_TICK, T3_POWER_TICK, T4_POWER_TICK
 * RATE_1_POWER_TICK, RATE_2_POWER_TICK, RATE_3_POWER_TICK
 * MASS_1_POWER_TICK, MASS_2_POWER_TICK, MASS_3_POWER_TICK
 * LOOTING_1_POWER_TICK, LOOTING_2_POWER_TICK, LOOTING_3_POWER_TICK
 * DECAP_1_POWER_TICK, DECAP_2_POWER_TICK, DECAP_3_POWER_TICK
 * XP_1_POWER_TICK, XP_2_POWER_TICK, XP_3_POWER_TICK
 * EFF_1_POWER_TICK, EFF_2_POWER_TICK, EFF_3_POWER_TICK
 * BM_LE_TANK_1_POWER_TICK, BM_LE_TANK_2_POWER_TICK, BM_LE_TANK_3_POWER_TICK
 * BM_LE_ALTAR_1_POWER_TICK, BM_LE_ALTAR_2_POWER_TICK, BM_LE_ALTAR_3_POWER_TICK
 * BM_CRYSTAL_1_POWER_TICK, BM_CRYSTAL_2_POWER_TICK, BM_CRYSTAL_3_POWER_TICK
 * EC_BLOOD_1_POWER_TICK, EC_BLOOD_2_POWER_TICK, EC_BLOOD_3_POWER_TICK
 * RATE_1_PARAM, RATE_2_PARAM, RATE_3_PARAM
 * MASS_1_PARAM, MASS_2_PARAM, MASS_3_PARAM
 * DECAP_1_PARAM, DECAP_2_PARAM, DECAP_3_PARAM
 * XP_1_PARAM, XP_2_PARAM, XP_3_PARAM
 * EFF_1_PARAM, EFF_2_PARAM, EFF_3_PARAM
 * BM_LE_TANK_1_PARAM, BM_LE_TANK_2_PARAM, BM_LE_TANK_3_PARAM
 * BM_LE_ALTAR_1_PARAM, BM_LE_ALTAR_2_PARAM, BM_LE_ALTAR_3_PARAM
 * EC_BLOOD_1_PARAM, EC_BLOOD_2_PARAM, EC_BLOOD_3_PARAM
 * BM_CRYSTAL_1_PARAM, BM_CRYSTAL_2_PARAM, BM_CRYSTAL_3_PARAM
 */

/* Default options for EnumConfigKey (global):
 * TARTARUS_ID
 * SAMPLE_SIZE
 * LEARN_TICKS
 * SPAWN_TICKS
 * HEADHUNTER_1_CHANCE, HEADHUNTER_2_CHANCE, HEADHUNTER_3_CHANCE
 * NUM_MOBS
 * KILL_COUNT
 * SPAWN_UNITS
 * DEATH_XP
 * MASS_FX
 * FACTORY_TIER
 * T1_UNITS_MAX, T2_UNITS_MAX, T3_UNITS_MAX, T4_UNITS_MAX
 * POWER_PER_UNIT
 * T1_POWER_MAX, T2_POWER_MAX, T3_POWER_MAX
 * T1_POWER_RX_TICK, T2_POWER_RX_TICK, T3_POWER_RX_TICK
 * T1_POWER_TICK, T2_POWER_TICK, T3_POWER_TICK, T4_POWER_TICK
 * T2_SHARD_GEN, T3_SHARD_GEN, T4_SHARD_GEN
 * RATE_1_POWER_TICK, RATE_2_POWER_TICK, RATE_3_POWER_TICK
 * MASS_1_POWER_TICK, MASS_2_POWER_TICK, MASS_3_POWER_TICK
 * LOOTING_1_POWER_TICK, LOOTING_2_POWER_TICK, LOOTING_3_POWER_TICK
 * DECAP_1_POWER_TICK, DECAP_2_POWER_TICK, DECAP_3_POWER_TICK
 * XP_1_POWER_TICK, XP_2_POWER_TICK, XP_3_POWER_TICK
 * EFF_1_POWER_TICK, EFF_2_POWER_TICK, EFF_3_POWER_TICK
 * BM_LE_TANK_1_POWER_TICK, BM_LE_TANK_2_POWER_TICK, BM_LE_TANK_3_POWER_TICK
 * BM_LE_ALTAR_1_POWER_TICK, BM_LE_ALTAR_2_POWER_TICK, BM_LE_ALTAR_3_POWER_TICK
 * BM_CRYSTAL_1_POWER_TICK, BM_CRYSTAL_2_POWER_TICK, BM_CRYSTAL_3_POWER_TICK
 * EC_BLOOD_1_POWER_TICK, EC_BLOOD_2_POWER_TICK, EC_BLOOD_3_POWER_TICK
 * RATE_1_PARAM, RATE_2_PARAM, RATE_3_PARAM
 * MASS_1_PARAM, MASS_2_PARAM, MASS_3_PARAM
 * LOOTING_1_PARAM, LOOTING_2_PARAM, LOOTING_3_PARAM
 * DECAP_1_PARAM, DECAP_2_PARAM, DECAP_3_PARAM
 * XP_1_PARAM, XP_2_PARAM, XP_3_PARAM
 * EFF_1_PARAM, EFF_2_PARAM, EFF_3_PARAM
 * BM_LE_TANK_1_PARAM, BM_LE_TANK_2_PARAM, BM_LE_TANK_3_PARAM
 * BM_LE_ALTAR_1_PARAM, BM_LE_ALTAR_2_PARAM, BM_LE_ALTAR_3_PARAM
 * EC_BLOOD_1_PARAM, EC_BLOOD_2_PARAM, EC_BLOOD_3_PARAM
 * BM_CRYSTAL_1_PARAM, BM_CRYSTAL_2_PARAM, BM_CRYSTAL_3_PARAM
 */