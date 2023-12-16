
import com.cleanroommc.groovyscript.event.LootTablesLoadedEvent

event_manager.listen { LootTablesLoadedEvent event ->
    patchStrongholdLibraryLT(event)
    patchChickenLT(event)
    patchZombieLT(event)
    //event.loot.printTables()
    //event.loot.printPools()
    //event.loot.printEntries()
}

def patchStrongholdLibraryLT(event) {
    event.loot.getTable('minecraft:chests/stronghold_library').getPool('main').addEntry(
        event.loot.entryBuilder()
            .name('minecraft:diamond_block')
            .item(item('minecraft:diamond_block'))
            .weight(1)
            .quality(1)
            .build()
    )
    event.loot.printEntries('minecraft:chests/stronghold_library', 'main')
}

def patchChickenLT(event) {
    event.loot.getTable('minecraft:entities/chicken').removePool('main')
    event.loot.getTable('minecraft:entities/chicken').addPool(
        event.loot.poolBuilder()
            .name('main')
            .entry(
                event.loot.entryBuilder()
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

def patchZombieLT(event) {
    event.loot.getTable('minecraft:entities/zombie').removePool('main')
    event.loot.getTable('minecraft:entities/zombie').addPool(
        event.loot.poolBuilder()
            .name('main')
            .entry(
                event.loot.entryBuilder()
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
