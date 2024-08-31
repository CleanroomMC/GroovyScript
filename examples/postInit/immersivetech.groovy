
// Auto generated groovyscript example file
// MODS_LOADED: immersivetech

log.info 'mod \'immersivetech\' detected, running script'

// Boiler:
// Converts an input fluidstack into an output fluidstack after a given amount of time in a multiblock structure when the
// multiblock has had its heat value increased enough by fuel.

mods.immersivetech.boiler.removeByInput(fluid('water'))
mods.immersivetech.boiler.removeByOutput(fluid('steam'))
// mods.immersivetech.boiler.removeAll()

mods.immersivetech.boiler.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .fluidOutput(fluid('hot_spring_water') * 500)
    .time(100)
    .register()

mods.immersivetech.boiler.recipeBuilder()
    .fluidInput(fluid('water') * 50)
    .fluidOutput(fluid('lava') * 50)
    .time(50)
    .register()


// Boiler Fuel:
// Converts an input fluidstack into heat for the Boiler multiblock structure over a given amount of time.

mods.immersivetech.boiler_fuel.removeByInput(fluid('biodiesel'))
// mods.immersivetech.boiler_fuel.removeAll()

mods.immersivetech.boiler_fuel.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .time(100)
    .heat(10)
    .register()

mods.immersivetech.boiler_fuel.recipeBuilder()
    .fluidInput(fluid('water') * 50)
    .time(50)
    .heat(0.05)
    .register()


// Cooling Tower:
// Converts up to two input fluidstacks into up to three output fluidstacks after a given amount of time in a multiblock
// structure.

mods.immersivetech.cooling_tower.removeByInput(fluid('hot_spring_water'))
// mods.immersivetech.cooling_tower.removeByOutput(fluid('water'))
// mods.immersivetech.cooling_tower.removeAll()

mods.immersivetech.cooling_tower.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .fluidOutput(fluid('hot_spring_water') * 500)
    .time(100)
    .register()

mods.immersivetech.cooling_tower.recipeBuilder()
    .fluidInput(fluid('water') * 50, fluid('hot_spring_water') * 50)
    .fluidOutput(fluid('lava') * 50, fluid('water') * 50, fluid('lava') * 50)
    .time(50)
    .register()


// Distiller:
// Converts an input fluidstack into an output fluidstack and has a chance to output an itemstack after a given amount of
// time in a multiblock structure.

// mods.immersivetech.distiller.removeByInput(fluid('water'))
mods.immersivetech.distiller.removeByOutput(fluid('distwater'))
// mods.immersivetech.distiller.removeByOutput(item('immersivetech:material'))
// mods.immersivetech.distiller.removeAll()

mods.immersivetech.distiller.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .fluidOutput(fluid('hot_spring_water') * 500)
    .time(100)
    .register()

mods.immersivetech.distiller.recipeBuilder()
    .fluidInput(fluid('water') * 50)
    .fluidOutput(fluid('lava') * 50)
    .output(item('minecraft:diamond'))
    .chance(0.5f)
    .time(50)
    .energy(5000)
    .register()


// Electrolytic Crucible Battery:
// Converts an input fluidstack into up to three output fluidstacks after a given amount of time and energy in a multiblock
// structure.

mods.immersivetech.electrolytic_crucible_battery.removeByInput(fluid('moltensalt'))
// mods.immersivetech.electrolytic_crucible_battery.removeByOutput(fluid('chlorine'))
// mods.immersivetech.electrolytic_crucible_battery.removeAll()

mods.immersivetech.electrolytic_crucible_battery.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .fluidOutput(fluid('hot_spring_water') * 500)
    .output(item('minecraft:clay'))
    .time(100)
    .register()

mods.immersivetech.electrolytic_crucible_battery.recipeBuilder()
    .fluidInput(fluid('water') * 500)
    .fluidOutput(fluid('lava') * 50, fluid('hot_spring_water') * 50, fluid('water') * 400)
    .output(item('minecraft:diamond'))
    .time(50)
    .energy(5000)
    .register()


// Gas Turbine:
// Converts an input fluidstack into an output fluidstack after a given amount of time in a multiblock structure, producing
// power for an adjacent Alternator multiblock.

mods.immersivetech.gas_turbine.removeByInput(fluid('biodiesel'))
// mods.immersivetech.gas_turbine.removeByOutput(fluid('fluegas'))
// mods.immersivetech.gas_turbine.removeAll()

mods.immersivetech.gas_turbine.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .fluidOutput(fluid('hot_spring_water') * 500)
    .time(100)
    .register()

mods.immersivetech.gas_turbine.recipeBuilder()
    .fluidInput(fluid('water') * 50)
    .fluidOutput(fluid('lava') * 50)
    .time(50)
    .register()


// Heat Exchanger:
// Converts up to two input fluidstacks into up to two output fluidstacks after a given amount of time in a multiblock
// structure.

mods.immersivetech.heat_exchanger.removeByInput(fluid('fluegas'))
mods.immersivetech.heat_exchanger.removeByOutput(fluid('hot_spring_water'))
// mods.immersivetech.heat_exchanger.removeAll()

mods.immersivetech.heat_exchanger.recipeBuilder()
    .fluidInput(fluid('lava') * 100, fluid('lava') * 50)
    .fluidOutput(fluid('hot_spring_water') * 500)
    .time(100)
    .register()

mods.immersivetech.heat_exchanger.recipeBuilder()
    .fluidInput(fluid('water') * 50, fluid('hot_spring_water') * 50)
    .fluidOutput(fluid('lava') * 50, fluid('water') * 10)
    .time(50)
    .energy(5000)
    .register()


// High-Pressure Steam Turbine:
// Converts an input fluidstack into an output fluidstack after a given amount of time in a multiblock structure, producing
// power for an adjacent Alternator multiblock.

mods.immersivetech.high_pressure_steam_turbine.removeByInput(fluid('highpressuresteam'))
// mods.immersivetech.high_pressure_steam_turbine.removeByOutput(fluid('steam'))
// mods.immersivetech.high_pressure_steam_turbine.removeAll()

mods.immersivetech.high_pressure_steam_turbine.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .fluidOutput(fluid('hot_spring_water') * 500)
    .time(100)
    .register()

mods.immersivetech.high_pressure_steam_turbine.recipeBuilder()
    .fluidInput(fluid('water') * 50)
    .fluidOutput(fluid('lava') * 50)
    .time(50)
    .register()


// Melting Crucible:
// Converts an input itemstack into an output fluidstack after a given amount of time and energy in a multiblock structure.

mods.immersivetech.melting_crucible.removeByInput(item('minecraft:cobblestone'))
mods.immersivetech.melting_crucible.removeByOutput(fluid('moltensalt'))
// mods.immersivetech.melting_crucible.removeAll()

mods.immersivetech.melting_crucible.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidOutput(fluid('hot_spring_water'))
    .time(100)
    .register()

mods.immersivetech.melting_crucible.recipeBuilder()
    .input(item('minecraft:clay') * 8)
    .fluidOutput(fluid('lava') * 50)
    .time(50)
    .energy(5000)
    .register()


// Radiator:
// Converts an input fluidstack into an output fluidstack after a given amount of time in a multiblock structure.

mods.immersivetech.radiator.removeByInput(fluid('exhauststeam'))
// mods.immersivetech.radiator.removeByOutput(fluid('distwater'))
// mods.immersivetech.radiator.removeAll()

mods.immersivetech.radiator.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .fluidOutput(fluid('hot_spring_water') * 500)
    .time(100)
    .register()

mods.immersivetech.radiator.recipeBuilder()
    .fluidInput(fluid('water') * 50)
    .fluidOutput(fluid('lava') * 50)
    .time(50)
    .register()


// Solar Tower:
// Converts an input fluidstack into an output fluidstack after a given amount of time in a multiblock structure, with the
// time being able to be sped up via Solar Reflector multiblocks.

mods.immersivetech.solar_tower.removeByInput(fluid('water'))
mods.immersivetech.solar_tower.removeByOutput(fluid('superheatedmoltensodium'))
// mods.immersivetech.solar_tower.removeAll()

mods.immersivetech.solar_tower.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .fluidOutput(fluid('hot_spring_water') * 500)
    .time(100)
    .register()

mods.immersivetech.solar_tower.recipeBuilder()
    .fluidInput(fluid('water') * 50)
    .fluidOutput(fluid('lava') * 50)
    .time(50)
    .register()


// Steam Turbine:
// Converts an input fluidstack into an output fluidstack after a given amount of time in a multiblock structure, producing
// power for an adjacent Alternator multiblock.

mods.immersivetech.steam_turbine.removeByInput(fluid('steam'))
// mods.immersivetech.steam_turbine.removeByOutput(fluid('exhauststeam'))
// mods.immersivetech.steam_turbine.removeAll()

mods.immersivetech.steam_turbine.recipeBuilder()
    .fluidInput(fluid('lava') * 100)
    .fluidOutput(fluid('hot_spring_water') * 500)
    .time(100)
    .register()

mods.immersivetech.steam_turbine.recipeBuilder()
    .fluidInput(fluid('water') * 50)
    .fluidOutput(fluid('lava') * 50)
    .time(50)
    .register()


