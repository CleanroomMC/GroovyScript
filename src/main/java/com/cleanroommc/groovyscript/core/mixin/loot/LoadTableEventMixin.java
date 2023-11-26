package com.cleanroommc.groovyscript.core.mixin.loot;

import com.cleanroommc.groovyscript.compat.loot.Loot;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ForgeEventFactory.class, remap = false)
public abstract class LoadTableEventMixin {

    @Inject(method = "loadLootTable", at = @At("RETURN"), cancellable = true)
    private static void injection(ResourceLocation name, LootTable table, LootTableManager lootTableManager, CallbackInfoReturnable<LootTable> cir) {
        if (VanillaModule.loot.tables.containsKey(name)) {
            cir.setReturnValue(VanillaModule.loot.tables.get(name));
        } else {
            VanillaModule.loot.tables.put(name, cir.getReturnValue());
        }
    }

}
