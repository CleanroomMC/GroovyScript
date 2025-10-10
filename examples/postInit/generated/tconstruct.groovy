
// Auto generated groovyscript example file
// MODS_LOADED: tconstruct

log.info 'mod \'tconstruct\' detected, running script'

// Alloying:
// Modifies what fluids can be mixed together in the Smeltery.

mods.tconstruct.alloying.removeByInputs(fluid('cobalt')*2,fluid('ardite')*2)
mods.tconstruct.alloying.removeByInputsAndOutput(fluid('knightslime')*72,fluid('iron')*72,fluid('stone')*144,fluid('purpleslime')*125)
mods.tconstruct.alloying.removeByOutput(fluid('pigiron'))
// mods.tconstruct.alloying.removeAll()

mods.tconstruct.alloying.recipeBuilder()
    .fluidOutput(fluid('iron') * 3)
    .fluidInput(fluid('clay') * 1,fluid('lava') * 2)
    .register()


mods.tconstruct.alloying.add(fluid('lava') * 144, fluid('water') * 500, fluid('iron') * 5, fluid('clay') * 60)

// Casting Basin:
// Pours out fluid into a basin to solidify it into a solid, optionally with a cast itemstack.

mods.tconstruct.casting_basin.removeByCast(item('minecraft:planks:0'))
mods.tconstruct.casting_basin.removeByInput(fluid('clay'))
mods.tconstruct.casting_basin.removeByOutput(item('minecraft:iron_block'))
// mods.tconstruct.casting_basin.removeAll()

mods.tconstruct.casting_basin.recipeBuilder()
    .fluidInput(fluid('water'))
    .output(item('minecraft:dirt'))
    .cast(item('minecraft:cobblestone'))
    .coolingTime(40)
    .register()


// Casting Table:
// Pours out fluid onto a table to solidify it into a solid, optionally with a cast itemstack.

mods.tconstruct.casting_table.removeByCast(item('minecraft:bucket'))
mods.tconstruct.casting_table.removeByInput(fluid('iron'))
mods.tconstruct.casting_table.removeByOutput(item('minecraft:gold_ingot'))
// mods.tconstruct.casting_table.removeAll()

mods.tconstruct.casting_table.recipeBuilder()
    .fluidInput(fluid('lava') * 50)
    .output(item('minecraft:diamond'))
    .coolingTime(750)
    .consumesCast(true)
    .cast(ore('gemEmerald'))
    .register()


// Drying Rack:
// Convert an item into a different item by hanging it out to dry.

// mods.tconstruct.drying.removeAll()

mods.tconstruct.drying.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:dirt'))
    .time(45)
    .register()


// Entity Melting:
// Allows mobs to create a bit of fluid when hurt by the Smeltery.

// mods.tconstruct.entity_melting.removeAll()

mods.tconstruct.entity_melting.recipeBuilder()
    .fluidOutput(fluid('iron') * 500)
    .input(resource('minecraft:pig'))
    .register()


// Melting:
// Modifies what items can be melted down in the Smeltery.

// mods.tconstruct.melting.removeAll()

mods.tconstruct.melting.recipeBuilder()
    .input(item('minecraft:gravel'))
    .fluidOutput(fluid('lava') * 25)
    .time(80)
    .register()


// Smeltery Fuel:
// Modifies what fluids are accepted as fuels for the Smeltery and how long each fuels the Smeltery.

// mods.tconstruct.smeltery_fuel.removeAll()

mods.tconstruct.smeltery_fuel.addFuel(fluid('water'), 250)

