package com.cleanroommc.groovyscript.compat.mods.projecte;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.projecte.WorldHelperAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.common.registry.EntityEntry;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class EntityRandomizerPeaceful extends VirtualizedRegistry<Class<? extends EntityLiving>> {

    @Override
    @GroovyBlacklist
    public void onReload() {
        WorldHelperAccessor.getPeacefuls().removeAll(removeScripted());
        WorldHelperAccessor.getPeacefuls().addAll(restoreFromBackup());
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(Class<? extends EntityLiving> entry) {
        addScripted(entry);
        WorldHelperAccessor.getPeacefuls().add(entry);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("entity('minecraft:zombie')"))
    public void add(EntityEntry entity) {
        add((Class<? extends EntityLiving>) entity.getEntityClass());
    }

    @MethodDescription
    public boolean remove(Class<? extends EntityLiving> entry) {
        if (WorldHelperAccessor.getPeacefuls().removeIf(r -> r == entry)) {
            addBackup(entry);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("entity('minecraft:pig')"))
    public boolean remove(EntityEntry entity) {
        return remove((Class<? extends EntityLiving>) entity.getEntityClass());
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Class<? extends EntityLiving>> streamRecipes() {
        return new SimpleObjectStream<>(WorldHelperAccessor.getPeacefuls()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        WorldHelperAccessor.getPeacefuls().forEach(this::addBackup);
        WorldHelperAccessor.getPeacefuls().clear();
    }
}
