
// MODS_LOADED: thermalexpansion
println 'mod \'thermalexpansion\' detected, running script'

mods.te.Pulverizer.recipeBuilder()
    .input(item('minecraft:bookshelf'))
    .output(item('minecraft:diamond'))
    .secondaryOutput(item('minecraft:diamond'), 1)
    .energy(2000)
    .register()

mods.te.Pulverizer.streamRecipes()
    .filter{ recipe -> recipe.getSecondaryOutputChance() <= 0 }
    .removeAll()

mods.thermalexpansion.Brewer.recipeBuilder()
    .fluidInput(fluid('lava') * 1500)
    .input(item('minecraft:gold_ingot'))
    .fluidOutput(fluid('water') * 500)
    .register()

mods.thermalexpansion.Crucible.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidOutput(fluid('lava'))
    .register()

//mods.thermalexpansion.Crucible.removeByInput(item('thermalfoundation:material:1027'))

/*
mods.thermalexpansion.Centrifuge.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:wool:5'), 0.3)
    .output(item('minecraft:wool:6'), 0.2)
    .output(item('minecraft:wool:7'), 0.1)
    .output(item('minecraft:wool:8'), 0.05)
    .fluidOutput(fluid('water') * 420)
    .energy(3333)
    .register()

mods.thermalexpansion.Centrifuge.removeByInput(item('minecraft:concrete_powder:6'))


mods.thermalexpansion.Charger.recipeBuilder()
    .input(item('minecraft:iron_ingot'))
    .output(item('minecraft:diamond'))
    .energy(500000)
    .register()

mods.thermalexpansion.Charger.removeByInput(item('thermalfoundation:fertilizer:1'))

mods.thermalexpansion.Compactor.recipeBuilder()
    .input(item('minecraft:coal') * 64)
    .output(item('minecraft:diamond'))
    .energy(70000)
    .register()

mods.thermalexpansion.Compactor.Coin.recipeBuilder()
    .input(item('minecraft:clay_ball'))
    .output(item('minecraft:diamond'))
    .register()

mods.thermalexpansion.Compactor.Gear.removeByInput(ore('ingotEnderium'))
 */