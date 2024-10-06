
// Auto generated groovyscript example file
// MODS_LOADED: magneticraft

import com.cout970.magneticraft.api.registries.machines.hydraulicpress.HydraulicPressMode

log.info 'mod \'magneticraft\' detected, running script'

// Crushing Table:
// Converts an input itemstack into an output itemstack when placed on top of the Crushing Table and interacted with by a
// Hammer which has.

mods.magneticraft.crushing_table.removeByInput(item('minecraft:iron_ore'))
mods.magneticraft.crushing_table.removeByOutput(item('minecraft:gunpowder'))
// mods.magneticraft.crushing_table.removeAll()

mods.magneticraft.crushing_table.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.magneticraft.crushing_table.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .register()


// Gasification Unit:
// Converts an input itemstack into either an output itemstack, an output fluidstack, or both in a Gasification Unit block.

// mods.magneticraft.gasification_unit.removeAll()

mods.magneticraft.gasification_unit.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .duration(50)
    .minTemperature(700)
    .register()

mods.magneticraft.gasification_unit.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidOutput(fluid('lava'))
    .duration(100)
    .minTemperature(500)
    .register()


// Grinder:
// Converts an input itemstack into an output itemstack with a chance at a second itemstack in a Grinder Multiblock.

mods.magneticraft.grinder.removeByInput(item('minecraft:iron_ore'))
mods.magneticraft.grinder.removeByOutput(item('minecraft:gravel'))
// mods.magneticraft.grinder.removeAll()

mods.magneticraft.grinder.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .ticks(50)
    .register()

mods.magneticraft.grinder.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'), item('minecraft:gold_ingot'))
    .chance(10)
    .ticks(50)
    .register()


// Hydraulic Press:
// Converts an input itemstack into an output itemstack when set to a given mode in a Hydraulic Press Multiblock.

mods.magneticraft.hydraulic_press.removeByInput(item('minecraft:iron_ingot'))
mods.magneticraft.hydraulic_press.removeByMode(HydraulicPressMode.MEDIUM)
mods.magneticraft.hydraulic_press.removeByOutput(item('minecraft:cobblestone'))
// mods.magneticraft.hydraulic_press.removeAll()

mods.magneticraft.hydraulic_press.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .ticks(50)
    .register()

mods.magneticraft.hydraulic_press.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .ticks(50)
    .medium()
    .register()

mods.magneticraft.hydraulic_press.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .ticks(100)
    .mode(HydraulicPressMode.HEAVY)
    .register()


// Oil Heater:
// Converts an input fluidstack into an output fluidstack in a Oil Heater Multiblock.

mods.magneticraft.oil_heater.removeByInput(fluid('oil'))
mods.magneticraft.oil_heater.removeByOutput(fluid('steam'))
// mods.magneticraft.oil_heater.removeAll()

mods.magneticraft.oil_heater.recipeBuilder()
    .fluidInput(fluid('water'))
    .fluidOutput(fluid('lava'))
    .duration(50)
    .minTemperature(200)
    .register()

mods.magneticraft.oil_heater.recipeBuilder()
    .fluidInput(fluid('lava'))
    .fluidOutput(fluid('water'))
    .duration(100)
    .minTemperature(50)
    .register()


// Refinery:
// Converts an input fluidstack into up to three output fluidstacks in a Refinery Multiblock.

mods.magneticraft.refinery.removeByInput(fluid('steam'))
mods.magneticraft.refinery.removeByOutput(fluid('fuel'))
// mods.magneticraft.refinery.removeAll()

mods.magneticraft.refinery.recipeBuilder()
    .fluidInput(fluid('water'))
    .fluidOutput(fluid('lava'))
    .duration(50)
    .register()

mods.magneticraft.refinery.recipeBuilder()
    .fluidInput(fluid('lava'))
    .fluidOutput(fluid('water'))
    .duration(100)
    .register()


// Sieve:
// Converts an input itemstack into up to three output itemstacks, each with a separate chance in a Sieve Multiblock.

mods.magneticraft.sieve.removeByInput(item('minecraft:sand'))
mods.magneticraft.sieve.removeByOutput(item('minecraft:quartz'))
// mods.magneticraft.sieve.removeAll()

mods.magneticraft.sieve.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'), 0.5)
    .duration(50)
    .register()

mods.magneticraft.sieve.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'), 0.05)
    .output(item('minecraft:clay'))
    .duration(50)
    .register()

mods.magneticraft.sieve.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'), 0.5)
    .output(item('minecraft:clay'), 0.5)
    .output(item('minecraft:clay'), 0.5)
    .duration(1)
    .register()


// Sluice Box:
// Converts an input itemstack into any number of output itemstacks each with a given chance. Consumes a bucket of water
// per batch, and can be crafted 10 at a time in a Sluice Box.

mods.magneticraft.sluice_box.removeByInput(item('minecraft:sand'))
mods.magneticraft.sluice_box.removeByOutput(item('minecraft:cobblestone'))
// mods.magneticraft.sluice_box.removeAll()

mods.magneticraft.sluice_box.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()

mods.magneticraft.sluice_box.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'), 0.5)
    .register()

mods.magneticraft.sluice_box.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'), 0.5)
    .output(item('minecraft:clay'), 0.3)
    .output(item('minecraft:clay'), 0.2)
    .output(item('minecraft:clay'), 0.1)
    .register()


// Thermopile:
// The Thermopile generates energy from temperature differences modified by the thermal resistances of the block. This lets
// you generate energy when placed on either side of a Thermopile.

mods.magneticraft.thermopile.removeByInput(blockstate('minecraft:ice'))
// mods.magneticraft.thermopile.removeAll()

mods.magneticraft.thermopile.recipeBuilder()
    .state(blockstate('minecraft:clay'))
    .conductivity(10)
    .temperature(500)
    .register()

mods.magneticraft.thermopile.recipeBuilder()
    .state(blockstate('minecraft:diamond_block'))
    .conductivity(70)
    .temperature(700)
    .register()


