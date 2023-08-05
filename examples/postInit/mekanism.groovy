
if (!isLoaded('mekanism')) return
println 'mod \'mekanism\' detected, running script'

// Bracket Handlers
// Gas
gas('gold')
gas('liquidosmium')

// Infusion
infusion('carbon')
infusion('redstone')
infusion('diamond')
infusion('obsidian')
infusion('fungi')
infusion('bio')
infusion('tin')


// Infusion:
// Add new infusion types and itemstacks to those types

mods.mekanism.infusion.infusion(infusion('diamond'))
    .add(100, item('minecraft:clay'))
    .remove(ore('dustDiamond'))

mods.mekanism.infusion.infusion(infusion('carbon'))
    .removeAll() // Must occur before anything is added
    .add(100, ore('ingotGold'))

// NOTE:
// To register the texture used, you have to add the following event listen to a PreInit file.
// event_manager.listen { TextureStitchEvent.Pre event -> event.getMap().registerSprite(resource('placeholdername:blocks/example')) }
// Where 'assets/placeholdername/textures/blocks/example.png' is the location of the desired texture.
mods.mekanism.infusion.infusion('groovy_example', resource('placeholdername:blocks/example'))
    .add(10, item('minecraft:ice'))
    .add(20, item('minecraft:packed_ice'))


//mods.mekanism.infusion.removeByType(infusion('diamond'))
//mods.mekanism.infusion.removeAll()


// Chemical Infuser:
// Combines two input gas stacks into a output gas stack
mods.mekanism.chemicalinfuser.recipeBuilder()
    .gasInput(gas('copper') * 10, gas('iron'))
    .gasOutput(gas('gold') * 15)
    .register()
//mods.mekanism.chemicalinfuser.add(gas('copper') * 10, gas('iron'), gas('gold') * 15)

mods.mekanism.chemicalinfuser.removeByInput(gas('hydrogen'), gas('chlorine'))
//mods.mekanism.chemicalinfuser.removeAll()


// Chemical Oxidizer (Oxidizer):
// Converts an input itemstack into an output gasstack
mods.mekanism.chemicaloxidizer.recipeBuilder()
    .input(ore('dustGold'))
    .gasOutput(gas('gold'))
    .register()
//mods.mekanism.chemicaloxidizer.add(ore('dustGold'), gas('gold'))

mods.mekanism.chemicaloxidizer.removeByInput(ore('dustSulfur'))
//mods.mekanism.chemicaloxidizer.removeAll()

// Combiner:
// Combines an input itemstack with an extra itemstack to create an output itemstack
mods.mekanism.combiner.recipeBuilder()
    .input(ore('gemQuartz') * 8)
    .extra(item('minecraft:netherrack'))
    .output(item('minecraft:quartz_ore'))
    .register()
//mods.mekanism.combiner.add(ore('gemQuartz') * 8, item('minecraft:netherrack'), item('minecraft:quartz_ore'))

mods.mekanism.combiner.removeByInput(item('minecraft:flint'), item('minecraft:cobblestone'))
//mods.mekanism.combiner.removeAll()


// Crusher:
// Converts an input itemstack into an output itemstack.
mods.mekanism.crusher.recipeBuilder()
    .input(item('minecraft:clay_ball'))
    .output(item('minecraft:gold_ingot'))
    .register()
//mods.mekanism.crusher.add(item('minecraft:clay_ball'), item('minecraft:gold_ingot'))

mods.mekanism.crusher.removeByInput(ore('ingotTin'))
//mods.mekanism.crusher.removeAll()


// Crystallizer:
// Converts an input gasstack into an output itemstack.
mods.mekanism.crystallizer.recipeBuilder()
    .gasInput(gas('cleanGold'))
    .output(item('minecraft:gold_ingot'))
    .register()
//mods.mekanism.crystallizer.add(gas('cleanGold'), item('minecraft:gold_ingot'))

mods.mekanism.crystallizer.removeByInput(gas('cleanGold')) // either remove first or don't remove at all. the recipe below should overwrite the original recipe
//mods.mekanism.crystallizer.removeAll()


// Dissolution Chamber (Dissolver):
// Converts an input itemstack into an output gasstack at the cost of 100mb of Sulfuric Acid
mods.mekanism.dissolutionchamber.recipeBuilder()
    .input(item('minecraft:packed_ice'))
    .gasOutput(gas('water') * 2000)
    .register()
//mods.mekanism.dissolutionchamber.add(item('minecraft:packed_ice'), gas('water'))

mods.mekanism.dissolutionchamber.removeByInput(item('mekanism:oreblock:0'))
//mods.mekanism.dissolutionchamber.removeAll()


// Enrichment Chamber (Enricher):
// Converts an input itemstack into an output itemstack.
mods.mekanism.enrichmentchamber.recipeBuilder()
    .input(item('minecraft:clay_ball'))
    .output(item('minecraft:nether_star'))
    .register()
//mods.mekanism.enrichmentchamber.add(item('minecraft:clay_ball'), item('minecraft:nether_star'))

mods.mekanism.enrichmentchamber.removeByInput(item('minecraft:diamond'))
//mods.mekanism.enrichmentchamber.removeAll()


// Electrolytic Separator (Separator):
// Converts an input fluid into two output gasstacks at the cost of power.
mods.mekanism.electrolyticseparator.recipeBuilder()
    .fluidInput(fluid('lava') * 10)
    .gasOutput(gas('cleanGold') * 5, gas('cleanCopper') * 3)
    .energy(3000)
    .register()
//mods.mekanism.electrolyticseparator.add(fluid('lava') * 10, gas('cleanGold') * 5, gas('cleanCopper') * 3, 3000)

mods.mekanism.electrolyticseparator.removeByInput(fluid('water'))
//mods.mekanism.electrolyticseparator.removeAll()


// Injection Chamber (Injector):
// Converts an input itemstack and 200 of a gasstack into an output itemstack.
mods.mekanism.injectionchamber.recipeBuilder()
    .input(item('minecraft:diamond'))
    .gasInput(gas('water')) // Always uses 200
    .output(item('minecraft:nether_star'))
    .register()
//mods.mekanism.injectionchamber.add(item('minecraft:diamond'), gas('water'), item('minecraft:nether_star'))

mods.mekanism.injectionchamber.removeByInput(item('minecraft:hardened_clay'), gas('water'))
//mods.mekanism.injectionchamber.removeAll()


// Metallurgic Infuser:
// Converts and input itemstack and a varible amount of an infusion type into an output itemstack.
mods.mekanism.metallurgicinfuser.recipeBuilder()
    .input(item('minecraft:nether_star'))
    .infuse(infusion('groovy_example'))
    .amount(50)
    .output(item('minecraft:clay'))
    .register()
//mods.mekanism.metallurgicinfuser.add(item('minecraft:nether_star'), infusion('groovy_example'), 50, item('minecraft:clay'))

mods.mekanism.metallurgicinfuser.removeByInput(ore('dustObsidian'), 'DIAMOND')
//mods.mekanism.metallurgicinfuser.removeAll()


// Osmium Compressor:
// Converts an input itemstack and 200 of a gasstack into an output itemstack. By default, will use Liquid Osmium as the gasstack
mods.mekanism.osmiumcompressor.recipeBuilder()
    .input(item('minecraft:diamond'))
    .gasInput(gas('hydrogen')) // Optional GasStack, default liquidosmium. Always uses 200
    .output(item('minecraft:nether_star'))
    .register()

//mods.mekanism.osmiumcompressor.add(item('minecraft:diamond'), gas('hydrogen'), item('minecraft:nether_star'))

mods.mekanism.osmiumcompressor.removeByInput(ore('dustRefinedObsidian'), gas('liquidosmium'))
//mods.mekanism.osmiumcompressor.removeAll()


// Pressurized Reaction Chamber (PRC):
// Converts an input fluidstack, gasstack, and optional itemstack into an output gasstack and optional itemstack.
mods.mekanism.pressurizedreactionchamber.recipeBuilder()
    .fluidInput(fluid('water'))
    .gasInput(gas('water'))
    .input(item('minecraft:clay_ball')) // Optional IIngredient
    .output(item('minecraft:diamond')) // Optional ItemStack
    .gasOutput(gas('ethene'))
    .register()

mods.mekanism.pressurizedreactionchamber.recipeBuilder()
    .fluidInput(fluid('lava'))
    .gasInput(gas('water') * 100)
    .gasOutput(gas('sulfuricacid') * 5)
    .register()

mods.mekanism.pressurizedreactionchamber.removeByInput(ore('logWood'), fluid('water'), gas('oxygen'))
//mods.mekanism.pressurizedreactionchamber.removeAll()


// Purification Chamber (Purifier):
// Converts an input itemstack and gasstack into an output itemstack.
mods.mekanism.purificationchamber.recipeBuilder()
    .input(item('minecraft:diamond'))
    .gasInput(gas('deuterium'))
    .output(item('minecraft:nether_star'))
    .register()
//mods.mekanism.purificationchamber.add(item('minecraft:diamond'), gas('oxygen'), item('minecraft:nether_star'))

mods.mekanism.purificationchamber.removeByInput(item('mekanism:oreblock:0'), gas('oxygen'))
//mods.mekanism.purificationchamber.removeAll()


// Sawmill:
// Converts an input itemstack into an output itemstack, with an optional additional output.
mods.mekanism.sawmill.recipeBuilder()
    .input(item('minecraft:diamond_block'))
    .output(item('minecraft:diamond') * 9)
    .extra(item('minecraft:clay_ball')) // Optional ItemStack, adds an extra chanced output
    .chance(0.7) // Optional double, defaults to 1.0
    .register()
//mods.mekanism.sawmill.add(item('minecraft:diamond_block'), item('minecraft:diamond') * 9, item('minecraft:clay_ball'), 0.7)

mods.mekanism.sawmill.removeByInput(item('minecraft:ladder'))
//mods.mekanism.sawmill.removeAll()


// Smelting (Smelter):
// Converts an input itemstack into an output itemstack in a recipe exclusive to the Smelter. Overrides the default furnace recipe, if applicable.
// WARNING: Exclusive recipes are not displayed in JEI.
mods.mekanism.smelting.recipeBuilder()
    .input(item('minecraft:clay_ball'))
    .output(item('minecraft:clay'))
    .register()
//mods.mekanism.smelting.add(item('minecraft:diamond_block'), item('minecraft:clay'))

// No recipes are exclusive to the Energised Smelter by default
//mods.mekanism.smelting.removeByInput(item('minecraft:clay'))
//mods.mekanism.smelting.removeAll()


// Solar Neutron Activator (SNA):
// Converts an input gasstack into an output gasstack while exposed to the sun.
mods.mekanism.solarneutronactivator.recipeBuilder()
    .gasInput(gas('water'))
    .gasOutput(gas('hydrogen'))
    .register()
//mods.mekanism.solarneutronactivator.add(gas('water'), gas('hydrogen'))

mods.mekanism.solarneutronactivator.removeByInput(gas('lithium'))
//mods.mekanism.solarneutronactivator.removeAll()


// Thermal Evaporation Plant (Thermal Evaporation, TEP):
// Converts an input fluidstack into an output fluidstack over time based on multiblock temperature.
mods.mekanism.thermalevaporationplant.recipeBuilder()
    .fluidInput(fluid('water'))
    .fluidOutput(fluid('steam'))
    .register()
//mods.mekanism.thermalevaporationplant.add(fluid('water'), fluid('steam'))

mods.mekanism.thermalevaporationplant.removeByInput(fluid('water'))
//mods.mekanism.thermalevaporationplant.removeAll()


// Washer:
// Converts an input gasstack into an output gasstack at the cost of 5mb of water.
mods.mekanism.washer.recipeBuilder()
    .gasInput(gas('water') * 10)
    .gasOutput(gas('hydrogen') * 20)
    .register()
//mods.mekanism.washer.add(gas('water'), gas('hydrogen'))

mods.mekanism.washer.removeByInput(gas('iron'))
//mods.mekanism.washer.removeAll()
