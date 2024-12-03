package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.Thaumcraft;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.ApiStatus;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RegistryDescription
public class AspectHelper extends VirtualizedRegistry<AspectListHelper> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(aspectList -> {
            if (aspectList.item != null)
                for (AspectStack as : aspectList.aspects)
                    this.remove(aspectList.item, as, false);
            else if (aspectList.entity != null)
                for (AspectStack as : aspectList.aspects)
                    this.remove(aspectList.entity, as, false);
        });
        restoreFromBackup().forEach(aspectList -> {
            if (aspectList.item != null)
                for (AspectStack as : aspectList.aspects)
                    this.add(aspectList.item, as, false);
            else if (aspectList.entity != null)
                for (AspectStack as : aspectList.aspects)
                    this.add(aspectList.entity, as, false);
        });
    }

    @GroovyBlacklist
    public void addScripted(Object target, AspectStack aspect) {
        AtomicBoolean found = new AtomicBoolean(false);
        getScriptedRecipes().forEach(scriptedAspect -> {
            if (target instanceof EntityEntry entityEntry && scriptedAspect.entity != null && entityEntry.getName().equals(scriptedAspect.entity.getName())) {
                found.set(true);
                scriptedAspect.addAspect(aspect);
            } else if (target instanceof ItemStack itemStack && scriptedAspect.item != null && itemStack.isItemEqual(scriptedAspect.item)) {
                found.set(true);
                scriptedAspect.addAspect(aspect);
            }
        });

        if (!found.get()) {
            ArrayList<AspectStack> aspectList = new ArrayList<>();
            aspectList.add(aspect);
            if (target instanceof ItemStack itemStack)
                addScripted(new AspectListHelper(itemStack, aspectList));
            else if (target instanceof EntityEntry entityEntry)
                addScripted(new AspectListHelper(entityEntry, aspectList));
        }
    }

    @GroovyBlacklist
    public void addBackup(Object target, AspectStack aspect) {
        AtomicBoolean found = new AtomicBoolean(false);
        getBackupRecipes().forEach(backupAspect -> {
            if (target instanceof EntityEntry entityEntry && backupAspect.entity != null && entityEntry.getName().equals(backupAspect.entity.getName())) {
                found.set(true);
                backupAspect.addAspect(aspect);
            } else if (target instanceof ItemStack itemStack && backupAspect.item != null && itemStack.isItemEqual(backupAspect.item)) {
                found.set(true);
                backupAspect.addAspect(aspect);
            }
        });

        if (!found.get()) {
            ArrayList<AspectStack> aspectList = new ArrayList<>();
            aspectList.add(aspect);
            if (target instanceof ItemStack itemStack)
                addBackup(new AspectListHelper(itemStack, aspectList));
            else if (target instanceof EntityEntry entityEntry)
                addBackup(new AspectListHelper(entityEntry, aspectList));
        }
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.add_entity", type = MethodDescription.Type.ADDITION)
    public void add(EntityEntry entity, AspectStack aspect) {
        this.add(entity, aspect, true);
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.add_ore", type = MethodDescription.Type.ADDITION)
    public void add(OreDictIngredient oreDict, AspectStack aspect) {
        this.add(oreDict, aspect, true);
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.add_item", type = MethodDescription.Type.ADDITION)
    public void add(ItemStack item, AspectStack aspect) {
        this.add(item, aspect, true);
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.remove_entity")
    public void remove(EntityEntry entity, AspectStack aspect) {
        this.remove(entity, aspect, true);
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.remove_ore")
    public void remove(OreDictIngredient oreDict, AspectStack aspect) {
        this.remove(oreDict, aspect, true);
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.remove_item")
    public void remove(ItemStack item, AspectStack aspect) {
        this.remove(item, aspect, true);
    }

    @SuppressWarnings("deprecation")
    public void add(EntityEntry entity, AspectStack aspect, boolean doBackup) {
        if (entity != null && aspect != null) {
            AtomicBoolean found = new AtomicBoolean(false);
            CommonInternals.scanEntities.forEach(entityTags -> {
                if (entityTags.entityName.equals(entity.getName())) {
                    entityTags.aspects.remove(aspect.getAspect());
                    if (aspect.getAmount() != 0)
                        entityTags.aspects.add(aspect.getAspect(), aspect.getAmount());
                    found.set(true);
                }
            });
            if (!found.get()) {
                ThaumcraftApi.registerEntityTag(
                        entity.getName(),
                        new AspectList().add(aspect.getAspect(), aspect.getAmount())
                );
            }

            if (doBackup) addScripted(entity, aspect);

            return;
        }
        GroovyLog.msg("Error adding Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    public void add(OreDictIngredient oreDic, AspectStack aspect, boolean doBackup) {
        if (oreDic != null && aspect != null) {
            List<ItemStack> ores = ThaumcraftApiHelper.getOresWithWildCards(oreDic.getOreDict());
            if (ores != null && !ores.isEmpty()) {

                for (ItemStack ore : ores) {
                    try {
                        ItemStack oc = ore.copy();
                        oc.setCount(1);
                        this.add(oc, aspect, doBackup);
                    } catch (Exception ignored) {
                    }
                }
            }
            return;
        }
        GroovyLog.msg("Error adding Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    public void add(ItemStack item, AspectStack aspect, boolean doBackup) {
        if (item != null && aspect != null) {
            CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(item)).remove(aspect.getAspect());

            if (aspect.getAmount() != 0)
                CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(item)).add(aspect.getAspect(), aspect.getAmount());

            if (doBackup) addScripted(item, aspect);

            return;
        }
        GroovyLog.msg("Error adding Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    public void remove(EntityEntry entity, AspectStack aspect, boolean doBackup) {
        if (entity != null && aspect != null) {
            CommonInternals.scanEntities.forEach(entityTags -> {
                if (entityTags.entityName.equals(entity.getName())) {
                    for (Aspect a : entityTags.aspects.getAspects()) {
                        if (a.equals(aspect.getAspect())) {
                            aspect.setAmount(entityTags.aspects.getAmount(a));
                            entityTags.aspects.remove(a);
                        }
                    }
                }
            });

            if (doBackup) addBackup(entity, aspect);

            return;
        }
        GroovyLog.msg("Error removing Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    public void remove(OreDictIngredient oreDic, AspectStack aspect, boolean doBackup) {
        if (oreDic != null && aspect != null) {
            List<ItemStack> ores = ThaumcraftApiHelper.getOresWithWildCards(oreDic.getOreDict());
            if (ores != null && !ores.isEmpty()) {

                for (ItemStack ore : ores) {
                    try {
                        ItemStack oc = ore.copy();
                        oc.setCount(1);
                        this.remove(oc, aspect, doBackup);
                    } catch (Exception ignored) {
                    }
                }
            }
            return;
        }
        GroovyLog.msg("Error removing Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    public void remove(ItemStack item, AspectStack aspect, boolean doBackup) {
        if (item != null && aspect != null) {
            if (doBackup) {
                aspect.setAmount(CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(item)).getAmount(aspect.getAspect()));
                addBackup(item, aspect);
            }

            CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(item)).remove(aspect.getAspect());

            return;
        }
        GroovyLog.msg("Error adding Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.removeAll_entity")
    public void removeAll(EntityEntry entity) {
        if (entity != null) {
            for (ThaumcraftApi.EntityTags e : CommonInternals.scanEntities) {
                if (e.entityName.equals(entity.getName())) {
                    for (Aspect a : e.aspects.getAspects()) {
                        this.remove(entity, new AspectStack(a, e.aspects.getAmount(a)));
                    }
                    return;
                }
            }
        }
        GroovyLog.msg("Error removing Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.removeAll_ore")
    public void removeAll(OreDictIngredient oreDic) {
        List<ItemStack> ores = ThaumcraftApiHelper.getOresWithWildCards(oreDic.getOreDict());
        if (ores != null && !ores.isEmpty()) {

            for (ItemStack ore : ores) {
                try {
                    ItemStack oc = ore.copy();
                    oc.setCount(1);
                    this.removeAll(oc);
                } catch (Exception ignored) {
                }
            }
        }
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.removeAll_item")
    public void removeAll(ItemStack target) {
        for (Aspect a : CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(target)).getAspects())
            this.remove(target, new AspectStack(a, CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(target)).getAmount(a)));
    }

    @RecipeBuilderDescription(example = {
            @Example(".object(item('minecraft:stone')).stripAspects().aspect(aspect('ignis') * 20).aspect('ordo', 5)"),
            @Example(".object(ore('cropPumpkin')).stripAspects().aspect(aspect('herba') * 20)"),
            @Example(".entity(entity('minecraft:chicken')).stripAspects().aspect('bestia', 20)")
    })
    public AspectHelperBuilder aspectBuilder() {
        return new AspectHelperBuilder();
    }

    public static class AspectHelperBuilder {

        @Property(comp = @Comp(unique = "groovyscript.wiki.thaumcraft.aspect_helper.target.required"))
        private EntityEntry entity;
        @Property(comp = @Comp(unique = "groovyscript.wiki.thaumcraft.aspect_helper.target.required"))
        private IIngredient object;
        @Property
        private final List<AspectStack> aspects = new ArrayList<>();
        @Property
        private boolean stripAspects;

        @RecipeBuilderMethodDescription
        public AspectHelperBuilder entity(EntityEntry entity) {
            this.entity = entity;
            return this;
        }

        @RecipeBuilderMethodDescription
        public AspectHelperBuilder object(IIngredient object) {
            this.object = object;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aspects")
        public AspectHelperBuilder aspect(AspectStack aspect) {
            this.aspects.add(aspect);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "aspects")
        public AspectHelperBuilder aspect(String tag, int amount) {
            Aspect a = Thaumcraft.validateAspect(tag);
            if (a != null) this.aspects.add(new AspectStack(a, amount));
            return this;
        }

        @RecipeBuilderMethodDescription
        public AspectHelperBuilder stripAspects() {
            stripAspects = !stripAspects;
            return this;
        }

        @RecipeBuilderRegistrationMethod
        public void register() {
            if (stripAspects) {
                if (entity != null) {
                    ModSupport.THAUMCRAFT.get().aspectHelper.removeAll(entity);
                } else if (object != null && object instanceof OreDictIngredient oreDictIngredient) {
                    ModSupport.THAUMCRAFT.get().aspectHelper.removeAll(oreDictIngredient);
                } else if (object != null && IngredientHelper.isItem(object) && !IngredientHelper.isEmpty(object)) {
                    ModSupport.THAUMCRAFT.get().aspectHelper.removeAll(IngredientHelper.toItemStack(object));
                } else {
                    GroovyLog.msg("Error removing Thaumcraft Aspects from item/entity")
                            .error()
                            .post();
                }
            }
            aspects.forEach(aspectStack -> {
                if (entity != null)
                    ModSupport.THAUMCRAFT.get().aspectHelper.add(entity, aspectStack);
                else if (object != null && object instanceof OreDictIngredient oreDictIngredient)
                    ModSupport.THAUMCRAFT.get().aspectHelper.add(oreDictIngredient, aspectStack);
                else if (object != null && IngredientHelper.isItem(object) && !IngredientHelper.isEmpty(object))
                    ModSupport.THAUMCRAFT.get().aspectHelper.add(IngredientHelper.toItemStack(object), aspectStack);
                else
                    GroovyLog.msg("Error adding Thaumcraft Aspects to item/entity")
                            .error()
                            .post();
            });
        }
    }
}
