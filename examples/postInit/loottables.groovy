
if (isReloading()) {
    println 'Cancelled running script loot'
    return
}

def pyramidLootTable = loot.getTable(resource('minecraft:chests/stronghold_library'))

pyramidLootTable.removePool('main')

pyramidLootTable.addPool(
        loot.poolBuilder('main')
                .entry(
                        loot.entryBuilder('minecraft:diamond_block')
                                .item(item('minecraft:diamond_block'))
                                .weight(1)
                                .quality(1)
                                .build())
                .randomChance(1.0f)
                .rollsRange(1.0f, 3.0f)
                .bonusRollsRange(0.0f, 0.0f)
                .build()
)




def pyramidLootTable2 = loot.getTable('minecraft:chests/stronghold_corridor')

pyramidLootTable2.removePool('main')

pyramidLootTable2.addPool(
        loot.poolBuilder('main')
                .entry(
                        loot.entryBuilder('minecraft:diamond_block')
                                .item(item('minecraft:diamond_block'))
                                .weight(1)
                                .quality(1)
                                .build())
                .randomChance(1.0f)
                .rollsRange(1.0f, 3.0f)
                .bonusRollsRange(0.0f, 0.0f)
                .build()
)

def chickenLootTable = loot.getTable('minecraft:entities/chicken')

chickenLootTable.removePool('main')

chickenLootTable.addPool(
        loot.poolBuilder('main').entry(
                loot.entryBuilder('minecraft:pumpkin')
                        .item(item('minecraft:pumpkin'))
                        .weight(1)
                        .quality(1)
                        .build()
        )
                .randomChance(1.0f)
                .rollsRange(1.0f, 3.0f)
                .bonusRollsRange(0.0f, 0.0f)
                .build()
)

def zombieLootTable = loot.getTable('minecraft:entities/zombie')

zombieLootTable.removePool('main')

zombieLootTable.addPool(
        loot.poolBuilder('main').entry(
                loot.entryBuilder('minecraft:potato')
                        .item(item('minecraft:potato'))
                        .weight(1)
                        .quality(1)
                        .smelt()
                        .build()
        )
                .randomChance(1.0f)
                .killedByNonPlayer()
                .rollsRange(1.0f, 3.0f)
                .bonusRollsRange(0.0f, 0.0f)
                .build()
)

//Loot.printEntries()