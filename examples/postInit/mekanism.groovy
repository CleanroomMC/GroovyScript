
// Auto generated groovyscript example file
// MODS_LOADED: mekanism

println 'mod \'mekanism\' detected, running script'

// Infusion:
// Add new infusion types and itemstacks to those types.

mods.mekanism.infusion.remove(ore('dustDiamond'))
mods.mekanism.infusion.removeByType(infusion('carbon'))
// mods.mekanism.infusion.removeByType(infusion('diamond'))
// mods.mekanism.infusion.removeAll()

mods.mekanism.infusion.addType('groovy_example', resource('placeholdername:blocks/mekanism_infusion_texture'))
mods.mekanism.infusion.add(infusion('diamond'), 100, item('minecraft:clay'))
mods.mekanism.infusion.add(infusion('carbon'), 100, item('minecraft:gold_ingot'))
mods.mekanism.infusion.add('groovy_example', 10, item('minecraft:ice'))
mods.mekanism.infusion.add('groovy_example', 20, item('minecraft:packed_ice'))

// Chemical Infuser:
// Combines two input gas stacks into a output gas stack.

mods.mekanism.chemical_infuser.removeByInput(gas('hydrogen'), gas('chlorine'))
// mods.mekanism.chemical_infuser.removeAll()

mods.mekanism.chemical_infuser.recipeBuilder()
    .gasInput(gas('copper') * 10, gas('iron'))
    .gasOutput(gas('gold') * 15)
    .register()


// mods.mekanism.chemical_infuser.add(gas('copper') * 10, gas('iron'), gas('gold') * 15)

// Chemical Oxidizer:
// Converts an input itemstack into an output gasstack.

mods.mekanism.chemical_oxidizer.removeByInput(ore('dustSulfur'))
// mods.mekanism.chemical_oxidizer.removeAll()

mods.mekanism.chemical_oxidizer.recipeBuilder()
    .input(ore('dustGold'))
    .gasOutput(gas('gold'))
    .register()


// mods.mekanism.chemical_oxidizer.add(ore('dustGold'), gas('gold'))

// Combiner:
// Combines an input itemstack with an extra itemstack to create an output itemstack.

mods.mekanism.combiner.removeByInput(item('minecraft:flint'), item('minecraft:cobblestone'))
// mods.mekanism.combiner.removeAll()

mods.mekanism.combiner.recipeBuilder()
    .input(ore('gemQuartz') * 8)
    .extra(item('minecraft:netherrack'))
    .output(item('minecraft:quartz_ore'))
    .register()


// mods.mekanism.combiner.add(ore('gemQuartz') * 8, item('minecraft:netherrack'), item('minecraft:quartz_ore'))

// Crusher:
// Converts an input itemstack into an output itemstack.

mods.mekanism.crusher.removeByInput(ore('ingotTin'))
// mods.mekanism.crusher.removeAll()

mods.mekanism.crusher.recipeBuilder()
    .input(item('minecraft:clay_ball'))
    .output(item('minecraft:gold_ingot'))
    .register()


// mods.mekanism.crusher.add(item('minecraft:clay_ball'), item('minecraft:gold_ingot'))

// Crystallizer:
// Converts an input gasstack into an output itemstack.

mods.mekanism.crystallizer.removeByInput(gas('cleanGold'))
// mods.mekanism.crystallizer.removeAll()

mods.mekanism.crystallizer.recipeBuilder()
    .gasInput(gas('cleanGold'))
    .output(item('minecraft:gold_ingot'))
    .register()


// mods.mekanism.crystallizer.add(gas('cleanGold'), item('minecraft:gold_ingot'))

// Dissolution Chamber:
// Converts an input itemstack into an output gasstack at the cost of 100mb of Sulfuric Acid.

mods.mekanism.dissolution_chamber.removeByInput(item('mekanism:oreblock:0'))
// mods.mekanism.dissolution_chamber.removeAll()

mods.mekanism.dissolution_chamber.recipeBuilder()
    .input(item('minecraft:packed_ice'))
    .gasOutput(gas('water') * 2000)
    .register()


// mods.mekanism.dissolution_chamber.add(item('minecraft:packed_ice'), gas('water'))

// Electrolytic Separator:
// Converts an input fluid into two output gasstacks at the cost of power.

mods.mekanism.electrolytic_separator.removeByInput(fluid('water'))
// mods.mekanism.electrolytic_separator.removeAll()

mods.mekanism.electrolytic_separator.recipeBuilder()
    .fluidInput(fluid('lava') * 10)
    .gasOutput(gas('cleanGold') * 5, gas('cleanCopper') * 3)
    .energy(3000)
    .register()


// mods.mekanism.electrolytic_separator.add(fluid('lava') * 10, gas('cleanGold') * 5, gas('cleanCopper') * 3, 3000)

// Enrichment Chamber:
// Converts an input itemstack into an output itemstack.

mods.mekanism.enrichment_chamber.removeByInput(item('minecraft:diamond'))
// mods.mekanism.enrichment_chamber.removeAll()

mods.mekanism.enrichment_chamber.recipeBuilder()
    .input(item('minecraft:clay_ball'))
    .output(item('minecraft:nether_star'))
    .register()


// mods.mekanism.enrichment_chamber.add(item('minecraft:clay_ball'), item('minecraft:nether_star'))

// Injection Chamber:
// Converts an input itemstack and 200 of a gasstack into an output itemstack.

mods.mekanism.injection_chamber.removeByInput(item('minecraft:hardened_clay'), gas('water'))
// mods.mekanism.injection_chamber.removeAll()

mods.mekanism.injection_chamber.recipeBuilder()
    .input(item('minecraft:diamond'))
    .gasInput(gas('water')) // Always uses 200 gas
    .output(item('minecraft:nether_star'))
    .register()


// mods.mekanism.injection_chamber.add(item('minecraft:diamond'), gas('water'), item('minecraft:nether_star'))

// Metallurgic Infuser:
// Converts and input itemstack and a varible amount of an infusion type into an output itemstack.

mods.mekanism.metallurgic_infuser.removeByInput(ore('dustObsidian'), 'DIAMOND')
// mods.mekanism.metallurgic_infuser.removeAll()

mods.mekanism.metallurgic_infuser.recipeBuilder()
    .input(item('minecraft:nether_star'))
    .infuse(infusion('groovy_example'))
    .amount(50)
    .output(item('minecraft:clay'))
    .register()


// mods.mekanism.metallurgic_infuser.add(item('minecraft:nether_star'), infusion('groovy_example'), 50, item('minecraft:clay'))

// Osmium Compressor:
// Converts an input itemstack and 200 of a gasstack into an output itemstack. By default, will use Liquid Osmium as the
// gasstack.

mods.mekanism.osmium_compressor.removeByInput(ore('dustRefinedObsidian'), gas('liquidosmium'))
// mods.mekanism.osmium_compressor.removeAll()

mods.mekanism.osmium_compressor.recipeBuilder()
    .input(item('minecraft:diamond'))
    .gasInput(gas('hydrogen')) // Always uses 200 gas
    .output(item('minecraft:nether_star'))
    .register()


// mods.mekanism.osmium_compressor.add(item('minecraft:diamond'), gas('hydrogen'), item('minecraft:nether_star'))

// Pressurized Reaction Chamber:
// Converts an input fluidstack, gasstack, and optional itemstack into an output gasstack and optional itemstack.

mods.mekanism.pressurized_reaction_chamber.removeByInput(ore('logWood'), fluid('water'), gas('oxygen'))
// mods.mekanism.pressurized_reaction_chamber.removeAll()

mods.mekanism.pressurized_reaction_chamber.recipeBuilder()
    .fluidInput(fluid('water'))
    .gasInput(gas('water'))
    .input(item('minecraft:clay_ball'))
    .gasOutput(gas('ethene'))
    .register()


// Purification Chamber:
// Converts an input itemstack and gasstack into an output itemstack.

mods.mekanism.purification_chamber.removeByInput(item('mekanism:oreblock:0'), gas('oxygen'))
// mods.mekanism.purification_chamber.removeAll()

mods.mekanism.purification_chamber.recipeBuilder()
    .input(item('minecraft:diamond'))
    .gasInput(gas('deuterium'))
    .output(item('minecraft:nether_star'))
    .register()


// mods.mekanism.purification_chamber.add(item('minecraft:diamond'), gas('oxygen'), item('minecraft:nether_star'))

// Sawmill:
// Converts an input itemstack into an output itemstack, with an optional additional output.

mods.mekanism.sawmill.removeByInput(item('minecraft:ladder'))
// mods.mekanism.sawmill.removeAll()

mods.mekanism.sawmill.recipeBuilder()
    .input(item('minecraft:diamond_block'))
    .output(item('minecraft:diamond') * 9)
    .extra(item('minecraft:clay_ball'))
    .register()


// mods.mekanism.sawmill.add(item('minecraft:diamond_block'), item('minecraft:diamond') * 9, item('minecraft:clay_ball'), 0.7)

// Smelting:
// Converts an input itemstack into an output itemstack in a recipe exclusive to the Smelter. Overrides the default furnace
// recipe, if applicable.

// mods.mekanism.smelting.removeByInput(item('minecraft:clay'))
// mods.mekanism.smelting.removeAll()

mods.mekanism.smelting.recipeBuilder()
    .input(item('minecraft:clay_ball'))
    .output(item('minecraft:clay'))
    .register()


// mods.mekanism.smelting.add(item('minecraft:diamond_block'), item('minecraft:clay'))

// Solar Neutron Activator:
// Converts an input gasstack into an output gasstack while exposed to the sun.

mods.mekanism.solar_neutron_activator.removeByInput(gas('lithium'))
// mods.mekanism.solar_neutron_activator.removeAll()

mods.mekanism.solar_neutron_activator.recipeBuilder()
    .gasInput(gas('water'))
    .gasOutput(gas('hydrogen'))
    .register()


// mods.mekanism.solar_neutron_activator.add(gas('water'), gas('hydrogen'))

// Thermal Evaporation Plant:
// Converts an input fluidstack into an output fluidstack over time based on multiblock temperature.

mods.mekanism.thermal_evaporation_plant.removeByInput(fluid('water'))
// mods.mekanism.thermal_evaporation_plant.removeAll()

mods.mekanism.thermal_evaporation_plant.recipeBuilder()
    .fluidInput(fluid('water'))
    .fluidOutput(fluid('steam'))
    .register()


// mods.mekanism.thermal_evaporation_plant.add(fluid('water'), fluid('steam'))

// Washer:
// Converts an input gasstack into an output gasstack at the cost of 5mb of water.

mods.mekanism.washer.removeByInput(gas('iron'))
// mods.mekanism.washer.removeAll()

mods.mekanism.washer.recipeBuilder()
    .gasInput(gas('water') * 10)
    .gasOutput(gas('hydrogen') * 20)
    .register()


// mods.mekanism.washer.add(gas('water'), gas('hydrogen'))

