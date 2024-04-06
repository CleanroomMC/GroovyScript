
// Auto generated groovyscript example file
// MODS_LOADED: naturesaura

println 'mod \'naturesaura\' detected, running script'

// Natural Altar Infusion:
// Converts an input itemstack into an itemstack in a multiblock structure, with an optional catalyst block, costing aura
// and taking a configurable duration.

mods.naturesaura.altar.removeByCatalyst(item('naturesaura:crushing_catalyst'))
mods.naturesaura.altar.removeByInput(item('minecraft:rotten_flesh'))
mods.naturesaura.altar.removeByName(resource('naturesaura:infused_iron'))
mods.naturesaura.altar.removeByOutput(item('minecraft:soul_sand'))
// mods.naturesaura.altar.removeAll()

mods.naturesaura.altar.recipeBuilder()
    .name('demo')
    .input(item('minecraft:clay'))
    .catalyst(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .aura(100)
    .time(100)
    .register()

mods.naturesaura.altar.recipeBuilder()
    .name(resource('example:demo'))
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot') * 8)
    .aura(30)
    .time(5)
    .register()

mods.naturesaura.altar.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'))
    .catalyst(item('minecraft:clay'))
    .aura(50)
    .time(100)
    .register()


// Offering to the Gods:
// Converts up to 16 times the input itemstack into output itemstacks by consuming a catalyst item from the ground in a
// multiblock structure.

// mods.naturesaura.offering.removeByCatalyst(item('naturesaura:calling_spirit'))
mods.naturesaura.offering.removeByInput(item('minecraft:nether_star'))
mods.naturesaura.offering.removeByName(resource('naturesaura:token_euphoria'))
mods.naturesaura.offering.removeByOutput(item('naturesaura:sky_ingot'))
// mods.naturesaura.offering.removeAll()

mods.naturesaura.offering.recipeBuilder()
    .name('demo')
    .input(item('minecraft:diamond'))
    .catalyst(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot') * 8)
    .register()

mods.naturesaura.offering.recipeBuilder()
    .name(resource('example:demo'))
    .input(item('minecraft:clay'))
    .catalyst(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond') * 8)
    .register()

mods.naturesaura.offering.recipeBuilder()
    .input(item('minecraft:gold_ingot') * 10)
    .catalyst(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .register()


// Ritual of the Forest:
// Converts multiple input items into an output itemstack after a duration when a sapling grows in the middle of a
// multiblock structure.

mods.naturesaura.ritual.removeByInput(item('naturesaura:infused_stone'))
mods.naturesaura.ritual.removeByName(resource('naturesaura:eye_improved'))
mods.naturesaura.ritual.removeByOutput(item('naturesaura:eye'))
mods.naturesaura.ritual.removeBySapling(item('minecraft:sapling:3'))
// mods.naturesaura.ritual.removeAll()

mods.naturesaura.ritual.recipeBuilder()
    .name('demo')
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .time(100)
    .sapling(item('minecraft:sapling:1'))
    .register()

mods.naturesaura.ritual.recipeBuilder()
    .name(resource('example:demo'))
    .input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .time(15)
    .sapling(item('minecraft:sapling:1'))
    .register()

mods.naturesaura.ritual.recipeBuilder()
    .input(item('minecraft:gold_ingot'), item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:clay'), item('minecraft:gold_ingot'), item('minecraft:clay'))
    .output(item('minecraft:diamond') * 16)
    .time(20)
    .sapling(item('minecraft:sapling:3'))
    .register()


// Altar of Birthing:
// Converts multiple input itemstacks into a summoned entity, costing aura and taking time.

mods.naturesaura.spawning.removeByEntity(entity('minecraft:polar_bear'))
mods.naturesaura.spawning.removeByEntity(resource('minecraft:cave_spider'))
mods.naturesaura.spawning.removeByInput(item('minecraft:bone'))
mods.naturesaura.spawning.removeByName(resource('naturesaura:cow'))
// mods.naturesaura.spawning.removeAll()

mods.naturesaura.spawning.recipeBuilder()
    .name('demo')
    .input(item('minecraft:clay'))
    .entity(entity('minecraft:bat'))
    .aura(100)
    .time(100)
    .register()

mods.naturesaura.spawning.recipeBuilder()
    .name(resource('example:demo'))
    .input(item('minecraft:mutton'))
    .entity(entity('minecraft:wolf'))
    .aura(30)
    .time(5)
    .register()

mods.naturesaura.spawning.recipeBuilder()
    .input(item('minecraft:bone'), item('minecraft:dye:15') * 4)
    .entity(resource('minecraft:skeleton'))
    .aura(10)
    .time(10)
    .register()


