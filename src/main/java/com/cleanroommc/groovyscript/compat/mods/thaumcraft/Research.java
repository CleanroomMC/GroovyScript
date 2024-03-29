package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
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

@RegistryDescription
public class Research extends VirtualizedRegistry<ResearchCategory> {

    protected AbstractReloadableStorage<IScanThing> scanStorage = new AbstractReloadableStorage<>();

    @Override
    public void onReload() {
        removeScripted().forEach(x -> ResearchCategories.researchCategories.remove(x.key));
        restoreFromBackup().forEach(x -> ResearchCategories.researchCategories.put(x.key, x));

        scanStorage.removeScripted();
        scanStorage.restoreFromBackup();
    }

    private void addCategory(ResearchCategory category) {
        ResearchCategories.researchCategories.put(category.key, category);
        addScripted(category);
    }

    private void removeCategory(ResearchCategory category) {
        ResearchCategories.researchCategories.remove(category.key);
        addBackup(category);
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.research.addCategory0", type = MethodDescription.Type.ADDITION)
    public void addCategory(String key, String researchkey, AspectList formula, String icon, String background) {
        addCategory(new ResearchCategory(key, researchkey, formula, new ResourceLocation(icon), new ResourceLocation(background)));
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.research.addCategory1", type = MethodDescription.Type.ADDITION)
    public void addCategory(String key, String researchkey, AspectList formula, String icon, String background, String background2) {
        addCategory(new ResearchCategory(key, researchkey, formula, new ResourceLocation(icon), new ResourceLocation(background), new ResourceLocation(background2)));
    }

    private void addScannable(IScanThing scanThing) {
        scanStorage.addScripted(scanThing);
        ScanningManager.addScannableThing(scanThing);
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.research.addScannable0", type = MethodDescription.Type.ADDITION)
    public void addScannable(String researchKey, Class<?> entityClass, boolean inheritedClasses) {
        addScannable(new ScanEntity(researchKey, entityClass, inheritedClasses));
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.research.addScannable1", type = MethodDescription.Type.ADDITION)
    public void addScannable(String researchKey, Class<?> entityClass, boolean inheritedClasses, ThaumcraftApi.EntityTagsNBT tags) {
        addScannable(new ScanEntity(researchKey, entityClass, inheritedClasses, tags));
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.research.addScannable2", type = MethodDescription.Type.ADDITION, example = @Example("'KNOWLEDGETYPEHUMOR', item('minecraft:pumpkin')"))
    public void addScannable(String researchKey, ItemStack item) {
        addScannable(new ScanItem(researchKey, item));
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.research.addScannable3", type = MethodDescription.Type.ADDITION)
    public void addScannable(Block block) {
        addScannable(new ScanBlock(block));
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.research.addScannable4", type = MethodDescription.Type.ADDITION)
    public void addScannable(String researchKey, Block block) {
        addScannable(new ScanBlock(researchKey, block));
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.research.addScannable5", type = MethodDescription.Type.ADDITION)
    public void addScannable(Material material) {
        addScannable(new ScanMaterial(material));
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.research.addScannable6", type = MethodDescription.Type.ADDITION)
    public void addScannable(String researchKey, Material material) {
        addScannable(new ScanMaterial(researchKey, material));
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.research.addScannable7", type = MethodDescription.Type.ADDITION)
    public void addScannable(Enchantment enchantment) {
        addScannable(new ScanEnchantment(enchantment));
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.research.addScannable8", type = MethodDescription.Type.ADDITION)
    public void addScannable(Potion potion) {
        addScannable(new ScanPotion(potion));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "resource('thaumcraft:research/new.json')", commented = true))
    public void addResearchLocation(ResourceLocation location) {
        ThaumcraftApi.registerResearchLocation(location);
        ResearchManager.parseAllResearch();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addResearchLocation(String location) {
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(location));
        ResearchManager.parseAllResearch();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addResearchLocation(String mod, String location) {
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(mod, location));
        ResearchManager.parseAllResearch();
    }

    @MethodDescription(example = @Example(value = "'BASICS'", commented = true))
    public void removeCategory(String key) {
        removeCategory(ResearchCategories.researchCategories.get(key));
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAllCategories() {
        ResearchCategories.researchCategories.forEach((k, v) -> addBackup(v));
        ResearchCategories.researchCategories.clear();
    }

    @RecipeBuilderDescription(example = @Example(".key('BASICS2').researchKey('UNLOCKAUROMANCY').formulaAspect(aspect('herba') * 5).formulaAspect(aspect('ordo') * 5).formulaAspect(aspect('perditio') * 5).formulaAspect('aer', 5).formulaAspect('ignis', 5).formulaAspect(aspect('terra') * 5).formulaAspect('aqua', 5).icon(resource('thaumcraft:textures/aspects/humor.png')).background(resource('thaumcraft:textures/gui/gui_research_back_1.jpg')).background2(resource('thaumcraft:textures/gui/gui_research_back_over.png'))"))
    public ResearchCategoryBuilder researchCategoryBuilder() {
        return new ResearchCategoryBuilder();
    }

    public static class ResearchCategoryBuilder {

        @Property
        private String key;
        @Property
        private String researchKey;
        @Property
        private final AspectList formula = new AspectList();
        @Property
        private ResourceLocation icon;
        @Property
        private ResourceLocation background;
        @Property
        private ResourceLocation background2 = null;

        @RecipeBuilderMethodDescription
        public ResearchCategoryBuilder key(String key) {
            this.key = key;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ResearchCategoryBuilder researchKey(String researchKey) {
            this.researchKey = researchKey;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "formula")
        public ResearchCategoryBuilder formulaAspect(AspectStack aspect) {
            this.formula.add(aspect.getAspect(), aspect.getAmount());
            return this;
        }

        @RecipeBuilderMethodDescription(field = "formula")
        public ResearchCategoryBuilder formulaAspect(String tag, int amount) {
            Aspect a = Thaumcraft.validateAspect(tag);
            if (a != null) this.formula.add(a, amount);
            return this;
        }

        @RecipeBuilderMethodDescription
        public ResearchCategoryBuilder icon(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ResearchCategoryBuilder icon(String icon) {
            this.icon = new ResourceLocation(icon);
            return this;
        }

        @RecipeBuilderMethodDescription
        public ResearchCategoryBuilder icon(String mod, String icon) {
            this.icon = new ResourceLocation(mod, icon);
            return this;
        }

        @RecipeBuilderMethodDescription
        public ResearchCategoryBuilder background(ResourceLocation background) {
            this.background = background;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ResearchCategoryBuilder background(String background) {
            this.background = new ResourceLocation(background);
            return this;
        }

        @RecipeBuilderMethodDescription
        public ResearchCategoryBuilder background(String mod, String background) {
            this.background = new ResourceLocation(mod, background);
            return this;
        }

        @RecipeBuilderMethodDescription
        public ResearchCategoryBuilder background2(ResourceLocation background2) {
            this.background2 = background2;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ResearchCategoryBuilder background2(String background2) {
            this.background2 = new ResourceLocation(background2);
            return this;
        }

        @RecipeBuilderMethodDescription
        public ResearchCategoryBuilder background2(String mod, String background2) {
            this.background2 = new ResourceLocation(mod, background2);
            return this;
        }

        @RecipeBuilderRegistrationMethod
        public ResearchCategory register() {
            ResearchCategory category = background2 == null
                                        ? new ResearchCategory(key, researchKey, formula, icon, background)
                                        : new ResearchCategory(key, researchKey, formula, icon, background, background2);

            ModSupport.THAUMCRAFT.get().research.addCategory(category);
            return category;
        }
    }
}
