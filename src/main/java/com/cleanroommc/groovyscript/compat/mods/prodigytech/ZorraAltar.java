package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lykrast.prodigytech.common.recipe.ZorraAltarManager;
import lykrast.prodigytech.common.util.Config;
import net.minecraft.enchantment.Enchantment;

import java.util.Map;
import java.util.Objects;


@RegistryDescription
public class ZorraAltar extends VirtualizedRegistry<ZorraAltar.ZorraRecipeData> {

    @GroovyBlacklist
    private static final Map<String, ZorraAltarManager> managers = new Object2ObjectOpenHashMap<>();

    ZorraAltar() {
        managers.put("sword", ZorraAltarManager.SWORD);
        managers.put("bow", ZorraAltarManager.BOW);
    }

    public ZorraAltarManager createRegistry(String key) {
        ZorraAltarManager manager = new ZorraAltarManager();
        managers.put(key, manager);
        return manager;
    }

    public ZorraAltarManager getRegistry(String key) {
        return managers.get(key);
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(this::removeEnchantment);
        restoreFromBackup().forEach(this::addEnchantment);
    }

    private void addEnchantment(ZorraRecipeData recipe) {
        if (!managers.containsKey(recipe.registry)) return;
        managers.get(recipe.registry).addEnchant(recipe.enchantment, recipe.maxLevel);
    }

    private void removeEnchantment(ZorraRecipeData recipe) {
        if (!managers.containsKey(recipe.registry)) return;
        managers.get(recipe.registry).removeEnchant(recipe.enchantment);
    }

    @MethodDescription(example = {
            @Example("'sword', enchantment('minecraft:power'), 10"),
            @Example("'stick', enchantment('minecraft:knockback'), 20")
    })
    public void addEnchantment(String registry, Enchantment enchantment, int maxLevel) {
        if (!managers.containsKey(registry)) return;
        managers.get(registry).addEnchant(enchantment, maxLevel);
        addScripted(new ZorraRecipeData(registry, enchantment, maxLevel));
    }

    @MethodDescription(example = @Example("'sword', enchantment('minecraft:sharpness')"))
    public boolean removeEnchantment(String registry, Enchantment enchantment) {
        if (!managers.containsKey(registry)) return false;
        int maxLevel = enchantment.getMaxLevel();
        if (maxLevel > 1)
            maxLevel += Config.altarBonusLvl;
        addBackup(new ZorraRecipeData(registry, enchantment, maxLevel));
        return managers.get(registry).removeEnchant(enchantment);
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static final class ZorraRecipeData {

        private final String registry;
        private final Enchantment enchantment;
        private final int maxLevel;

        public ZorraRecipeData(String registry, Enchantment enchantment, int maxLevel) {
            this.registry = registry;
            this.enchantment = enchantment;
            this.maxLevel = maxLevel;
        }

        public String registry() {
            return registry;
        }

        public Enchantment enchantment() {
            return enchantment;
        }

        public int maxLevel() {
            return maxLevel;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ZorraRecipeData) obj;
            return Objects.equals(this.registry, that.registry) && Objects.equals(this.enchantment, that.enchantment) && this.maxLevel == that.maxLevel;
        }

        @Override
        public int hashCode() {
            return Objects.hash(registry, enchantment, maxLevel);
        }

        @Override
        public String toString() {
            return "ZorraRecipeData[" + "registry=" + registry + ", " + "enchantment=" + enchantment + ", " + "maxLevel=" + maxLevel + ']';
        }
    }
}
