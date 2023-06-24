package com.cleanroommc.groovyscript.compat.loot;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.google.common.collect.Lists;
import groovy.lang.Closure;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.KilledByPlayer;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.conditions.RandomChanceWithLooting;
import net.minecraft.world.storage.loot.functions.*;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LootEntryBuilder {

    private static final LootCondition[] EMPTY_CONDITIONS = {};

    private String name;
    private Item item;
    private int weight = 1;
    private int quality = 0;
    private final List<LootFunction> functions = new ArrayList<>();
    private final List<LootCondition> conditions = new ArrayList<>();
    private final GroovyLog.Msg out = GroovyLog.msg("Error creating GroovyScript LootPool").warn();

    public LootEntryBuilder() {
        this.name = "";
    }

    public LootEntryBuilder(String name) {
        this.name = name;
    }

    public LootEntryBuilder name(String name) {
        this.name = name;
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

    //===========================LOOT FUNCTIONS===================================

    public LootEntryBuilder function(LootFunction function) {
        this.functions.add(function);
        return this;
    }

    public LootEntryBuilder function(Closure<Object> function) {
        if (Arrays.equals(function.getParameterTypes(), new Class[]{ItemStack.class, Random.class, LootContext.class})) {
            this.functions.add(new GroovyLootFunction(function));
        } else {
            out.add("custom LootFunctions require parameters (net.minecraft.item.ItemStack, java.util.Random, net.minecraft.world.storage.loot.LootContext)");
        }

        return this;
    }

    public LootEntryBuilder function(Closure<Object> function, LootCondition condition) {
        return this.function(function, new LootCondition[]{condition});
    }

    public LootEntryBuilder function(Closure<Object> function, LootCondition... conditions) {
        if (Arrays.equals(function.getParameterTypes(), new Class[]{ItemStack.class, Random.class, LootContext.class})) {
            this.functions.add(new GroovyLootFunction(conditions, function));
        } else {
            out.add("custom LootFunctions require parameters (net.minecraft.item.ItemStack, java.util.Random, net.minecraft.world.storage.loot.LootContext)");
        }

        return this;
    }

    public LootEntryBuilder enchantWithLevels(boolean isTreasure, int min, int max) {
        this.enchantWithLevels(isTreasure, min, max, new LootCondition[0]);
        return this;
    }

    public LootEntryBuilder enchantWithLevels(boolean isTreasure, int min, int max, LootCondition condition) {
        this.enchantWithLevels(isTreasure, min, max, new LootCondition[]{condition});
        return this;
    }

    public LootEntryBuilder enchantWithLevels(boolean isTreasure, int min, int max, LootCondition... conditions) {
        out.add(min < 0.0f, () -> "enchantWithLevels minimum cannot be less than 0.");
        out.add(max < 0.0f, () -> "enchantWithLevels maximum cannot be less than 0.");
        this.functions.add(new EnchantWithLevels(conditions, new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f)), isTreasure));
        return this;
    }

    public LootEntryBuilder enchantRandomly() {
        return this.enchantRandomly(EMPTY_CONDITIONS);
    }

    public LootEntryBuilder enchantRandomly(Enchantment enchantment) {
        return this.enchantRandomly(EMPTY_CONDITIONS, enchantment);
    }

    public LootEntryBuilder enchantRandomly(Enchantment... enchantments) {
        return this.enchantRandomly(EMPTY_CONDITIONS, enchantments);
    }

    public LootEntryBuilder enchantRandomly(LootCondition condition, Enchantment enchantment) {
        return this.enchantRandomly(new LootCondition[]{condition}, enchantment);
    }

    public LootEntryBuilder enchantRandomly(LootCondition condition, Enchantment... enchantments) {
        return this.enchantRandomly(new LootCondition[]{condition}, enchantments);
    }

    public LootEntryBuilder enchantRandomly(LootCondition[] conditions, Enchantment... enchantments) {
        List<Enchantment> list = (enchantments != null) ? Lists.newArrayList(enchantments) : null;
        if (conditions == null) conditions = EMPTY_CONDITIONS;
        this.functions.add(new EnchantRandomly(conditions, list));
        return this;
    }

    public LootEntryBuilder lootingEnchantBonus(float min, float max) {
        return this.lootingEnchantBonus(min, max, (int) max, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder lootingEnchantBonus(float min, float max, int limit) {
        return this.lootingEnchantBonus(min, max, limit, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder lootingEnchantBonus(float min, float max, LootCondition conditions) {
        return this.lootingEnchantBonus(min, max, (int) max, new LootCondition[]{conditions});
    }

    public LootEntryBuilder lootingEnchantBonus(float min, float max, int limit, LootCondition conditions) {
        return this.lootingEnchantBonus(min, max, limit, new LootCondition[]{conditions});
    }

    public LootEntryBuilder lootingEnchantBonus(float min, float max, LootCondition... conditions) {
        return this.lootingEnchantBonus(min, max, (int) max, conditions);
    }

    public LootEntryBuilder lootingEnchantBonus(float min, float max, int limit, LootCondition... conditions) {
        out.add(min < 0.0f, () -> "lootingEnchantBonus minimum cannot be less than 0.");
        out.add(max < 0.0f, () -> "lootingEnchantBonus maximum cannot be less than 0.");
        out.add(limit < 0, () -> "lootingEnchantBonus limit cannot be less than 0.");
        if (conditions == null) conditions = EMPTY_CONDITIONS;
        this.functions.add(new LootingEnchantBonus(conditions, new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f)), Math.max(limit, 0)));
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

    public LootEntryBuilder setDamage(int min, int max, LootCondition conditions) {
        return this.setDamage(min, max, new LootCondition[]{conditions});
    }

    public LootEntryBuilder setDamage(int min, int max, LootCondition... conditions) {
        out.add(min < 0 || min >= 32767, () -> "setDamage minimum cannot be less than 0 or more than 32766.");
        out.add(max < 0 || min >= 32767, () -> "setDamage maximum cannot be less than 0 or more than 32766.");
        if (conditions == null) conditions = EMPTY_CONDITIONS;
        this.functions.add(new SetDamage(conditions, new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f))));
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

    public LootEntryBuilder setCount(int min, int max, LootCondition conditions) {
        return this.setCount(min, max, new LootCondition[]{conditions});
    }

    public LootEntryBuilder setCount(int min, int max, LootCondition... conditions) {
        out.add(min < 0, () -> "setCount minimum cannot be less than 0.");
        out.add(max < 0, () -> "setCount maximum cannot be less than 0.");
        if (conditions == null) conditions = EMPTY_CONDITIONS;
        this.functions.add(new SetCount(conditions, new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f))));
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

    public LootEntryBuilder setMetadata(int min, int max, LootCondition conditions) {
        return this.setMetadata(min, max, new LootCondition[]{conditions});
    }

    public LootEntryBuilder setMetadata(int min, int max, LootCondition... conditions) {
        out.add(min < 0 || min >= 32767, () -> "setMetadata minimum cannot be less than 0 or more than 32766.");
        out.add(max < 0 || min >= 32767, () -> "setMetadata maximum cannot be less than 0 or more than 32766.");
        if (conditions == null) conditions = EMPTY_CONDITIONS;
        this.functions.add(new SetMetadata(conditions, new RandomValueRange(Math.max(min, 0.0f), Math.max(max, 0.0f))));
        return this;
    }

    public LootEntryBuilder setNBT(NBTTagCompound tag) {
        return this.setNBT(tag, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder setNBT(NBTTagCompound tag, LootCondition conditions) {
        return this.setNBT(tag, new LootCondition[]{conditions});
    }

    public LootEntryBuilder setNBT(NBTTagCompound tag, LootCondition... conditions) {
        if (conditions == null) conditions = EMPTY_CONDITIONS;
        this.functions.add(new SetNBT(conditions, tag));
        return this;
    }

    public LootEntryBuilder setAttributes(SetAttributesFunction.Modifier modifiers) {
        return this.setAttributes(new SetAttributesFunction.Modifier[]{modifiers}, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder setAttributes(SetAttributesFunction.Modifier[] modifiers) {
        return this.setAttributes(modifiers, EMPTY_CONDITIONS);
    }

    public LootEntryBuilder setAttributes(SetAttributesFunction.Modifier modifiers, LootCondition conditions) {
        return this.setAttributes(new SetAttributesFunction.Modifier[]{modifiers}, new LootCondition[]{conditions});
    }

    public LootEntryBuilder setAttributes(SetAttributesFunction.Modifier[] modifiers, LootCondition conditions) {
        return this.setAttributes(modifiers, new LootCondition[]{conditions});
    }

    public LootEntryBuilder setAttributes(SetAttributesFunction.Modifier[] modifiers, LootCondition... conditions) {
        this.functions.add(new SetAttributesFunction(conditions, modifiers));
        return this;
    }

    public LootEntryBuilder smelt() {
        return this.smelt(EMPTY_CONDITIONS);
    }

    public LootEntryBuilder smelt(LootCondition conditions) {
        return this.smelt(new LootCondition[]{conditions});
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
        if (Arrays.equals(customCondition.getParameterTypes(), new Class[]{Random.class, LootContext.class})) {
            this.conditions.add(new GroovyLootCondition(customCondition));
        } else {
            out.add("custom LootConditions require parameters (java.util.Random, net.minecraft.world.storage.loot.LootContext)");
        }

        return this;
    }

    //===========================BASE===================================

    private boolean validate() {
        if (item == null) out.add("No item provided.").error();
        if (name == null || name.isEmpty()) out.add("No name provided").error();
        if (weight <= 0) out.add("weight <= 0 may make the loot entry unable to be rolled");
        if (quality < 0) out.add("quality < 0 may make the loot entry unable to be rolled");
        out.postIfNotEmpty();
        return out.getLevel() != Level.ERROR;
    }

    public LootEntry build() {
        if (!validate()) return null;
        return new LootEntryItem(item, weight, quality, functions.toArray(new LootFunction[0]), conditions.toArray(new LootCondition[0]), name);
    }

}
