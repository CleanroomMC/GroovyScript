package com.cleanroommc.groovyscript.compat.loot;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

public class Loot extends NamedRegistry implements IScriptReloadable {

    public Map<ResourceLocation, LootTable> tables = new Object2ObjectOpenHashMap<>();

    // TODO add event shortcut here

    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        tables.clear();
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            for (WorldServer world : server.worlds) {
                world.getLootTableManager().reloadLootTables();
            }
        }
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public void afterScriptLoad() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            for (WorldServer world : server.worlds) {
                world.getLootTableManager().reloadLootTables();
            }
        }
    }

}
