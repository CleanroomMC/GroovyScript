
// enderio
//mods.enderio.alloySmelter.add(item('minecraft:nether_star'), [item('minecraft:clay_ball'), item('minecraft:gold_ingot'), item('minecraft:dirt')], 9999)
//mods.enderio.alloySmelter.remove(item('enderio:item_alloy_ingot0)>')

mods.enderio.AlloySmelter.recipeBuilder()
        .input(item('minecraft:clay_ball'), item('minecraft:gold_ingot'), item('minecraft:dirt'))
        .output(item('minecraft:nether_star'))
        .energy(9999)
        .register()

mods.enderio.SoulBinder.recipeBuilder()
        .input(item('minecraft:clay_ball'))
        .output(item('minecraft:nether_star'))
        .entitySoul('minecraft:zombie')
        .xp(20)
        .energy(9999)
        .register()

//mods.enderio.soulBinder.add('balanceness', item('minecraft:nether_star'), item('minecraft:clay'), ['minecraft:wither'], 9999, 20)
mods.enderio.SoulBinder.remove(item('enderio:item_material:16'))

mods.enderio.SagMill.recipeBuilder()
        .input(item('minecraft:clay_ball'))
        .output(item('minecraft:iron_ingot'), 0.3f)
        .output(item('minecraft:gold_ingot'), 0.15f)
        .output(item('minecraft:diamond'), 0.05f)
        .output(item('minecraft:diamond'), 0.05f)
        .bonusTypeMultiply()
        .energy(6000)
        .tierSimple()
        .register()

//mods.enderio.sagMill.removeByInput item('minecraft:wheat')

mods.enderio.SliceNSplice.add(item('minecraft:totem_of_undying'), [item('minecraft:diamond'), item('minecraft:gold_ingot'), item('minecraft:diamond'), null, item('minecraft:nether_star'), null], 9999)
//mods.enderio.SliceNSplice.remove(item('enderio:block_enderman_skull2)>')
//mods.enderio.SliceNSplice.removeByInput([item('enderio:item_alloy_ingot7)>', item('enderio:block_enderman_skull'), item('enderio:item_alloy_ingot7)>', item('minecraft:potion'), item('enderio:item_basic_capacitor'), item('minecraft:potion')])

//mods.enderio.FluidFuel.addFuel(fluid('water'), 1000, 100)
//mods.enderio.FluidFuel.addCoolant(fluid('water'), 10f)

mods.enderio.Enchanter.remove(item('enchantment:efficiency'))
mods.enderio.Enchanter.recipeBuilder()
        .enchantment(item('enchantment:efficiency'))
        .input(item('minecraft:nether_star') * 2)
        .xpCostMultiplier(3f)
        .customBook(item('enderio:item_dark_steel_upgrade:0'))
        .customLapis(item('minecraft:poisonous_potato'))
        .register()

mods.enderio.Vat.recipeBuilder()
        .input(fluid('water') * 500)
        .output(fluid('lava') * 500)
        .itemInputLeft(item('minecraft:diamond'), 1.5)
        .itemInputRight(item('minecraft:iron_ingot'), 0.7)
        .itemInputRight(item('minecraft:gold_ingot'), 7)
        .baseMultiplier(1)
        .register()

mods.enderio.Vat.remove(fluid('nutrient_distillation'))

mods.enderio.Tank.removeDrain(fluid('water'), item('minecraft:sponge:0'))
mods.enderio.Tank.addFill(item('minecraft:clay_ball'), fluid('lava') * 10000, item('minecraft:nether_star'))
