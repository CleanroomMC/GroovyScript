package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.brackets.AspectBracketHandler;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
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

import java.util.ArrayList;
import java.util.Collection;

public class Research extends VirtualizedRegistry<ResearchCategory> {

    protected Collection<IScanThing> scanBackup;
    protected Collection<IScanThing> scanScripted;

    public Research() {
        scanBackup = new ArrayList<>();
        scanScripted = new ArrayList<>();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(x -> ResearchCategories.researchCategories.remove(x.key));
        restoreFromBackup().forEach(x -> ResearchCategories.researchCategories.put(x.key, x));

        scanBackup.clear();
        scanScripted.clear();
    }

    private void addCategory(ResearchCategory category) {
        ResearchCategories.researchCategories.put(category.key, category);
        addScripted(category);
    }

    private void removeCategory(ResearchCategory category) {
        ResearchCategories.researchCategories.remove(category.key);
        addBackup(category);
    }

    public void addCategory(String key, String researchkey, AspectList formula, String icon, String background) {
        addCategory(new ResearchCategory(key, researchkey, formula, new ResourceLocation(icon), new ResourceLocation(background)));
    }

    public void addCategory(String key, String researchkey, AspectList formula, String icon, String background, String background2) {
        addCategory(new ResearchCategory(key, researchkey, formula, new ResourceLocation(icon), new ResourceLocation(background), new ResourceLocation(background2)));
    }

    private void addScannable(IScanThing scanThing) {
        scanScripted.add(scanThing);
        ScanningManager.addScannableThing(scanThing);
    }

    public void addScannable(String researchKey, Class<?> entityClass, boolean inheritedClasses) {
        addScannable(new ScanEntity(researchKey, entityClass, inheritedClasses));
    }

    public void addScannable(String researchKey, Class<?> entityClass, boolean inheritedClasses, ThaumcraftApi.EntityTagsNBT tags) {
        addScannable(new ScanEntity(researchKey, entityClass, inheritedClasses, tags));
    }

    public void addScannable(String researchKey, ItemStack item) {
        addScannable(new ScanItem(researchKey, item));
    }

    public void addScannable(Block block) {
        addScannable(new ScanBlock(block));
    }

    public void addScannable(String researchKey, Block block) {
        addScannable(new ScanBlock(researchKey, block));
    }

    public void addScannable(Material material) {
        addScannable(new ScanMaterial(material));
    }

    public void addScannable(String researchKey, Material material) {
        addScannable(new ScanMaterial(researchKey, material));
    }

    public void addScannable(Enchantment enchantment) {
        addScannable(new ScanEnchantment(enchantment));
    }

    public void addScannable(Potion potion) {
        addScannable(new ScanPotion(potion));
    }

    public void addResearchLocation(ResourceLocation location) {
        ThaumcraftApi.registerResearchLocation(location);
        ResearchManager.parseAllResearch();
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
        removeCategory(ResearchCategories.researchCategories.get(key));
    public void removeAllCategories() {
        ResearchCategories.researchCategories.forEach((k, v) -> addBackup(v));
        ResearchCategories.researchCategories.clear();
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
            Aspect a = AspectBracketHandler.validateAspect(tag);
            if (a != null) this.formula.add(a, amount);
            return this;
        }

        public ResearchCategoryBuilder icon(ResourceLocation icon) {
            this.icon = icon;
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

        public ResearchCategoryBuilder background(ResourceLocation background) {
            this.background = background;
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

        public ResearchCategoryBuilder background2(ResourceLocation background2) {
            this.background2 = background2;
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

        public ResearchCategory register() {
            ResearchCategory category = background2 == null
                                        ? new ResearchCategory(key, researchKey, formula, icon, background)
                                        : new ResearchCategory(key, researchKey, formula, icon, background, background2);

            ModSupport.THAUMCRAFT.get().research.addCategory(category);
            return category;
        }
    }
}
