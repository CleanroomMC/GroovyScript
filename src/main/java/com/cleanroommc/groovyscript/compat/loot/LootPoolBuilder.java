package com.cleanroommc.groovyscript.compat.loot;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.KilledByPlayer;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.conditions.RandomChanceWithLooting;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public class LootPoolBuilder {

    private String name;
    private final List<LootEntry> lootEntries = new ArrayList<>();
    private final List<LootCondition> poolConditions = new ArrayList<>();
    private RandomValueRange rolls = new RandomValueRange(1);
    private RandomValueRange bonusRolls = new RandomValueRange(0);
    private ResourceLocation tableName;
    private final GroovyLog.Msg out = GroovyLog.msg("Error creating GroovyScript LootPool").warn();

    public LootPoolBuilder name(String name) {
        this.name = name;
        return this;
    }

    public LootPoolBuilder name(ResourceLocation name) {
        this.name = name.toString();
        return this;
    }

    public LootPoolBuilder table(String table) {
        this.tableName = new ResourceLocation(table);
        return this;
    }

    public LootPoolBuilder table(ResourceLocation table) {
        this.tableName = table;
        return this;
    }

    public LootPoolBuilder entry(LootEntry entry) {
        this.lootEntries.add(entry);
        return this;
    }

    public LootPoolBuilder entry(ItemStack stack, int weight) {
        this.lootEntries.add(new LootEntryBuilder()
                                     .name(stack.getItem().getRegistryName().getNamespace() + ":" + stack.getMetadata())
                                     .item(stack)
                                     .weight(weight).build());
        return this;
    }

    public LootPoolBuilder randomChance(float chance) {
        out.add(chance < 0.0f || chance > 1.0f, () -> "randomChance must be in range [0,1].");
        this.poolConditions.add(new RandomChance(chance));
        return this;
    }

    public LootPoolBuilder randomChanceWithLooting(float chance, float lootingMultiplier) {
        out.add(chance < 0.0f || chance > 1.0f, () -> "randomChance must be in range [0,1].");
        out.add(lootingMultiplier < 0.0f, () -> "lootingMultiplier cannot be less than 0.");
        this.poolConditions.add(new RandomChanceWithLooting(chance, lootingMultiplier));
        return this;
    }

    public LootPoolBuilder killedByPlayer() {
        this.poolConditions.add(new KilledByPlayer(false));
        return this;
    }

    public LootPoolBuilder killedByNonPlayer() {
        this.poolConditions.add(new KilledByPlayer(true));
        return this;
    }

    public LootPoolBuilder condition(LootCondition condition) {
        this.poolConditions.add(condition);
        return this;
    }

    public LootPoolBuilder condition(Closure<Object> customCondition) {
        this.poolConditions.add(new GroovyLootCondition(customCondition));
        return this;
    }

    public LootPoolBuilder rollsRange(float value) {
        return rollsRange(value, value);
    }

    public LootPoolBuilder rollsRange(float min, float max) {
        out.add(min < 0.0f, () -> "rollRange minimum cannot be less than 0.");
        out.add(max < 0.0f, () -> "rollRange maximum cannot be less than 0.");
        this.rolls = new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f));
        return this;
    }

    public LootPoolBuilder bonusRollsRange(float value) {
        return rollsRange(value, value);
    }

    public LootPoolBuilder bonusRollsRange(float min, float max) {
        out.add(min < 0.0f, () -> "rollRange minimum cannot be less than 0.");
        out.add(max < 0.0f, () -> "rollRange maximum cannot be less than 0.");
        this.bonusRolls = new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f));
        return this;
    }

    private boolean validate(boolean validateForRegister) {
        if (name == null || name.isEmpty()) out.add("No name provided").error();
        if (validateForRegister) {
            if (tableName == null || !VanillaModule.loot.tables.containsKey(tableName)) out.add("No valid LootTable specified").error();
            else if (name == null || name.isEmpty()) out.add("LootPool must have a name specified with .name()").error();
            else if (VanillaModule.loot.tables.get(tableName).getPool(name) != null) out.add("Attempted to add duplicate pool " + name + " to " + tableName).error();
        }
        out.postIfNotEmpty();
        return out.getLevel() != Level.ERROR;
    }

    public LootPool build() {
        if (!validate(false)) return null;
        return new LootPool(lootEntries.toArray(new LootEntry[0]), poolConditions.toArray(new LootCondition[0]), rolls, bonusRolls, name);
    }

    public void register() {
        if (!validate(true)) return;
        VanillaModule.loot.tables.get(tableName).addPool(
                new LootPool(lootEntries.toArray(new LootEntry[0]), poolConditions.toArray(new LootCondition[0]), rolls, bonusRolls, name)
        );
    }

}