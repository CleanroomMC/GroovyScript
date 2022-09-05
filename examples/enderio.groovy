
// enderio
//mods.enderio.alloySmelter.add('<minecraft:nether_star>', ['<minecraft:clay_ball>', '<minecraft:gold_ingot>', '<minecraft:dirt>'], 9999)
//mods.enderio.alloySmelter.remove('<enderio:item_alloy_ingot:0>')

mods.enderio.AlloySmelter.recipeBuilder()
        .input('<minecraft:clay_ball>', '<minecraft:gold_ingot>', '<minecraft:dirt>')
        .output('<minecraft:nether_star>')
        .energy(9999)
        .register()

mods.enderio.SoulBinder.recipeBuilder()
        .input('<minecraft:clay_ball>')
        .output('<minecraft:nether_star>')
        .entitySoul('minecraft:zombie')
        .xp(20)
        .energy(9999)
        .register()

//mods.enderio.soulBinder.add('balanceness', '<minecraft:nether_star>', '<minecraft:clay>', ['minecraft:wither'], 9999, 20)
mods.enderio.SoulBinder.remove('<enderio:item_material:16>')

mods.enderio.SagMill.recipeBuilder()
        .input('<minecraft:clay_ball>')
        .output('<minecraft:iron_ingot>', 0.3f)
        .output('<minecraft:gold_ingot>', 0.15f)
        .output('<minecraft:diamond>', 0.05f)
        .output('<minecraft:diamond>', 0.05f)
        .bonusTypeMultiply()
        .energy(6000)
        .tierSimple()
        .register()

//mods.enderio.sagMill.removeByInput '<minecraft:wheat>'

mods.enderio.SliceNSplice.add('<minecraft:totem_of_undying>', ['<minecraft:diamond>', '<minecraft:gold_ingot>', '<minecraft:diamond>', null, '<minecraft:nether_star>', null], 9999)
//mods.enderio.SliceNSplice.remove('<enderio:block_enderman_skull:2>')
//mods.enderio.SliceNSplice.removeByInput(['<enderio:item_alloy_ingot:7>', '<enderio:block_enderman_skull>', '<enderio:item_alloy_ingot:7>', '<minecraft:potion>', '<enderio:item_basic_capacitor>', '<minecraft:potion>'])

//mods.enderio.FluidFuel.addFuel(fluid('water'), 1000, 100)
//mods.enderio.FluidFuel.addCoolant(fluid('water'), 10f)

mods.enderio.Enchanter.remove('<enchantment:efficiency>')
mods.enderio.Enchanter.recipeBuilder()
        .enchantment('<enchantment:efficiency>')
        .input('<minecraft:nether_star>' * 2)
        .xpCostMultiplier(3f)
        .customBook('<enderio:item_dark_steel_upgrade:0>')
        .customLapis('<minecraft:poisonous_potato>')
        .register()

mods.enderio.Vat.recipeBuilder()
        .input('<fluid:water>' * 500)
        .output('<fluid:lava>' * 500)
        .itemInputLeft('<minecraft:diamond>', 1.5)
        .itemInputRight('<minecraft:iron_ingot>', 0.7)
        .itemInputRight('<minecraft:gold_ingot>', 7)
        .baseMultiplier(1)
        .register()

mods.enderio.Vat.remove('<fluid:nutrient_distillation>')

mods.enderio.Tank.removeDrain('<fluid:water>', '<minecraft:sponge:0>')
mods.enderio.Tank.addFill('<minecraft:clay_ball>', '<fluid:lava>' * 10000, '<minecraft:nether_star>')
