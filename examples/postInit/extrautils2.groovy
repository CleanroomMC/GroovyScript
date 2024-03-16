
// Auto generated groovyscript example file
// MODS_LOADED: extrautils2

import com.rwtema.extrautils2.power.IWorldPowerMultiplier
import com.rwtema.extrautils2.tile.TilePassiveGenerator

println 'mod \'extrautils2\' detected, running script'

// Crusher:
// Converts an input itemstack into an output itemstack with a chance of an additional itemstack output, consuming energy.

mods.extrautils2.crusher.removeByInput(item('minecraft:blaze_rod'))
// mods.extrautils2.crusher.removeAll()

mods.extrautils2.crusher.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .energy(1000)
    .time(5)
    .register()

mods.extrautils2.crusher.recipeBuilder()
    .input(item('minecraft:blaze_rod'))
    .output(item('minecraft:gold_ingot') * 3)
    .output(item('minecraft:gold_ingot'))
    .chance(0.2f)
    .energy(1000)
    .time(5)
    .register()


// Enchanter:
// Converts two input itemstacks into an output itemstack, consuming energy and require nearby enchantment power providers.

mods.extrautils2.enchanter.removeByInput(item('minecraft:bookshelf'))
// mods.extrautils2.enchanter.removeAll()

mods.extrautils2.enchanter.recipeBuilder()
    .input(item('minecraft:gold_ingot'), item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .energy(1000)
    .time(5)
    .register()


// Furnace:
// Converts an input itemstack into an output itemstack, consuming energy.

mods.extrautils2.furnace.removeByInput(item('minecraft:emerald_ore:*'))
// mods.extrautils2.furnace.removeAll()

mods.extrautils2.furnace.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .energy(1000)
    .time(5)
    .register()


// Generators:
// Converts up to two input itemstacks and an input fluidstack into energy over time.

mods.extrautils2.generator.remove('extrautils2:generator_lava', fluid('lava'))
mods.extrautils2.generator.remove('extrautils2:generator_culinary', item('minecraft:apple'))
mods.extrautils2.generator.removeByGenerator('extrautils2:generator_death')
// mods.extrautils2.generator.removeAll()

mods.extrautils2.generator.recipeBuilder()
    .generator('extrautils2:generator_pink')
    .input(item('minecraft:clay'))
    .energy(1000)
    .energyPerTick(100)
    .register()

mods.extrautils2.generator.recipeBuilder()
    .generator('extrautils2:generator_slime')
    .input(item('minecraft:clay') * 3)
    .input(item('minecraft:gold_ingot'))
    .energy(1000000)
    .energyPerTick(100)
    .register()

mods.extrautils2.generator.recipeBuilder()
    .generator('extrautils2:generator_redstone')
    .input(item('minecraft:clay') * 3)
    .fluidInput(fluid('water') * 300)
    .energy(1000)
    .energyPerTick(100)
    .register()

mods.extrautils2.generator.recipeBuilder()
    .generator('extrautils2:generator_lava')
    .fluidInput(fluid('water') * 300)
    .energy(100)
    .energyPerTick(1000)
    .register()


// Grid Power Generators:
// Passively produces Grid Power into the Owner's GP network

mods.extrautils2.grid_power_passive_generator.setBasePower(resource('generators:creative'), 5f)
mods.extrautils2.grid_power_passive_generator.setBasePower(resource('generators:player_wind_up'), 100f)
mods.extrautils2.grid_power_passive_generator.setPowerLevel(resource('generators:solar'), { TilePassiveGenerator generator, World world -> 100f })
mods.extrautils2.grid_power_passive_generator.setPowerMultiplier(resource('generators:wind'), IWorldPowerMultiplier.CONSTANT)
mods.extrautils2.grid_power_passive_generator.setScaling(resource('generators:creative'), 500.0F, 0.5F, 1000.0F, 0.25F, 1500.0F, 0.05F)

// Resonator:
// Converts and input itemstack into an output itemstack, consuming Grid Power from the Owner's GP network. Can also
// require an active Rainbow Generator.

mods.extrautils2.resonator.removeByInput(item('minecraft:quartz_block'))
mods.extrautils2.resonator.removeByOutput(item('extrautils2:ingredients:4'))
// mods.extrautils2.resonator.removeAll()

mods.extrautils2.resonator.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .rainbow()
    .energy(1000)
    .register()

mods.extrautils2.resonator.recipeBuilder()
    .input(item('minecraft:gold_block'))
    .output(item('minecraft:clay') * 5)
    .energy(100)
    .register()

mods.extrautils2.resonator.recipeBuilder()
    .input(item('minecraft:redstone'))
    .output(item('extrautils2:ingredients:4'))
    .ownerTag()
    .energy(5000)
    .register()


