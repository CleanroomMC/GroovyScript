package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.Thaumcraft;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.ApiStatus;
import thaumcraft.api.ThaumcraftApi;
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
            if (aspectList.item != null) {
                for (AspectStack as : aspectList.aspects) {
                    remove(aspectList.item, as, false);
                }
            } else if (aspectList.entity != null) {
                for (AspectStack as : aspectList.aspects) {
                    remove(aspectList.entity, as, false);
                }
            }
        });
        restoreFromBackup().forEach(aspectList -> {
            if (aspectList.item != null) {
                for (AspectStack as : aspectList.aspects) {
                    add(aspectList.item, as, false);
                }
            } else if (aspectList.entity != null) {
                for (AspectStack as : aspectList.aspects) {
                    add(aspectList.entity, as, false);
                }
            }
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
            if (target instanceof ItemStack itemStack) {
                addScripted(new AspectListHelper(itemStack, aspectList));
            } else if (target instanceof EntityEntry entityEntry) {
                addScripted(new AspectListHelper(entityEntry, aspectList));
            }
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
        add(entity, aspect, true);
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.add_item", type = MethodDescription.Type.ADDITION)
    public void add(IIngredient ingredient, AspectStack aspect) {
        add(ingredient, aspect, true);
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.remove_entity")
    public void remove(EntityEntry entity, AspectStack aspect) {
        remove(entity, aspect, true);
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.remove_item")
    public void remove(IIngredient ingredient, AspectStack aspect) {
        remove(ingredient, aspect, true);
    }

    private static AspectList getAspects(ItemStack stack, boolean add) {
        int id = CommonInternals.generateUniqueItemstackId(stack);
        AspectList aspects = CommonInternals.objectTags.get(id);
        if (aspects == null) {
            aspects = new AspectList();
            if (add) CommonInternals.objectTags.put(id, aspects);
        }
        return aspects;
    }

    @GroovyBlacklist
    @SuppressWarnings("deprecation")
    public void add(EntityEntry entity, AspectStack aspect, boolean doBackup) {
        if (entity != null && aspect != null) {
            boolean found = false;
            for (ThaumcraftApi.EntityTags tags : CommonInternals.scanEntities) {
                if (tags.entityName.equals(entity.getName())) {
                    tags.aspects.remove(aspect.getAspect());
                    if (aspect.getAmount() != 0) {
                        tags.aspects.add(aspect.getAspect(), aspect.getAmount());
                    }
                    found = true;
                }
            }
            if (!found) {
                ThaumcraftApi.registerEntityTag(entity.getName(), new AspectList().add(aspect.getAspect(), aspect.getAmount()));
            }

            if (doBackup) addScripted(entity, aspect);

            return;
        }
        GroovyLog.msg("Error adding Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    @GroovyBlacklist
    public void add(IIngredient ingredient, AspectStack aspect, boolean doBackup) {
        if (ingredient != null && aspect != null) {
            for (ItemStack ore : ingredient.getMatchingStacks()) {
                ItemStack oc = ore.copy();
                oc.setCount(1);
                add(oc, aspect, doBackup);
            }
            return;
        }
        GroovyLog.msg("Error adding Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    @GroovyBlacklist
    public void add(ItemStack item, AspectStack aspect, boolean doBackup) {
        if (item != null && aspect != null) {
            AspectList aspects = getAspects(item, aspect.getAmount() != 0);
            aspects.remove(aspect.getAspect());
            if (aspect.getAmount() != 0) {
                aspects.add(aspect.getAspect(), aspect.getAmount());
            }
            if (doBackup) addScripted(item, aspect);
            return;
        }
        GroovyLog.msg("Error adding Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    @GroovyBlacklist
    public void remove(EntityEntry entity, AspectStack aspect, boolean doBackup) {
        if (entity != null && aspect != null) {
            for (ThaumcraftApi.EntityTags tags : CommonInternals.scanEntities) {
                if (tags.entityName.equals(entity.getName())) {
                    for (Aspect a : tags.aspects.getAspects()) {
                        if (a.equals(aspect.getAspect())) {
                            aspect.setAmount(tags.aspects.getAmount(a));
                            tags.aspects.remove(a);
                        }
                    }
                }
            }

            if (doBackup) addBackup(entity, aspect);

            return;
        }
        GroovyLog.msg("Error removing Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    @GroovyBlacklist
    public void remove(IIngredient ingredient, AspectStack aspect, boolean doBackup) {
        if (ingredient != null && aspect != null) {
            for (ItemStack ore : ingredient.getMatchingStacks()) {
                ItemStack oc = ore.copy();
                oc.setCount(1);
                remove(oc, aspect, doBackup);
            }
            return;
        }
        GroovyLog.msg("Error removing Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    @GroovyBlacklist
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
                        remove(entity, new AspectStack(a, e.aspects.getAmount(a)));
                    }
                    return;
                }
            }
        }
        GroovyLog.msg("Error removing Thaumcraft Aspects from item/entity")
                .error()
                .post();
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.removeAll_item")
    public void removeAll(IIngredient ingredient) {
        for (ItemStack stack : ingredient.getMatchingStacks()) {
            removeAll(stack);
        }
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.aspect_helper.removeAll_item")
    public void removeAll(ItemStack target) {
        AspectList aspects = getAspects(target, false);
        for (Aspect a : aspects.getAspects()) {
            remove(target, new AspectStack(a, aspects.getAmount(a)), true);
        }
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
            return ingredient(object);
        }

        @RecipeBuilderMethodDescription(field = "object")
        public AspectHelperBuilder ingredient(IIngredient object) {
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
            this.stripAspects = !this.stripAspects;
            return this;
        }

        @RecipeBuilderRegistrationMethod
        public void register() {
            if (this.stripAspects) {
                if (this.entity != null) {
                    ModSupport.THAUMCRAFT.get().aspectHelper.removeAll(this.entity);
                } else if (this.object != null) {
                    ModSupport.THAUMCRAFT.get().aspectHelper.removeAll(this.object);
                } else {
                    GroovyLog.msg("Error removing Thaumcraft Aspects from item/entity")
                            .error()
                            .post();
                }
            }
            this.aspects.forEach(aspectStack -> {
                if (this.entity != null) {
                    ModSupport.THAUMCRAFT.get().aspectHelper.add(this.entity, aspectStack);
                } else if (this.object != null) {
                    ModSupport.THAUMCRAFT.get().aspectHelper.add(this.object, aspectStack);
                } else {
                    GroovyLog.msg("Error adding Thaumcraft Aspects to item/entity")
                            .error()
                            .post();
                }
            });
        }
    }
}
