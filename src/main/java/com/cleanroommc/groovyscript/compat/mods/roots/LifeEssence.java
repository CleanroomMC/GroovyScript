package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.registry.EntityEntry;

import static epicsquid.roots.init.ModRecipes.getLifeEssenceList;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class LifeEssence extends VirtualizedRegistry<Class<? extends EntityLivingBase>> {

    @Override
    public void onReload() {
        removeScripted().forEach(getLifeEssenceList()::remove);
        restoreFromBackup().forEach(getLifeEssenceList()::add);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(Class<? extends EntityLivingBase> clazz) {
        getLifeEssenceList().add(clazz);
        addScripted(clazz);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(EntityLivingBase entity) {
        add(entity.getClass());
    }

    @MethodDescription(example = @Example("entity('minecraft:wither_skeleton')"), type = MethodDescription.Type.ADDITION)
    public void add(EntityEntry entity) {
        add((Class<? extends EntityLivingBase>) entity.getEntityClass());
    }

    @MethodDescription(example = @Example("entity('minecraft:sheep')"))
    public boolean remove(Class<? extends EntityLivingBase> clazz) {
        if (!getLifeEssenceList().remove(clazz)) return false;
        addBackup(clazz);
        return true;
    }

    @MethodDescription
    public boolean remove(EntityLivingBase entity) {
        return remove(entity.getClass());
    }

    @MethodDescription
    public boolean remove(EntityEntry entity) {
        return remove((Class<? extends EntityLivingBase>) entity.getEntityClass());
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getLifeEssenceList().forEach(this::addBackup);
        getLifeEssenceList().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Class<? extends EntityLivingBase>> streamRecipes() {
        return new SimpleObjectStream<>(getLifeEssenceList()).setRemover(this::remove);
    }
}
