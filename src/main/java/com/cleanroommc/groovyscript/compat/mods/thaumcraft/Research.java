package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.*;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ScanEnchantment;
import thaumcraft.common.lib.research.ScanPotion;

public class Research {

    public Research() {
        //do nothing
    }

    public void addCategory(String key, String researchkey, AspectList formula, String icon, String background) {
        ResearchCategories.registerCategory(key, researchkey, formula, new ResourceLocation(icon), new ResourceLocation(background));
    }

    public void addCategory(String key, String researchkey, AspectList formula, String icon, String background, String background2) {
        ResearchCategories.registerCategory(key, researchkey, formula, new ResourceLocation(icon), new ResourceLocation(background), new ResourceLocation(background2));
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

    public void addResearchLocation(String location) {
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(location));
        ResearchManager.parseAllResearch();
    }

    public void addResearchLocation(String mod, String location) {
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(mod, location));
        ResearchManager.parseAllResearch();
    }

    public void removeCategory(String key) {
        ResearchCategories.researchCategories.remove(key);
    }

    public ResearchCategoryBuilder researchCategoryBuilder() {
        return new ResearchCategoryBuilder();
    }

    public static class ResearchCategoryBuilder {

        private String key;
        private String researchKey;
        private final AspectList formula = new AspectList();
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

        public ResearchCategoryBuilder formulaAspect(String tag, int amount) {
            Aspect a = Thaumcraft.validateAspect(tag);
            if (a != null) this.formula.add(a, amount);
            return this;
        }

        public ResearchCategoryBuilder icon(String icon) {
            this.icon = new ResourceLocation(icon);
            return this;
        }

        public ResearchCategoryBuilder icon(String mod, String icon) {
            this.icon = new ResourceLocation(mod, icon);
            return this;
        }

        public ResearchCategoryBuilder background(String background) {
            this.background = new ResourceLocation(background);
            return this;
        }

        public ResearchCategoryBuilder background(String mod, String background) {
            this.background = new ResourceLocation(mod, background);
            return this;
        }

        public ResearchCategoryBuilder background2(String background2) {
            this.background2 = new ResourceLocation(background2);
            return this;
        }

        public ResearchCategoryBuilder background2(String mod, String background2) {
            this.background2 = new ResourceLocation(mod, background2);
            return this;
        }

        public void register() {
            if (background2 == null) {
                ResearchCategories.registerCategory(key, researchKey, formula, icon, background);
            } else {
                ResearchCategories.registerCategory(key, researchKey, formula, icon, background, background2);
            }
        }
    }
}
