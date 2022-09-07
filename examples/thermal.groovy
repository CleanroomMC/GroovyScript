
mods.te.Pulverizer.recipeBuilder()
    .input('<minecraft:bookshelf>')
    .output('<minecraft:diamond>')
    .secondaryOutput('<minecraft:diamond>', 1)
    .energy(2000)
    .register()

mods.te.Pulverizer.streamRecipes()
    .filter{ recipe -> recipe.getSecondaryOutputChance() <= 0 }
    .removeAll()

mods.thermalexpansion.Brewer.recipeBuilder()
    .fluidInput('<fluid:lava>' * 1500)
    .input('<minecraft:gold_ingot>')
    .fluidOutput('<fluid:water>' * 500)
    .register()

mods.thermalexpansion.Crucible.recipeBuilder()
    .input('<minecraft:diamond>')
    .fluidOutput('<fluid:lava>')
    .register()

//mods.thermalexpansion.Crucible.removeByInput('<thermalfoundation:material:1027>')

mods.thermalexpansion.Centrifuge.recipeBuilder()
    .input('<minecraft:diamond>')
    .output('<minecraft:wool:5>', 0.3)
    .output('<minecraft:wool:6>', 0.2)
    .output('<minecraft:wool:7>', 0.1)
    .output('<minecraft:wool:8>', 0.05)
    .fluidOutput('<fluid:water>' * 420)
    .energy(3333)
    .register()

mods.thermalexpansion.Centrifuge.removeByInput('<minecraft:concrete_powder:6>')

mods.thermalexpansion.Charger.recipeBuilder()
    .input('<minecraft:iron_ingot>')
    .output('<minecraft:diamond>')
    .energy(500000)
    .register()

mods.thermalexpansion.Charger.removeByInput('<thermalfoundation:fertilizer:1>')

mods.thermalexpansion.Compactor.recipeBuilder()
    .input('<minecraft:coal>' * 64)
    .output('<minecraft:diamond>')
    .energy(70000)
    .register()

mods.thermalexpansion.Compactor.Coin.recipeBuilder()
    .input('<minecraft:clay_ball>')
    .output('<minecraft:diamond>')
    .register()

mods.thermalexpansion.Compactor.Gear.removeByInput('<ore:ingotEnderium>')