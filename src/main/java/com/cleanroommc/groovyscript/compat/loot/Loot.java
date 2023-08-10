package com.cleanroommc.groovyscript.compat.loot;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.loot.LootPoolAccessor;
import com.cleanroommc.groovyscript.core.mixin.loot.LootTableAccessor;
import groovy.lang.Closure;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.KilledByPlayer;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.conditions.RandomChanceWithLooting;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class Loot {

    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        TABLES.clear();
        TABLE_MANAGER = new LootTableManager(null);
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public void afterScriptRun() {
        if (Minecraft.getMinecraft().isIntegratedServerRunning() && Minecraft.getMinecraft().getIntegratedServer() != null) {
            for (WorldServer world : Minecraft.getMinecraft().getIntegratedServer().worlds) {
                world.getLootTableManager().reloadLootTables();
            }
        }
    }

    public static final Map<ResourceLocation, LootTable> TABLES = new Object2ObjectOpenHashMap<>();
    public static LootTableManager TABLE_MANAGER;

    @ApiStatus.Internal
    @GroovyBlacklist
    public static void init() {
        TABLE_MANAGER = new LootTableManager(null);
    }

    public LootTable getTable(ResourceLocation name) {
        LootTable lootTable = TABLES.get(name);
        if (lootTable == null) GroovyLog.msg("GroovyScript found 0 LootTable(s) named " + name).post();
        return lootTable;
    }

    public LootTable getTable(String name) {
        return getTable(new ResourceLocation(name));
    }

    public void removeTable(ResourceLocation name) {
        TABLES.put(name, LootTable.EMPTY_LOOT_TABLE);
    }

    public void removeTable(String name) {
        TABLES.put(new ResourceLocation(name), LootTable.EMPTY_LOOT_TABLE);
    }

    public LootPoolBuilder poolBuilder() {
        return new LootPoolBuilder();
    }

    public LootPoolBuilder poolBuilder(String name) {
        return new LootPoolBuilder(name);
    }

    public LootEntryBuilder entryBuilder() {
        return new LootEntryBuilder();
    }

    public LootEntryBuilder entryBuilder(String name) {
        return new LootEntryBuilder(name);
    }

    public void printTables() {
        GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootTable(s)");
        TABLES.keySet().forEach(table -> out.add(table.toString()));
        if (!out.postIfNotEmpty())
            GroovyLog.msg("GroovyScript found 0 LootTables :thonk:").error().post();
    }

    public void printPools() {
        if (TABLES.values().size() == 0) {
            GroovyLog.msg("GroovyScript found 0 LootTables :thonk:").error().post();
            return;
        }

        GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootPools(s)");

        TABLES.forEach((rl, table) -> {
            if (((LootTableAccessor) table).getPools() == null || ((LootTableAccessor) table).getPools().size() == 0) {
                return;
            }
            out.add(rl.toString());
            ((LootTableAccessor) table).getPools().forEach(pool -> out.add("\t - " + pool.getName()));
        });

        out.postIfNotEmpty();
    }

    public void printPools(String name) {
        LootTable table = this.getTable(name);
        if (table == null) return;
        GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootPools(s)");
        ((LootTableAccessor) table).getPools().forEach(pool -> out.add(pool.getName()));
        if (!out.postIfNotEmpty())
            GroovyLog.msg("GroovyScript found 0 LootPools in " + name).error().post();
    }

    public void printEntries() {
        if (TABLES.values().size() == 0) {
            GroovyLog.msg("GroovyScript found 0 LootTables :thonk:").error().post();
            return;
        }

        GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootEntries(s)");

        TABLES.forEach((rl, table) -> {
            if (((LootTableAccessor) table).getPools() == null || ((LootTableAccessor) table).getPools().size() == 0) {
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
        if (((LootTableAccessor) table).getPools() == null || ((LootTableAccessor) table).getPools().size() == 0) {
            GroovyLog.msg("GroovyScript found 0 LootPools in " + tableName).error().post();
            return;
        }

        GroovyLog.Msg out = GroovyLog.msg("GroovyScript found the following LootEntry(s)");
        ((LootTableAccessor) table).getPools().forEach(pool -> {
            out.add(pool.getName());
            ((LootPoolAccessor) pool).getLootEntries().forEach(entry -> out.add("\t" + entry.getEntryName()));
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
        ((LootPoolAccessor) pool).getLootEntries().forEach(entry -> out.add(entry.getEntryName()));
        if (!out.postIfNotEmpty())
            GroovyLog.msg("GroovyScript found 0 LootEntries in LootPool " + poolName).error().post();
    }

    public SetAttributesFunction.Modifier attributeModifier(String attrName, String modifName, int operationIn, float min, float max) {
        return this.attributeModifier(attrName, modifName, operationIn, min, max, new EntityEquipmentSlot[0]);
    }

    public SetAttributesFunction.Modifier attributeModifier(String attrName, String modifName, int operationIn, float min, float max, EntityEquipmentSlot slotsIn) {
        return this.attributeModifier(attrName, modifName, operationIn, min, max, new EntityEquipmentSlot[]{slotsIn});
    }

    public SetAttributesFunction.Modifier attributeModifier(String attrName, String modifName, int operationIn, float min, float max, EntityEquipmentSlot... slotsIn) {
        GroovyLog.Msg out = GroovyLog.msg("Error creating Loot Attribute Modifier:");
        out.add(attrName == null || attrName.equals(""), () -> "no attribute type provided");
        out.add(attrName == null || attrName.equals(""), () -> "no modifier type provided");
        out.add(operationIn < 0 || operationIn > 2, () -> "operation number must be between [0,2]");
        if (!out.postIfNotEmpty()) {
            return new SetAttributesFunction.Modifier(attrName, modifName, operationIn, new RandomValueRange(min, max), slotsIn);
        }
        return null;
    }

    public static class Conditions {

        public static LootCondition custom(Closure<Object> customCondition) {
            if (Arrays.equals(customCondition.getParameterTypes(), new Class[]{Random.class, LootContext.class})) {
                return new GroovyLootCondition(customCondition);
            }
            GroovyLog.msg("custom LootConditions require parameters (java.util.Random, net.minecraft.world.storage.loot.LootContext)").error().post();
            return null;
        }

        public static LootCondition randomChance(float chance) {
            GroovyLog.Msg out = GroovyLog.msg("Error creating LootCondition").error();
            out.add(chance < 0.0f || chance > 1.0f, () -> "randomChance must be in range [0,1].");
            out.postIfNotEmpty();
            return new RandomChance(Math.max(0.0f, Math.min(1.0f, chance)));
        }

        public static LootCondition randomChanceWithLooting(float chance, float lootingMultiplier) {
            GroovyLog.Msg out = GroovyLog.msg("Error creating LootCondition").error();
            out.add(chance < 0.0f || chance > 1.0f, () -> "randomChance must be in range [0,1].");
            out.add(lootingMultiplier < 0.0f, () -> "lootingMultiplier cannot be less than 0.");
            out.postIfNotEmpty();
            return new RandomChanceWithLooting(Math.max(0.0f, Math.min(1.0f, chance)), Math.max(lootingMultiplier, 0.0f));
        }

        public static LootCondition killedByPlayer() {
            return new KilledByPlayer(false);
        }

        public static LootCondition killedByNonPlayer() {
            return new KilledByPlayer(true);
        }
    }

}
