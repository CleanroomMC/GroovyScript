
// Auto generated groovyscript example file
// MODS_LOADED: industrialforegoing

import net.minecraft.potion.PotionEffect

log.info 'mod \'industrialforegoing\' detected, running script'

// Bioreactor:
// Converts an input item into Biofuel, with the amount of Biofuel generated being based on the number of concurrent
// conversion processes inside the Bioreactor.

mods.industrialforegoing.bio_reactor.removeByInput(item('minecraft:wheat_seeds'))
// mods.industrialforegoing.bio_reactor.removeAll()

mods.industrialforegoing.bio_reactor.add(item('minecraft:clay'))

// Latex Extractor:
// Converts an input block in-world into a fluidstack over time, eventually breaking the block.

mods.industrialforegoing.extractor.removeByInput(item('minecraft:log2:1'))
// mods.industrialforegoing.extractor.removeByOutput(fluid('latex'))
// mods.industrialforegoing.extractor.removeAll()

mods.industrialforegoing.extractor.add(item('minecraft:clay'), fluid('lava') * 50)
mods.industrialforegoing.extractor.add(item('minecraft:stone'), fluid('water') * 100, 1)

// Fluid Dictionary Converter:
// Converts one fluid into another fluid at a given ratio.

// mods.industrialforegoing.fluid_dictionary.removeByInput(fluid('essence'))
// mods.industrialforegoing.fluid_dictionary.removeByOutput(fluid(essence'))
// mods.industrialforegoing.fluid_dictionary.removeAll()

mods.industrialforegoing.fluid_dictionary.add(fluid('biofuel'), fluid('latex'),)
mods.industrialforegoing.fluid_dictionary.add(fluid('latex'), fluid('biofuel'),)
mods.industrialforegoing.fluid_dictionary.add(fluid('essence'), fluid('latex'), 2)
mods.industrialforegoing.fluid_dictionary.add(fluid('latex'), fluid('essence'), 0.5)

// Laser Drill:
// Converts power into ores, with a given weight, between a minimum and maximum Y value, in any whitelisted biome or not in
// any blacklisted biome, and with a specific color of laser lens impacting the probability.

mods.industrialforegoing.laser_drill.removeByBlacklist(biome('minecraft:sky'))
mods.industrialforegoing.laser_drill.removeByLens(5)
// mods.industrialforegoing.laser_drill.removeByLens(item('industrialforegoing:laser_lens:5'))
mods.industrialforegoing.laser_drill.removeByOutput(item('minecraft:coal_ore'))
mods.industrialforegoing.laser_drill.removeByWhitelist(biome('minecraft:hell'))
// mods.industrialforegoing.laser_drill.removeAll()

mods.industrialforegoing.laser_drill.recipeBuilder()
    .output(item('minecraft:clay'))
    .lensMeta(5)
    .weight(100)
    .register()


// Fermentation Station:
// Converts an input fluidstack into an output fluidstack.

mods.industrialforegoing.ore_fermenter.removeByInput(fluid('if.ore_fluid_raw').withNbt([Ore: 'oreRedstone']))
// mods.industrialforegoing.ore_fermenter.removeByOutput(fluid('if.ore_fluid_fermented').withNbt([Ore: 'oreRedstone']))
// mods.industrialforegoing.ore_fermenter.removeAll()

mods.industrialforegoing.ore_fermenter.add(fluid('if.ore_fluid_raw').withNbt(['Ore': 'oreGold']), fluid('if.ore_fluid_fermented').withNbt(['Ore': 'oreGold']) * 2)

// Washing Factory:
// Converts an input itemstack and input fluidstack into an output fluidstack.

// mods.industrialforegoing.ore_raw.removeByInput(fluid('meat'))
mods.industrialforegoing.ore_raw.removeByOre(ore('oreRedstone'))
// mods.industrialforegoing.ore_raw.removeByOre('oreRedstone')
// mods.industrialforegoing.ore_raw.removeByOutput(fluid('if.ore_fluid_raw').withNbt(['Ore': 'oreRedstone']),)
// mods.industrialforegoing.ore_raw.removeAll()

mods.industrialforegoing.ore_raw.add(ore('stone'), fluid('water') * 1000, fluid('lava') * 50)
mods.industrialforegoing.ore_raw.add(ore('oreGold'), fluid('meat') * 200, fluid('if.ore_fluid_raw').withNbt(['Ore': 'oreGold']) * 300)

// Fluid Sieving Machine:
// Converts an input itemstack and input fluidstack into an output itemstack.

mods.industrialforegoing.ore_sieve.removeByInput(fluid('if.pink_slime'))
mods.industrialforegoing.ore_sieve.removeByInput(item('minecraft:sand'))
// mods.industrialforegoing.ore_sieve.removeByOutput(item('industrialforegoing:pink_slime_ingot)
// mods.industrialforegoing.ore_sieve.removeAll()

mods.industrialforegoing.ore_sieve.add(fluid('lava') * 5, item('minecraft:gold_ingot'), item('minecraft:clay'))
mods.industrialforegoing.ore_sieve.add(fluid('if.ore_fluid_fermented').withNbt(['Ore': 'oreGold']) * 100, item('minecraft:nether_star') * 2, item('minecraft:clay'))

// Protein Reactor:
// Converts an input item into Protein, with the amount of Protein generated being based on the number of concurrent
// conversion processes inside the Protein Reactor.

mods.industrialforegoing.protein_reactor.removeByInput(item('minecraft:porkchop'))
// mods.industrialforegoing.protein_reactor.removeAll()

mods.industrialforegoing.protein_reactor.add(item('minecraft:clay'))

// Sludge Refiner:
// Converts 1000mb of Sludge into a random itemstack based on the weight of the given itemstack.

mods.industrialforegoing.sludge_refiner.removeByOutput(item('minecraft:clay_ball'))
// mods.industrialforegoing.sludge_refiner.removeAll()

mods.industrialforegoing.sludge_refiner.add(item('minecraft:gold_ingot'), 5)

// Straw:
// Converts an fluid block in-world into various effects for the player when consumed via a straw.

// mods.industrialforegoing.straw.removeAll()

mods.industrialforegoing.straw.recipeBuilder()
    .fluidInput(fluid('if.pink_slime'))
    .effect(new PotionEffect(potion('minecraft:strength'), 1800, 3))
    .register()


