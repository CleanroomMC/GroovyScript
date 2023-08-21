package com.cleanroommc.groovyscript.core.mixin.loot;

import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = LootPool.class)
public interface LootPoolAccessor {

    @Accessor
    List<LootEntry> getLootEntries();

}
