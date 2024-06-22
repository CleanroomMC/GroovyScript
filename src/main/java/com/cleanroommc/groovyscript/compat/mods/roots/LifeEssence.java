package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.init.ModRecipes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.registry.EntityEntry;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class LifeEssence extends VirtualizedRegistry<Class<? extends EntityLivingBase>> {

    @Override
    public void onReload() {
        removeScripted().forEach(ModRecipes.getLifeEssenceList()::remove);
        restoreFromBackup().forEach(ModRecipes.getLifeEssenceList()::add);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(Class<? extends EntityLivingBase> clazz) {
        ModRecipes.getLifeEssenceList().add(clazz);
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
        if (!ModRecipes.getLifeEssenceList().remove(clazz)) return false;
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

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipes.getLifeEssenceList().forEach(this::addBackup);
        ModRecipes.getLifeEssenceList().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Class<? extends EntityLivingBase>> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipes.getLifeEssenceList()).setRemover(this::remove);
    }
}
