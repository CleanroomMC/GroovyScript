package com.cleanroommc.groovyscript.compat.loot;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.ingredient.NbtHelper;
import com.google.common.collect.Lists;
import groovy.lang.Closure;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.KilledByPlayer;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.conditions.RandomChanceWithLooting;
import net.minecraft.world.storage.loot.functions.*;
import org.apache.logging.log4j.Level;

import java.util.*;

public class LootEntryBuilder {

    public static final LootCondition[] EMPTY_CONDITIONS = {};

    private String name;
    private Item item;
    private int weight = 1;
    private int quality;
    private final List<LootFunction> functions = new ArrayList<>();
    private final List<LootCondition> conditions = new ArrayList<>();
    private ResourceLocation tableName;
    private String poolName;
    private final GroovyLog.Msg out = GroovyLog.msg("Error creating GroovyScript LootPool").warn();

    public LootEntryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public LootEntryBuilder name(ResourceLocation name) {
        this.name = name.toString();
        return this;
    }

    public LootEntryBuilder item(Item item) {
        this.item = item;
        return this;
    }

    public LootEntryBuilder item(ItemStack stack) {
        this.item = stack.getItem();
        if (stack.getMetadata() != 0) {
            setMetadata(stack.getMetadata(), stack.getMetadata());
        }
        if (stack.hasTagCompound()) {
            setNBT(stack.getTagCompound());
        }
        return this;
    }

    public LootEntryBuilder weight(int weight) {
        this.weight = weight;
        return this;
    }

    public LootEntryBuilder quality(int quality) {
        this.quality = quality;
        return this;
    }

    public LootEntryBuilder table(String table) {
        this.tableName = new ResourceLocation(table);
        return this;
    }

    public LootEntryBuilder table(ResourceLocation table) {
        this.tableName = table;
        return this;
    }

    public LootEntryBuilder pool(String pool) {
        this.poolName = pool;
        return this;
    }

    public LootEntryBuilder pool(ResourceLocation pool) {
        this.poolName = pool.toString();
        return this;
    }

    //===========================LOOT FUNCTIONS===================================

    public LootEntryBuilder function(LootFunction function) {
        this.functions.add(function);
        return this;
    }

    public LootEntryBuilder function(Closure<Object> function) {
        this.functions.add(new GroovyLootFunction(function));
        return this;
    }

    public LootEntryBuilder function(Closure<Object> function, LootCondition condition) {
        return this.function(function, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder function(Closure<Object> function, LootCondition... conditions) {
        this.functions.add(new GroovyLootFunction(conditions, function));
        return this;
    }

    public LootEntryBuilder enchantWithLevels(boolean isTreasure, int min, int max) {
        return this.enchantWithLevels(isTreasure, min, max, new LootCondition[0]);
    }

    public LootEntryBuilder enchantWithLevels(boolean isTreasure, int min, int max, LootCondition... conditions) {
        out.add(min < 0.0f, () -> "enchantWithLevels minimum cannot be less than 0.");
        out.add(max < 0.0f, () -> "enchantWithLevels maximum cannot be less than 0.");
        this.functions.add(new EnchantWithLevels(conditions, new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f)), isTreasure));
        return this;
    }

    public LootEntryBuilder enchantRandomly() {
        return this.enchantRandomly((Enchantment[]) null, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder enchantRandomly(Enchantment... enchantments) {
        return this.enchantRandomly(enchantments, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder enchantRandomly(LootCondition... conditions) {
        return this.enchantRandomly((Enchantment[]) null, conditions);
    }

    public LootEntryBuilder enchantRandomly(Enchantment enchantment, LootCondition... condidtions) {
        return this.enchantRandomly();
    }

    public LootEntryBuilder enchantRandomly(Enchantment[] enchantments, LootCondition... conditions) {
        List<Enchantment> list = (enchantments != null) ? Lists.newArrayList(enchantments) : null;
        this.functions.add(new EnchantRandomly((conditions == null)? EMPTY_CONDITIONS : conditions, list));
        return this;
    }

    public LootEntryBuilder lootingBonus(float min, float max) {
        return this.lootingBonus(min, max, (int) max, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder lootingBonus(float min, float max, int limit) {
        return this.lootingBonus(min, max, limit, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder lootingBonus(float min, float max, LootCondition... conditions) {
        return this.lootingBonus(min, max, (int) max, conditions);
    }

    public LootEntryBuilder lootingBonus(float min, float max, int limit, LootCondition... conditions) {
        out.add(min < 0.0f, () -> "lootingEnchantBonus minimum cannot be less than 0.");
        out.add(max < 0.0f, () -> "lootingEnchantBonus maximum cannot be less than 0.");
        out.add(limit < 0, () -> "lootingEnchantBonus limit cannot be less than 0.");
        this.functions.add(new LootingEnchantBonus((conditions == null)? EMPTY_CONDITIONS : conditions, new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f)), Math.max(limit, 0)));
        return this;
    }

    public LootEntryBuilder setDamage(int dmg) {
        return this.setDamage(dmg, dmg, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder setDamage(int dmg, LootCondition... conditions) {
        return this.setDamage(dmg, dmg, conditions);
    }

    public LootEntryBuilder setDamage(int min, int max) {
        return this.setDamage(min, max, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder setDamage(int min, int max, LootCondition... conditions) {
        out.add(min < 0 || min >= 32767, () -> "setDamage minimum cannot be less than 0 or more than 32766.");
        out.add(max < 0 || min >= 32767, () -> "setDamage maximum cannot be less than 0 or more than 32766.");
        this.functions.add(new SetDamage((conditions == null)? EMPTY_CONDITIONS : conditions, new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f))));
        return this;
    }

    public LootEntryBuilder setCount(int count) {
        return this.setCount(count, count, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder setCount(int count, LootCondition... conditions) {
        return this.setCount(count, count, conditions);
    }

    public LootEntryBuilder setCount(int min, int max) {
        return this.setCount(min, max, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder setCount(int min, int max, LootCondition... conditions) {
        out.add(min < 0, () -> "setCount minimum cannot be less than 0.");
        out.add(max < 0, () -> "setCount maximum cannot be less than 0.");
        this.functions.add(new SetCount((conditions == null)? EMPTY_CONDITIONS : conditions, new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f))));
        return this;
    }

    public LootEntryBuilder setMetadata(int meta) {
        return this.setMetadata(meta, meta, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder setMetadata(int meta, LootCondition... conditions) {
        return this.setMetadata(meta, meta, conditions);
    }

    public LootEntryBuilder setMetadata(int min, int max) {
        return this.setMetadata(min, max, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder setMetadata(int min, int max, LootCondition... conditions) {
        out.add(min < 0 || min >= 32767, () -> "setMetadata minimum cannot be less than 0 or more than 32766.");
        out.add(max < 0 || min >= 32767, () -> "setMetadata maximum cannot be less than 0 or more than 32766.");
        this.functions.add(new SetMetadata((conditions == null)? EMPTY_CONDITIONS : conditions, new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f))));
        return this;
    }

    public LootEntryBuilder setNBT(String tag) {
        try {
            return this.setNBT(JsonToNBT.getTagFromJson(tag), EMPTY_CONDITIONS);
        } catch (NBTException e) {
            out.add("could not parse nbt string");
            return this;
        }
    }

    public LootEntryBuilder setNBT(Map<String, Object> tag) {
        return this.setNBT(NbtHelper.ofMap(tag), EMPTY_CONDITIONS);
    }

    public LootEntryBuilder setNBT(NBTTagCompound tag) {
        return this.setNBT(tag, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder setNBT(String tag, LootCondition... conditions) {
        try {
            return this.setNBT(JsonToNBT.getTagFromJson(tag), conditions);
        } catch (NBTException e) {
            out.add("could not parse nbt string");
            return this;
        }
    }

    public LootEntryBuilder setNBT(Map<String, Object> tag, LootCondition... conditions) {
        return this.setNBT(NbtHelper.ofMap(tag), conditions);
    }

    public LootEntryBuilder setNBT(NBTTagCompound tag, LootCondition... conditions) {
        this.functions.add(new SetNBT((conditions == null)? EMPTY_CONDITIONS : conditions, tag));
        return this;
    }

    public LootEntryBuilder smelt() {
        return this.smelt(EMPTY_CONDITIONS);
    }

    public LootEntryBuilder smelt(LootCondition... conditions) {
        this.functions.add(new Smelt(conditions));
        return this;
    }

    //===========================LOOT CONDITIONS===================================

    public LootEntryBuilder randomChance(float chance) {
        out.add(chance < 0.0f || chance > 1.0f, () -> "randomChance must be in range [0,1].");
        this.conditions.add(new RandomChance(Math.max(0.0f, Math.min(1.0f, chance))));
        return this;
    }

    public LootEntryBuilder randomChanceWithLooting(float chance, float lootingMultiplier) {
        out.add(chance < 0.0f || chance > 1.0f, () -> "randomChance must be in range [0,1].");
        out.add(lootingMultiplier < 0.0f, () -> "lootingMultiplier cannot be less than 0.");
        this.conditions.add(new RandomChanceWithLooting(Math.max(0.0f, Math.min(1.0f, chance)), Math.max(lootingMultiplier, 0.0f)));
        return this;
    }

    public LootEntryBuilder killedByPlayer() {
        this.conditions.add(new KilledByPlayer(false));
        return this;
    }

    public LootEntryBuilder killedByNonPlayer() {
        this.conditions.add(new KilledByPlayer(true));
        return this;
    }

    public LootEntryBuilder condition(LootCondition condition) {
        this.conditions.add(condition);
        return this;
    }

    public LootEntryBuilder condition(Closure<Object> customCondition) {
        this.conditions.add(new GroovyLootCondition(customCondition));
        return this;
    }

    //===========================BASE===================================

    private boolean validate(boolean validateForRegister) {
        if (item == null) out.add("No item provided.").error();
        else if (name == null || name.isEmpty()) {
            if (item.getRegistryName() != null) name = item.getRegistryName().toString();
        }
        if (name == null || name.isEmpty()) out.add("No name provided").error();
        if (weight <= 0) out.add("weight <= 0 may make the loot entry unable to be rolled");
        if (quality < 0) out.add("quality < 0 may make the loot entry unable to be rolled");

        if (validateForRegister) {
            if (tableName == null || VanillaModule.loot.tables.get(tableName) == null) out.add("No valid LootTable specified").error();
            else if (poolName == null || poolName.isEmpty() || VanillaModule.loot.tables.get(tableName).getPool(poolName) == null) out.add("No valid LootPool specified").error();
            else if (VanillaModule.loot.tables.get(tableName).getPool(poolName).getEntry(name) != null) out.add("Attempted to add duplicate entry " + name + " to " + tableName + " - " + poolName);
        }

        out.postIfNotEmpty();
        return out.getLevel() != Level.ERROR;
    }

    public LootEntry build() {
        if (!validate(false)) return null;
        return new LootEntryItem(item, weight, quality, functions.toArray(new LootFunction[0]), conditions.toArray(new LootCondition[0]), name);
    }

    public void register() {
        if (!validate(true)) return;
        VanillaModule.loot.tables.get(tableName).getPool(poolName).addEntry(
                new LootEntryItem(item, weight, quality, functions.toArray(new LootFunction[0]), conditions.toArray(new LootCondition[0]), name)
        );
    }

}
