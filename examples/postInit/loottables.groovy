
import com.cleanroommc.groovyscript.event.LootTablesLoadedEvent

event_manager.listen { LootTablesLoadedEvent event ->
    patchStrongholdLibraryLT()
    patchChickenLT()
    patchZombieLT()
    //loot.printTables()
    //loot.printPools()
    //loot.printEntries()
}

def patchStrongholdLibraryLT() {
    loot.getTable('minecraft:chests/stronghold_library').getPool('main').addEntry(
        loot.entryBuilder()
            .name('minecraft:diamond_block')
            .item(item('minecraft:diamond_block'))
            .weight(1)
            .quality(1)
            .build()
    )
    loot.printEntries('minecraft:chests/stronghold_library', 'main')
}

def patchChickenLT() {
    loot.getTable('minecraft:entities/chicken').removePool('main')
    loot.getTable('minecraft:entities/chicken').addPool(
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
}

def patchZombieLT() {
    loot.getTable('minecraft:entities/zombie').removePool('main')
    loot.getTable('minecraft:entities/zombie').addPool(
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
}
