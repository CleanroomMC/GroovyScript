package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.brackets.AspectBracketHandler;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
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

public class AspectHelper extends VirtualizedRegistry<AspectListHelper> {

    public AspectHelper() {
        super("AspectHelper", "aspect_helper");
    }

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
        scripted.forEach(scriptedAspect -> {
            if (target instanceof EntityEntry && scriptedAspect.entity != null
                && ((EntityEntry) target).getName().equals(scriptedAspect.entity.getName())) {
                found.set(true);
                scriptedAspect.addAspect(aspect);
            } else if (target instanceof ItemStack && scriptedAspect.item != null
                       && ((ItemStack) target).isItemEqual(scriptedAspect.item)) {
                found.set(true);
                scriptedAspect.addAspect(aspect);
            }
        });

        if (!found.get()) {
            ArrayList<AspectStack> aspectList = new ArrayList<>();
            aspectList.add(aspect);
            if (target instanceof ItemStack)
                scripted.add(new AspectListHelper((ItemStack) target, aspectList));
            else if (target instanceof EntityEntry)
                scripted.add(new AspectListHelper((EntityEntry) target, aspectList));
        }
    }

    @GroovyBlacklist
    public void addBackup(Object target, AspectStack aspect) {
        AtomicBoolean found = new AtomicBoolean(false);
        backup.forEach(backupAspect -> {
            if (target instanceof EntityEntry && backupAspect.entity != null
                && ((EntityEntry) target).getName().equals(backupAspect.entity.getName())) {
                found.set(true);
                backupAspect.addAspect(aspect);
            } else if (target instanceof ItemStack && backupAspect.item != null
                       && ((ItemStack) target).isItemEqual(backupAspect.item)) {
                found.set(true);
                backupAspect.addAspect(aspect);
            }
        });

        if (!found.get()) {
            ArrayList<AspectStack> aspectList = new ArrayList<>();
            aspectList.add(aspect);
            if (target instanceof ItemStack)
                backup.add(new AspectListHelper((ItemStack) target, aspectList));
            else if (target instanceof EntityEntry)
                backup.add(new AspectListHelper((EntityEntry) target, aspectList));
        }
    }

    public void add(EntityEntry entity, AspectStack aspect) {
        this.add(entity, aspect, true);
    }

    public void add(OreDictIngredient oreDict, AspectStack aspect) {
        this.add(oreDict, aspect, true);
    }

    public void add(ItemStack item, AspectStack aspect) {
        this.add(item, aspect, true);
    }

    public void remove(EntityEntry entity, AspectStack aspect) {
        this.remove(entity, aspect, true);
    }

    public void remove(OreDictIngredient oreDict, AspectStack aspect) {
        this.remove(oreDict, aspect, true);
    }

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
            if (ores != null && ores.size() > 0) {

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
            if (ores != null && ores.size() > 0) {

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

    public void removeAll(OreDictIngredient oreDic) {
        List<ItemStack> ores = ThaumcraftApiHelper.getOresWithWildCards(oreDic.getOreDict());
        if (ores != null && ores.size() > 0) {

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

    public void removeAll(ItemStack target) {
        for (Aspect a : CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(target)).getAspects())
            this.remove(target, new AspectStack(a, CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(target)).getAmount(a)));
    }

    public AspectHelperBuilder aspectBuilder() {
        return new AspectHelperBuilder();
    }

    public static class AspectHelperBuilder {

        private EntityEntry entity;
        private IIngredient object;
        private final ArrayList<AspectStack> aspects = new ArrayList<>();

        public AspectHelperBuilder entity(EntityEntry entity) {
            this.entity = entity;
            return this;
        }

        public AspectHelperBuilder object(IIngredient object) {
            this.object = object;
            return this;
        }

        public AspectHelperBuilder aspect(AspectStack aspect) {
            this.aspects.add(aspect);
            return this;
        }

        public AspectHelperBuilder aspect(String tag, int amount) {
            Aspect a = AspectBracketHandler.validateAspect(tag);
            if (a != null) this.aspects.add(new AspectStack(a, amount));
            return this;
        }

        public AspectHelperBuilder stripAspects() {
            if (entity != null) {
                ModSupport.THAUMCRAFT.get().aspectHelper.removeAll(entity);
            } else if (object != null && object instanceof OreDictIngredient) {
                ModSupport.THAUMCRAFT.get().aspectHelper.removeAll((OreDictIngredient) object);
            } else if (object != null && IngredientHelper.isItem(object) && !IngredientHelper.isEmpty(object)) {
                ModSupport.THAUMCRAFT.get().aspectHelper.removeAll(IngredientHelper.toItemStack(object));
            } else {
                GroovyLog.msg("Error removing Thaumcraft Aspects from item/entity")
                        .error()
                        .post();
            }
            return this;
        }

        public void register() {
            aspects.forEach(aspectStack -> {
                if (entity != null)
                    ModSupport.THAUMCRAFT.get().aspectHelper.add(entity, aspectStack);
                else if (object != null && object instanceof OreDictIngredient)
                    ModSupport.THAUMCRAFT.get().aspectHelper.add(((OreDictIngredient) object), aspectStack);
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
