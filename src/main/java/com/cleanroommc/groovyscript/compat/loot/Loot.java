package com.cleanroommc.groovyscript.compat.loot;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.core.mixin.loot.LootPoolAccessor;
import com.cleanroommc.groovyscript.core.mixin.loot.LootTableAccessor;
import groovy.lang.Closure;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

public class Loot implements IScriptReloadable {

    public LootTableManager tableManager;
    public Map<ResourceLocation, LootTable> tables = new Object2ObjectOpenHashMap<>();

    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        tables.clear();
        tableManager = new LootTableManager(null);
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

    @ApiStatus.Internal
    @GroovyBlacklist
    public void init() {
        tableManager = new LootTableManager(null);
    }

    public LootTable getTable(ResourceLocation name) {
        LootTable lootTable = tables.get(name);
        if (lootTable == null) GroovyLog.msg("GroovyScript found 0 LootTable(s) named " + name).post();
        return lootTable;
    }

    public LootTable getTable(String name) {
        return getTable(new ResourceLocation(name));
    }

    public void removeTable(ResourceLocation name) {
        tables.put(name, LootTable.EMPTY_LOOT_TABLE);
    }

    public void removeTable(String name) {
        tables.put(new ResourceLocation(name), LootTable.EMPTY_LOOT_TABLE);
    }

    public LootPoolBuilder poolBuilder() {
        return new LootPoolBuilder();
    }

    public LootEntryBuilder entryBuilder() {
        return new LootEntryBuilder();
    }

    public void printTables() {
        GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootTable(s)");
        tables.keySet().forEach(table -> out.add(table.toString()));
        if (!out.postIfNotEmpty())
            GroovyLog.msg("GroovyScript found 0 LootTables :thonk:").error().post();
    }

    public void printPools() {
        if (tables.values().isEmpty()) {
            GroovyLog.msg("GroovyScript found 0 LootTables :thonk:").error().post();
            return;
        }

        GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootPools(s)");

        tables.forEach((rl, table) -> {
            if (((LootTableAccessor) table).getPools() == null || ((LootTableAccessor) table).getPools().isEmpty()) {
                return;
            }
            out.add(rl.toString());
            ((LootTableAccessor) table).getPools().forEach(pool -> out.add("\t - " + pool.getName()));
        });

        out.postIfNotEmpty();
    }

    public void printPools(String tableName) {
        LootTable table = this.getTable(tableName);
        if (table == null) return;
        GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootPools(s)");
        out.add(tableName);
        ((LootTableAccessor) table).getPools().forEach(pool -> out.add("\t - " + pool.getName()));
        if (!out.postIfNotEmpty())
            GroovyLog.msg("GroovyScript found 0 LootPools in " + tableName).error().post();
    }

    public void printEntries() {
        if (tables.values().isEmpty()) {
            GroovyLog.msg("GroovyScript found 0 LootTables :thonk:").error().post();
            return;
        }

        GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootEntries(s)");

        tables.forEach((rl, table) -> {
            if (((LootTableAccessor) table).getPools() == null || ((LootTableAccessor) table).getPools().isEmpty()) {
                return;
            }
            out.add(rl.toString());
            ((LootTableAccessor) table).getPools().forEach(pool -> {
                out.add("\t - " + pool.getName());
                ((LootPoolAccessor) pool).getLootEntries().forEach(entry -> out.add("\t\t - " + entry.getEntryName()));
            });
        });

        out.postIfNotEmpty();
    }

    public void printEntries(String tableName) {
        LootTable table = this.getTable(tableName);
        if (table == null) return;
        if (((LootTableAccessor) table).getPools() == null || ((LootTableAccessor) table).getPools().isEmpty()) {
            GroovyLog.msg("GroovyScript found 0 LootPools in " + tableName).error().post();
            return;
        }

        GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootEntry(s)");
        out.add(tableName);
        ((LootTableAccessor) table).getPools().forEach(pool -> {
            out.add("\t - " + pool.getName());
            ((LootPoolAccessor) pool).getLootEntries().forEach(entry -> out.add("\t\t - " + entry.getEntryName()));
        });
        if (!out.postIfNotEmpty())
            GroovyLog.msg("GroovyScript found 0 LootEntries in LootTable " + tableName).error().post();
    }

    public void printEntries(String tableName, String poolName) {
        LootTable table = this.getTable(tableName);
        if (table == null) return;
        LootPool pool = table.getPool(poolName);
        if (pool == null) {
            GroovyLog.msg("GroovyScript could not find LootPool " + poolName + " in LootTable " + tableName).error().post();
            return;
        }

        GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootEntry(s)");
        out.add(tableName);
        out.add("\t - " + poolName);
        ((LootPoolAccessor) pool).getLootEntries().forEach(entry -> out.add("\t\t - " + entry.getEntryName()));
        if (!out.postIfNotEmpty())
            GroovyLog.msg("GroovyScript found 0 LootEntries in LootPool " + poolName).error().post();
    }

    public GroovyLootFunction function(Closure<Object> function) {
        return new GroovyLootFunction(function);
    }

    public GroovyLootFunction function(Closure<Object> function, LootCondition... conditions) {
        return new GroovyLootFunction(conditions, function);
    }

    public GroovyLootCondition condition(Closure<Object> condition) {
        return new GroovyLootCondition(condition);
    }

}
