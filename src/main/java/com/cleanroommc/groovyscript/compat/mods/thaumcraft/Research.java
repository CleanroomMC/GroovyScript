package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
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

    public Research() {
        //do nothing
    }

    public void addCategory(String key, String researchkey, AspectList formula, ResourceLocation icon, ResourceLocation background) {
        ResearchCategories.registerCategory(key, researchkey, formula, icon, background);
    }

    public void addCategory(String key, String researchkey, AspectList formula, ResourceLocation icon, ResourceLocation background, ResourceLocation background2) {
        ResearchCategories.registerCategory(key, researchkey, formula, icon, background, background2);
    }

    public void addScannable(String researchKey, Class entityClass, boolean inheritedClasses) {
        ScanningManager.addScannableThing(new ScanEntity(researchKey, entityClass, inheritedClasses));
    }

    public void addScannable(String researchKey, Class entityClass, boolean inheritedClasses, ThaumcraftApi.EntityTagsNBT tags) {
        ScanningManager.addScannableThing(new ScanEntity(researchKey, entityClass, inheritedClasses, tags));
    }

    public void addScannable(String researchKey, ItemStack item) {
        ScanningManager.addScannableThing(new ScanItem(researchKey, item));
    }

    public void addScannable(Block block) {
        ScanningManager.addScannableThing(new ScanBlock(block));
    }

    public void addScannable(String researchKey, Block block) {
        ScanningManager.addScannableThing(new ScanBlock(researchKey, block));
    }

    public void addScannable(Material material) {
        ScanningManager.addScannableThing(new ScanMaterial(material));
    }

    public void addScannable(String researchKey, Material material) {
        ScanningManager.addScannableThing(new ScanMaterial(researchKey, material));
    }

    public void addScannable(Enchantment enchantment) {
        ScanningManager.addScannableThing(new ScanEnchantment(enchantment));
    }

    public void addScannable(Potion potion) {
        ScanningManager.addScannableThing(new ScanPotion(potion));
    }

    public void addResearchLocation(ResourceLocation location) {
        ThaumcraftApi.registerResearchLocation(location);
        ResearchManager.parseAllResearch();
    }

    public void removeCategory(String key) {
        ResearchCategories.researchCategories.remove(key);
    }

    public ResearchCategoryBuilder researchCategoryBuilder() {
        return new ResearchCategoryBuilder();
    }

    public class ResearchCategoryBuilder {

        private String key;
        private String researchKey;
        private AspectList formula = new AspectList();
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

        public ResearchCategoryBuilder formulaAspect(AspectStack aspect) {
            this.formula.add(aspect.getAspect(), aspect.getAmount());
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
                ResearchCategories.registerCategory(key, researchKey, formula, icon, background);
            else
                ResearchCategories.registerCategory(key, researchKey, formula, icon, background, background2);
        }
    }



}
