package com.cleanroommc.groovyscript.event;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.event.LootTableLoadEvent;

public class LootTableLoadedEvent extends LootTableLoadEvent {
    public LootTableLoadedEvent(ResourceLocation name, LootTable table, LootTableManager lootTableManager) {
        super(name, table, lootTableManager);
    }
}
