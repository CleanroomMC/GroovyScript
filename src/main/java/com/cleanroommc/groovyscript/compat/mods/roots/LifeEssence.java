package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.registry.EntityEntry;

import static epicsquid.roots.init.ModRecipes.getLifeEssenceList;

public class LifeEssence extends VirtualizedRegistry<Class<? extends EntityLivingBase>> {

    public LifeEssence() {
        super();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(getLifeEssenceList()::remove);
        restoreFromBackup().forEach(getLifeEssenceList()::add);
    }

    public void add(Class<? extends EntityLivingBase> clazz) {
        getLifeEssenceList().add(clazz);
        addScripted(clazz);
    }

    public void add(EntityLivingBase entity) {
        add(entity.getClass());
    }

    public void add(EntityEntry entity) {
        add((Class<? extends EntityLivingBase>) entity.getEntityClass());
    }

    public boolean remove(Class<? extends EntityLivingBase> clazz) {
        if (!getLifeEssenceList().remove(clazz)) return false;
        addBackup(clazz);
        return true;
    }

    public boolean remove(EntityLivingBase entity) {
        return remove(entity.getClass());
    }

    public boolean remove(EntityEntry entity) {
        return remove((Class<? extends EntityLivingBase>) entity.getEntityClass());
    }

    public void removeAll() {
        getLifeEssenceList().forEach(this::addBackup);
        getLifeEssenceList().clear();
    }

    public SimpleObjectStream<Class<? extends EntityLivingBase>> streamRecipes() {
        return new SimpleObjectStream<>(getLifeEssenceList()).setRemover(this::remove);
    }
}
