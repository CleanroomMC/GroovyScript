package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.*;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ScanEnchantment;
import thaumcraft.common.lib.research.ScanPotion;

public class Research {

    public static void addCategory(String key, String researchkey, AspectList formula, ResourceLocation icon, ResourceLocation background) {
        ResearchCategories.registerCategory(key, researchkey, formula, icon, background);
    }

    public static void addCategory(String key, String researchkey, AspectList formula, ResourceLocation icon, ResourceLocation background, ResourceLocation background2) {
        ResearchCategories.registerCategory(key, researchkey, formula, icon, background, background2);
    }

    public static void addScannable(String researchKey, Class entityClass, boolean inheritedClasses) {
        ScanningManager.addScannableThing(new ScanEntity(researchKey, entityClass, inheritedClasses));
    }

    public static void addScannable(String researchKey, Class entityClass, boolean inheritedClasses, ThaumcraftApi.EntityTagsNBT tags) {
        ScanningManager.addScannableThing(new ScanEntity(researchKey, entityClass, inheritedClasses, tags));
    }

    public static void addScannable(String researchKey, ItemStack item) {
        ScanningManager.addScannableThing(new ScanItem(researchKey, item));
    }

    public static void addScannable(Block block) {
        ScanningManager.addScannableThing(new ScanBlock(block));
    }

    public static void addScannable(String researchKey, Block block) {
        ScanningManager.addScannableThing(new ScanBlock(researchKey, block));
    }

    public static void addScannable(Material material) {
        ScanningManager.addScannableThing(new ScanMaterial(material));
    }

    public static void addScannable(String researchKey, Material material) {
        ScanningManager.addScannableThing(new ScanMaterial(researchKey, material));
    }

    public static void addScannable(Enchantment enchantment) {
        ScanningManager.addScannableThing(new ScanEnchantment(enchantment));
    }

    public static void addScannable(Potion potion) {
        ScanningManager.addScannableThing(new ScanPotion(potion));
    }

    public static void addResearchLocation(ResourceLocation location) {
        ThaumcraftApi.registerResearchLocation(location);
        ResearchManager.parseAllResearch();
    }

    public static void removeCategory(String key) {
        ResearchCategories.researchCategories.remove(key);
    }

    public static class ResearchCategoryBuilder {

        private String key;
        private String researchKey;
        private AspectList formula;
        private ResourceLocation icon;
        private ResourceLocation background;
        private ResourceLocation background2 = null;

        public ResearchCategoryBuilder key(String key) {
            this.key = key;
            return this;
        }

        public ResearchCategoryBuilder researchKey(String researchKey) {
            this.researchKey = researchKey;
            return this;
        }

        public ResearchCategoryBuilder formula(AspectList formula) {
            this.formula = formula;
            return this;
        }

        public ResearchCategoryBuilder icon(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        public ResearchCategoryBuilder background(ResourceLocation background) {
            this.background = background;
            return this;
        }

        public ResearchCategoryBuilder background2(ResourceLocation background2) {
            this.background2 = background2;
            return this;
        }

        public void register() {
            if (background2 == null)
                Research.addCategory(key, researchKey, formula, icon, background);
            else
                Research.addCategory(key, researchKey, formula, icon, background, background2);
        }
    }



}
