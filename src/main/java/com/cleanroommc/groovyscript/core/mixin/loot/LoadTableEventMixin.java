package com.cleanroommc.groovyscript.core.mixin.loot;

import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.event.LootTablesLoadedEvent;
import com.google.common.cache.LoadingCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LootTableManager.class)
public abstract class LoadTableEventMixin {

    @Shadow
    @Final
    private LoadingCache<ResourceLocation, LootTable> registeredLootTables;

    @Inject(method = "reloadLootTables", at = @At("RETURN"))
    private void injection(CallbackInfo ci) {
        VanillaModule.loot.tables.putAll(this.registeredLootTables.asMap());
        MinecraftForge.EVENT_BUS.post(new LootTablesLoadedEvent());
    }

}
