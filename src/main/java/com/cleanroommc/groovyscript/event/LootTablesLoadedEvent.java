package com.cleanroommc.groovyscript.event;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.loot.GroovyLootCondition;
import com.cleanroommc.groovyscript.compat.loot.GroovyLootFunction;
import com.cleanroommc.groovyscript.compat.loot.LootEntryBuilder;
import com.cleanroommc.groovyscript.compat.loot.LootPoolBuilder;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.core.mixin.loot.LootPoolAccessor;
import com.cleanroommc.groovyscript.core.mixin.loot.LootTableAccessor;
import groovy.lang.Closure;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.fml.common.eventhandler.Event;

public class LootTablesLoadedEvent extends Event {

    public final Loot loot = new Loot();

    public static class Loot {

        public static final LootTable EMPTY_LOOT_TABLE = new LootTable(new LootPool[0]);

        public LootTable getTable(ResourceLocation name) {
            LootTable lootTable = VanillaModule.loot.tables.get(name);
            if (lootTable == null) GroovyLog.msg("GroovyScript found 0 LootTable(s) named " + name).post();
            return lootTable;
        }

        public LootTable getTable(String name) {
            return getTable(new ResourceLocation(name));
        }

        public LootPoolBuilder poolBuilder() {
            return new LootPoolBuilder();
        }

        public LootEntryBuilder entryBuilder() {
            return new LootEntryBuilder();
        }

        public void printTables() {
            GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootTable(s)");
            VanillaModule.loot.tables.keySet().forEach(table -> out.add(table.toString()));
            if (!out.postIfNotEmpty())
                GroovyLog.msg("GroovyScript found 0 LootTables :thonk:").error().post();
        }

        public void printPools() {
            if (VanillaModule.loot.tables.values().isEmpty()) {
                GroovyLog.msg("GroovyScript found 0 LootTables :thonk:").error().post();
                return;
            }

            GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootPools(s)");

            VanillaModule.loot.tables.forEach((rl, table) -> {
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
            if (VanillaModule.loot.tables.values().isEmpty()) {
                GroovyLog.msg("GroovyScript found 0 LootTables :thonk:").error().post();
                return;
            }

            GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootEntries(s)");

            VanillaModule.loot.tables.forEach((rl, table) -> {
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

}
