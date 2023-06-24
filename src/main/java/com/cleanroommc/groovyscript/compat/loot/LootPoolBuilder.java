package com.cleanroommc.groovyscript.compat.loot;

import com.cleanroommc.groovyscript.api.GroovyLog;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.KilledByPlayer;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.conditions.RandomChanceWithLooting;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LootPoolBuilder {

    private String name;
    private final List<LootEntry> lootEntries = new ArrayList<>();
    private final List<LootCondition> poolConditions = new ArrayList<>();
    private RandomValueRange rolls;
    private RandomValueRange bonusRolls;
    private final GroovyLog.Msg out = GroovyLog.msg("Error creating GroovyScript LootPool").warn();

    public LootPoolBuilder() {
    }

    public LootPoolBuilder(String name) {
        this.name = name;
    }

    public LootPoolBuilder name(String name) {
        this.name = name;
        return this;
    }

    public LootPoolBuilder entry(LootEntry entry) {
        this.lootEntries.add(entry);
        return this;
    }

    public LootPoolBuilder entry(ItemStack stack, int weight) {
        this.lootEntries.add(new LootEntryBuilder(stack.getItem().getRegistryName().getNamespace() + ":" + stack.getMetadata())
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
        if (Arrays.equals(customCondition.getParameterTypes(), new Class[]{Random.class, LootContext.class})) {
            this.poolConditions.add(new GroovyLootCondition(customCondition));
        } else {
            out.add("custom LootConditions require parameters (java.util.Random, net.minecraft.world.storage.loot.LootContext)");
        }

        return this;
    }

    public LootPoolBuilder rollsRange(float min, float max) {
        out.add(min < 0.0f, () -> "rollRange minimum cannot be less than 0.");
        out.add(max < 0.0f, () -> "rollRange maximum cannot be less than 0.");
        this.rolls = new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f));
        return this;
    }

    public LootPoolBuilder bonusRollsRange(float min, float max) {
        out.add(min < 0.0f, () -> "rollRange minimum cannot be less than 0.");
        out.add(max < 0.0f, () -> "rollRange maximum cannot be less than 0.");
        this.bonusRolls = new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f));
        return this;
    }

    private boolean validate() {
        if (name == null || name.isEmpty()) out.add("No name provided").error();
        out.postIfNotEmpty();
        return out.getLevel() != Level.ERROR;
    }

    public LootPool build() {
        return new LootPool(lootEntries.toArray(new LootEntry[0]), poolConditions.toArray(new LootCondition[0]), rolls, bonusRolls, name);
    }

}