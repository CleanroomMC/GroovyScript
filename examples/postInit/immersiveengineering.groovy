
// Auto generated groovyscript example file
// MODS_LOADED: immersiveengineering

log.info 'mod \'immersiveengineering\' detected, running script'

// Alloy Kiln:
// Converts two input itemstacks into an output itemstack, consuming fuel (based on burn time).

mods.immersiveengineering.alloy_kiln.removeByInput(item('minecraft:gold_ingot'), item('immersiveengineering:metal:3'))
mods.immersiveengineering.alloy_kiln.removeByOutput(item('immersiveengineering:metal:6'))
// mods.immersiveengineering.alloy_kiln.removeAll()

mods.immersiveengineering.alloy_kiln.recipeBuilder()
    .input(item('minecraft:diamond'), ore('ingotGold'))
    .output(item('minecraft:clay'))
    .register()


// Arc Furnace:
// Converts 1 input itemstack with up to 4 additional inputs into an output itemstack and an optional 'slag' itemstack,
// taking time and using rf power.

mods.immersiveengineering.arc_furnace.removeByInput(item('immersiveengineering:metal:18'), item('immersiveengineering:material:17'))
mods.immersiveengineering.arc_furnace.removeByOutput(item('immersiveengineering:metal:7'))
// mods.immersiveengineering.arc_furnace.removeAll()

mods.immersiveengineering.arc_furnace.recipeBuilder()
    .mainInput(item('minecraft:diamond'))
    .input(item('minecraft:diamond'), ore('ingotGold'))
    .output(item('minecraft:clay'))
    .time(100)
    .energyPerTick(100)
    .slag(item('minecraft:gold_nugget'))
    .register()


// Blast Furnace:
// Converts an input itemstack into an output itemstack and an optional 'slag' itemstack, taking time and consuming fuel
// (based on Blast Furnace Fuels).

mods.immersiveengineering.blast_furnace.removeByInput(item('minecraft:iron_block'))
mods.immersiveengineering.blast_furnace.removeByOutput(item('immersiveengineering:metal:8'))
// mods.immersiveengineering.blast_furnace.removeAll()

mods.immersiveengineering.blast_furnace.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .time(100)
    .slag(item('minecraft:gold_nugget'))
    .register()


// Blast Furnace Fuel:
// Allows an item to be used in the Blast Furnace as a fuel for the given number of ticks.

mods.immersiveengineering.blast_furnace_fuel.removeByInput(item('immersiveengineering:material:6'))
// mods.immersiveengineering.blast_furnace_fuel.removeAll()

mods.immersiveengineering.blast_furnace_fuel.recipeBuilder()
    .input(item('minecraft:clay'))
    .time(100)
    .register()


// Blueprint Crafting:
// Converts any number of input itemstacks into an output itemstack, using a blueprint with the category nbt tag as a
// catalyst.

mods.immersiveengineering.blueprint_crafting.removeByCategory('electrode')
mods.immersiveengineering.blueprint_crafting.removeByInput('components', item('immersiveengineering:metal:38'), item('immersiveengineering:metal:38'), item('immersiveengineering:metal'))
mods.immersiveengineering.blueprint_crafting.removeByOutput('components', item('immersiveengineering:material:8'))
// mods.immersiveengineering.blueprint_crafting.removeAll()

mods.immersiveengineering.blueprint_crafting.recipeBuilder()
    .input(item('minecraft:diamond'), ore('ingotGold'))
    .output(item('minecraft:clay'))
    .category('groovy')
    .register()


mods.immersiveengineering.blueprint_crafting.streamRecipesByCategory('molds')

// Bottling Machine:
// Converts an input itemstack and fluidstack into an output itemstack.

mods.immersiveengineering.bottling_machine.removeByInput(item('minecraft:sponge'), fluid('water') * 1000)
mods.immersiveengineering.bottling_machine.removeByOutput(item('minecraft:potion').withNbt([Potion:'minecraft:mundane']))
// mods.immersiveengineering.bottling_machine.removeAll()

mods.immersiveengineering.bottling_machine.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidInput(fluid('water'))
    .output(item('minecraft:clay'))
    .register()


// Coke Oven:
// Converts an input itemstack into an output itemstack over time, producing a given amount of creosote oil.

mods.immersiveengineering.coke_oven.removeByInput(item('minecraft:log'))
mods.immersiveengineering.coke_oven.removeByOutput(item('immersiveengineering:material:6'))
// mods.immersiveengineering.coke_oven.removeAll()

mods.immersiveengineering.coke_oven.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .time(100)
    .creosote(50)
    .register()


// Crusher:
// Converts an input itemstack into an output itemstack with optional additional chanced item outputs, consuming energy.

mods.immersiveengineering.crusher.removeByInput(item('immersiveengineering:material:7'))
mods.immersiveengineering.crusher.removeByOutput(item('minecraft:sand'))
// mods.immersiveengineering.crusher.removeAll()

mods.immersiveengineering.crusher.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .energy(100)
    .register()

mods.immersiveengineering.crusher.recipeBuilder()
    .input(item('minecraft:diamond_block'))
    .output(item('minecraft:diamond'))
    .secondaryOutput(item('minecraft:gold_ingot'))
    .secondaryOutput(item('minecraft:gold_ingot'), 0.3)
    .energy(100)
    .register()


// Excavator:
// Adds a Mineral Mix with the given name, weight, fail chance, ores, and allowed dimensions. A Mineral Mix can be mined by
// an Excavator Multiblock and scanned via a Core Sample Drill.

mods.immersiveengineering.excavator.removeByMineral('silt')
mods.immersiveengineering.excavator.removeByOres(ore('oreAluminum'))
// mods.immersiveengineering.excavator.removeAll()

mods.immersiveengineering.excavator.recipeBuilder()
    .name('demo')
    .weight(20000)
    .fail(0.5)
    .ore(ore('blockDiamond'), 50)
    .ore('blockGold', 10)
    .dimension(0, 1)
    .register()

mods.immersiveengineering.excavator.recipeBuilder()
    .name('demo')
    .weight(2000)
    .fail(0.1)
    .ore(ore('blockDiamond'), 50)
    .dimension(-1, 1)
    .blacklist()
    .register()


// Fermenter:
// Converts an input itemstack into an output fluidstack with an optional output itemstack, consuming power.

mods.immersiveengineering.fermenter.removeByInput(item('minecraft:reeds'))
mods.immersiveengineering.fermenter.removeByOutput(fluid('ethanol'))
// mods.immersiveengineering.fermenter.removeAll()

mods.immersiveengineering.fermenter.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .fluidOutput(fluid('water'))
    .energy(100)
    .register()


// Metal Press:
// Converts an input itemstack into an output itemstack, with a mold catalyst, consuming power.

mods.immersiveengineering.metal_press.removeByInput(item('minecraft:iron_ingot'))
mods.immersiveengineering.metal_press.removeByInput(item('immersiveengineering:mold'), item('immersiveengineering:metal:8'))
mods.immersiveengineering.metal_press.removeByMold(item('immersiveengineering:mold:4'))
mods.immersiveengineering.metal_press.removeByOutput(item('immersiveengineering:material:2'))
mods.immersiveengineering.metal_press.removeByOutput(item('immersiveengineering:mold'), item('immersiveengineering:metal:31'))
// mods.immersiveengineering.metal_press.removeAll()

mods.immersiveengineering.metal_press.recipeBuilder()
    .mold(item('minecraft:diamond'))
    .input(ore('ingotGold'))
    .output(item('minecraft:clay'))
    .energy(100)
    .register()


// Mixer:
// Converts any number of input itemstacks and a fluidstack into an output fluidstack, consuming power.

mods.immersiveengineering.mixer.removeByInput(fluid('water'), item('minecraft:speckled_melon'))
mods.immersiveengineering.mixer.removeByInput(item('minecraft:sand'), item('minecraft:sand'), item('minecraft:clay_ball'), item('minecraft:gravel'))
mods.immersiveengineering.mixer.removeByOutput(fluid('potion').withNbt([Potion:'minecraft:night_vision']))
// mods.immersiveengineering.mixer.removeAll()

mods.immersiveengineering.mixer.recipeBuilder()
    .input(item('minecraft:diamond'), ore('ingotGold'), ore('ingotGold'), ore('ingotGold'))
    .fluidInput(fluid('water'))
    .fluidOutput(fluid('lava'))
    .energy(100)
    .register()


// Refinery:
// Converts 2 input fluidstacks into an output fluidstack, consuming power.

mods.immersiveengineering.refinery.removeByInput(fluid('plantoil'), fluid('ethanol'))
// mods.immersiveengineering.refinery.removeByOutput(fluid('biodiesel'))
// mods.immersiveengineering.refinery.removeAll()

mods.immersiveengineering.refinery.recipeBuilder()
    .fluidInput(fluid('water'), fluid('water'))
    .fluidOutput(fluid('lava'))
    .energy(100)
    .register()


// Squeezer:
// Converts an input itemstack into either an output itemstack, fluidstack, or both, using energy.

mods.immersiveengineering.squeezer.removeByInput(item('minecraft:wheat_seeds'))
mods.immersiveengineering.squeezer.removeByOutput(fluid('plantoil'))
mods.immersiveengineering.squeezer.removeByOutput(item('immersiveengineering:material:18'))
// mods.immersiveengineering.squeezer.removeAll()

mods.immersiveengineering.squeezer.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .fluidOutput(fluid('lava'))
    .energy(100)
    .register()

mods.immersiveengineering.squeezer.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .energy(100)
    .register()

mods.immersiveengineering.squeezer.recipeBuilder()
    .input(item('minecraft:clay'))
    .fluidOutput(fluid('water'))
    .energy(100)
    .register()


