//println("Hello from groovy")

def map = [test: 2, test2: 3]

println map
println map.getClass()

/*recipes.addShaped("test", '<minecraft:bucket>', [
        [null, null, null],
        [null, '<thermalexpansion:capacitor:*>'.withNbt([Energy: 1000000]), null],
        [null, null, null]
])

recipes.remove('thermalexpansion:capacitor_10')
recipes.remove('thermalexpansion:capacitor_11')*/

//ItemStack item = '<minecraft:diamond>'
//println("Count: " + item.getCount().toString())
//println("Meta: " + item.getMetadata().toString())

/*def netherStarTransformer = { netherStar ->
    return '<minecraft:diamond>'
}

recipes.addShaped("test_name", '<minecraft:clay_ball>' * 3, [
        ['<minecraft:nether_star>'.transform(netherStarTransformer), '<fluid:water>' * 1000, '<minecraft:nether_star>'.transform(netherStarTransformer)],
        [null, '<ore:ingotIron>', null],
        [item('minecraft:nether_star').transform(netherStarTransformer), null, '<minecraft:nether_star>'.transform(netherStarTransformer)]
])

//recipes.addShapeless('test_shapeless', '<minecraft:diamond>' * 2, ['<minecraft:clay_ball>', '<minecraft:diamond>'])
//recipes.addShapeless('test_shapeless2', '<minecraft:diamond>' * 2, ['<minecraft:clay_ball>', '<minecraft:gold_ingot>'])

//recipes.remove('minecraft:clay')

// mekanism
//mods.mekanism.crusher.add '<minecraft:clay_ball>', '<minecraft:nether_star>'
//mods.mekanism.crusher.remove '<minecraft:wool:2>', "<minecraft:string>"



events.world.onBlockBreak { world, blockState, pos, player ->
    player.sendMessage(new TextComponentString("Block broken"))
    false
}

events.entity.onEnderTeleport { event ->
    event.setAttackDamage 19.5f
}*/

