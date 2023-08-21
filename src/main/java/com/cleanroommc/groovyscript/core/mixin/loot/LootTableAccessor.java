package com.cleanroommc.groovyscript.core.mixin.loot;

import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = LootTable.class)
public interface LootTableAccessor {

    @Accessor
    List<LootPool> getPools();

}
