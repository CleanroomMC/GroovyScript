
if (!isLoaded('immersiveengineering')) return
println 'mod \'immersiveengineering\' detected, running script'

// Alloy Kiln:
// Converts two input itemstacks into an output itemstack, consuming fuel (based on burn time).
mods.immersiveengineering.alloykiln.recipeBuilder()
    .input(item('minecraft:diamond'), ore('ingotGold'))
    .output(item('minecraft:clay'))
    .register()

mods.immersiveengineering.alloykiln.removeByInput(item('minecraft:gold_ingot'), item('immersiveengineering:metal:3'))
mods.immersiveengineering.alloykiln.removeByOutput(item('immersiveengineering:metal:6'))
//mods.immersiveengineering.alloykiln.removeAll()


// Arc Furnace:
// Converts 1 input itemstack with up to 4 additional inputs into an output itemstack and an optional 'slag' itemstack, taking time and using rf power.
mods.immersiveengineering.arcfurnace.recipeBuilder()
    .mainInput(item('minecraft:diamond'))
    .input(item('minecraft:diamond'), ore('ingotGold'))
    .output(item('minecraft:clay'))
    .time(100)
    .energyPerTick(100)
    .slag(item('minecraft:gold_nugget'))
    .register()

mods.immersiveengineering.arcfurnace.removeByInput(item('immersiveengineering:metal:18'), item('immersiveengineering:material:17'))
mods.immersiveengineering.arcfurnace.removeByOutput(item('immersiveengineering:metal:7'))
//mods.immersiveengineering.arcfurnace.removeAll()


// Blast Furnace:
// Converts an input itemstack into an output itemstack and an optional 'slag' itemstack, taking time and consuming fuel (based on Blast Furnace Fuels).
mods.immersiveengineering.blastfurnace.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .time(100)
    .slag(item('minecraft:gold_nugget'))
    .register()

mods.immersiveengineering.blastfurnace.removeByInput(item('minecraft:iron_block'))
mods.immersiveengineering.blastfurnace.removeByOutput(item('immersiveengineering:metal:8'))
//mods.immersiveengineering.blastfurnace.removeAll()


// Blast Furnace Fuel:
// Allows an item to be used in the Blast Furnace as a fuel for the given number of ticks.
mods.immersiveengineering.blastfurnacefuel.recipeBuilder()
    .input(item('minecraft:clay'))
    .time(100)
    .register()

mods.immersiveengineering.blastfurnacefuel.removeByInput(item('immersiveengineering:material:6'))
//mods.immersiveengineering.blastfurnacefuel.removeAll()


// Blueprint Crafting (Blueprint):
// Converts any number of input itemstacks into an output itemstack, using a blueprint with the category nbt tag as a catalyst.
mods.immersiveengineering.blueprint.recipeBuilder()
    .input(item('minecraft:diamond'), ore('ingotGold'))
    .output(item('minecraft:clay'))
    .category('groovy') // Default blueprint options: components, molds, bullet, specialBullet, electrode.
    .register()

mods.immersiveengineering.blueprint.removeByCategory('electrode')
mods.immersiveengineering.blueprint.removeByInput('components', item('immersiveengineering:metal:38'), item('immersiveengineering:metal:38'), item('immersiveengineering:metal'))
mods.immersiveengineering.blueprint.removeByOutput('components', item('immersiveengineering:material:8'))
//mods.immersiveengineering.blueprint.removeAll()


// Bottling Machine (Bottling):
// Converts an input itemstack and fluidstack into an output itemstack.
mods.immersiveengineering.bottling.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidInput(fluid('water'))
    .output(item('minecraft:clay'))
    .register()

mods.immersiveengineering.bottling.removeByInput(item('minecraft:sponge'), fluid('water') * 1000)
mods.immersiveengineering.bottling.removeByOutput(item('minecraft:potion').withNbt([Potion:'minecraft:mundane']))
//mods.immersiveengineering.bottling.removeAll()


// Coke Oven:
// Converts an input itemstack into an output itemstack over time, producing a given amount of creosote oil.
mods.immersiveengineering.cokeoven.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .time(100)
    .creosote(50)
    .register()

mods.immersiveengineering.cokeoven.removeByInput(item('minecraft:log'))
mods.immersiveengineering.cokeoven.removeByOutput(item('immersiveengineering:material:6'))
//mods.immersiveengineering.cokeoven.removeAll()


// Crusher:
// Converts an input itemstack into an output itemstack, consuming energy.
mods.immersiveengineering.crusher.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .energy(100)
    .register()

mods.immersiveengineering.crusher.removeByInput(item('immersiveengineering:material:7'))
mods.immersiveengineering.crusher.removeByOutput(item('minecraft:sand'))
//mods.immersiveengineering.crusher.removeAll()


// Excavator:
// Adds a Mineral Mix with the given name, weight, fail chance, ores, and allowed dimensions. A Mineral Mix can be mined
// by an Excavator Multiblock.
// WARNING: reloading will not change chunks already 'discovered'
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

mods.immersiveengineering.excavator.removeByOres(ore('oreAluminum'))
mods.immersiveengineering.excavator.removeByMineral('silt')
//mods.immersiveengineering.excavator.removeAll()


// Fermenter:
// Converts an input itemstack into an output fluidstack with an optional output itemstack, consuming power.
mods.immersiveengineering.fermenter.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))  // Optional
    .fluidOutput(fluid('water'))
    .energy(100)
    .register()

mods.immersiveengineering.fermenter.removeByInput(item('minecraft:reeds'))
mods.immersiveengineering.fermenter.removeByOutput(fluid('ethanol'))
//mods.immersiveengineering.fermenter.removeAll()


// Metal Press:
// Converts an input itemstack into an output itemstack, with a mold catalyst, consuming power.
mods.immersiveengineering.metalpress.recipeBuilder()
    .mold(item('minecraft:diamond'))
    .input(ore('ingotGold'))
    .output(item('minecraft:clay'))
    .energy(100)
    .register()

mods.immersiveengineering.metalpress.removeByInput(item('minecraft:iron_ingot'))
mods.immersiveengineering.metalpress.removeByInput(item('immersiveengineering:mold'), item('immersiveengineering:metal:8'))
mods.immersiveengineering.metalpress.removeByOutput(item('immersiveengineering:material:23'))
mods.immersiveengineering.metalpress.removeByOutput(item('immersiveengineering:mold'), item('immersiveengineering:metal:31'))
mods.immersiveengineering.metalpress.removeByMold(item('immersiveengineering:mold:4'))
//mods.immersiveengineering.metalpress.removeAll()


// Mixer:
// Converts any number of input itemstacks and a fluidstack into an output fluidstack, consuming power.
mods.immersiveengineering.mixer.recipeBuilder()
    .input(item('minecraft:diamond'), ore('ingotGold'), ore('ingotGold'), ore('ingotGold'))
    .fluidInput(fluid('water'))
    .fluidOutput(fluid('lava'))
    .energy(100)
    .register()

mods.immersiveengineering.mixer.removeByInput(item('minecraft:sand'), item('minecraft:sand'), item('minecraft:clay_ball'), item('minecraft:gravel'))
mods.immersiveengineering.mixer.removeByInput(fluid('water'), item('minecraft:speckled_melon'))
mods.immersiveengineering.mixer.removeByOutput(fluid('potion').withNbt([Potion:'minecraft:night_vision']))
//mods.immersiveengineering.mixer.removeAll()


// Refinery:
// Converts 2 input fluidstacks into an output fluidstack, consuming power.
mods.immersiveengineering.refinery.recipeBuilder()
    .fluidInput(fluid('water'), fluid('water'))
    .fluidOutput(fluid('lava'))
    .energy(100)
    .register()

mods.immersiveengineering.refinery.removeByInput(fluid('plantoil'), fluid('ethanol'))
//mods.immersiveengineering.refinery.removeByOutput(fluid('biodiesel')) // <- already removed by 'removeByInput' line
//mods.immersiveengineering.refinery.removeAll()


// Squeezer:
// Converts an input itemstack into either an output itemstack, fluidstack, or both, using energy.
mods.immersiveengineering.squeezer.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay')) // Either an output itemstack or output fluidstack must be defined
    .fluidOutput(fluid('lava')) // Either an output itemstack or output fluidstack must be defined
    .energy(100)
    .register()

// WARNING: If only an output itemstack is defined, the itemstack will not display in JEI.
mods.immersiveengineering.squeezer.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay')) // Either an output itemstack or output fluidstack must be defined
    .energy(100)
    .register()

mods.immersiveengineering.squeezer.recipeBuilder()
    .input(item('minecraft:clay'))
    .fluidOutput(fluid('water')) // Either an output itemstack or output fluidstack must be defined
    .energy(100)
    .register()

mods.immersiveengineering.squeezer.removeByInput(item('minecraft:wheat_seeds'))
mods.immersiveengineering.squeezer.removeByOutput(fluid('plantoil'))
mods.immersiveengineering.squeezer.removeByOutput(item('immersiveengineering:material:18'))
//mods.immersiveengineering.squeezer.removeAll()
