
def strongholdLibraryLootTable = loot.getTable(resource('minecraft:chests/stronghold_library'))

def strongholdLibraryMainPool = strongholdLibraryLootTable.getPool('main')

strongholdLibraryMainPool.addEntry(
    loot.entryBuilder()
        .name('minecraft:diamond_block')
        .item(item('minecraft:diamond_block'))
        .weight(1)
        .quality(1)
        .build()
)

def chickenLootTable = loot.getTable('minecraft:entities/chicken')

chickenLootTable.removePool('main')

chickenLootTable.addPool(
    loot.poolBuilder()
        .name('main')
        .entry(
            loot.entryBuilder()
            .item(item('minecraft:diamond'))
            .function{ stack, random, context ->
                stack.setCount(10)
                return stack
            }
            .weight(1)
            .quality(1)
            .build()
        )
        .condition{ random, context -> random.nextFloat() < 0.05f }
        .rollsRange(1.0f, 3.0f)
        .bonusRollsRange(0.0f, 0.0f)
        .build()
)

def zombieLootTable = loot.getTable('minecraft:entities/zombie')

zombieLootTable.removePool('main')

zombieLootTable.addPool(
    loot.poolBuilder()
        .name('main')
        .entry(
            loot.entryBuilder()
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
Loot.printEntries('minecraft:chests/stronghold_library', 'main')
