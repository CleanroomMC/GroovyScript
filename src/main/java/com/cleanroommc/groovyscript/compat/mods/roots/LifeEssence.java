package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import epicsquid.roots.init.ModRecipes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.registry.EntityEntry;

import java.util.Collection;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class LifeEssence extends StandardListRegistry<Class<? extends EntityLivingBase>> {

    @Override
    public Collection<Class<? extends EntityLivingBase>> getRecipes() {
        return ModRecipes.getLifeEssenceList();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(EntityLivingBase entity) {
        add(entity.getClass());
    }

    @SuppressWarnings("unchecked")
    @MethodDescription(example = @Example("entity('minecraft:wither_skeleton')"), type = MethodDescription.Type.ADDITION)
    public void add(EntityEntry entity) {
        add((Class<? extends EntityLivingBase>) entity.getEntityClass());
    }

    @MethodDescription
    public boolean remove(EntityLivingBase entity) {
        return remove(entity.getClass());
    }

    @SuppressWarnings("unchecked")
    @MethodDescription(example = @Example("entity('minecraft:sheep')"))
    public boolean remove(EntityEntry entity) {
        return remove((Class<? extends EntityLivingBase>) entity.getEntityClass());
    }
}
