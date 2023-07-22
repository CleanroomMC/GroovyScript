
if (isReloading()) {
    println 'Cancelled running script loot'
    return
}

// TODO: `loot` (all lowercase` is a default package in groovy. figure out how to not have this implode (rename to LootTable?)


def pyramidLootTable = Loot.getTable(resource('minecraft:chests/stronghold_library'))

pyramidLootTable.removePool('main')

pyramidLootTable.addPool(
        Loot.poolBuilder('main')
                .entry(
                        Loot.entryBuilder('minecraft:diamond_block')
                                .item(item('minecraft:diamond_block'))
                                .weight(1)
                                .quality(1)
                                .build())
                .randomChance(1.0f)
                .rollsRange(1.0f, 3.0f)
                .bonusRollsRange(0.0f, 0.0f)
                .build()
)




def pyramidLootTable2 = Loot.getTable('minecraft:chests/stronghold_corridor')

pyramidLootTable2.removePool('main')

pyramidLootTable2.addPool(
        Loot.poolBuilder('main')
                .entry(
                        Loot.entryBuilder('minecraft:diamond_block')
                                .item(item('minecraft:diamond_block'))
                                .weight(1)
                                .quality(1)
                                .build())
                .randomChance(1.0f)
                .rollsRange(1.0f, 3.0f)
                .bonusRollsRange(0.0f, 0.0f)
                .build()
)

def chickenLootTable = Loot.getTable('minecraft:entities/chicken')

chickenLootTable.removePool('main')

chickenLootTable.addPool(
        Loot.poolBuilder('main').entry(
                Loot.entryBuilder('minecraft:pumpkin')
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

def zombieLootTable = Loot.getTable('minecraft:entities/zombie')

zombieLootTable.removePool('main')

zombieLootTable.addPool(
        Loot.poolBuilder('main').entry(
                Loot.entryBuilder('minecraft:potato')
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