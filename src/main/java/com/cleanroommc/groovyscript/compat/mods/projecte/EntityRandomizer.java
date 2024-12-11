package com.cleanroommc.groovyscript.compat.mods.projecte;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.projecte.WorldHelperAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.common.registry.EntityEntry;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class EntityRandomizer extends VirtualizedRegistry<Class<? extends EntityLiving>> {

    private final AbstractReloadableStorage<Class<? extends EntityLiving>> peacefulStorage = new AbstractReloadableStorage<>();

    @Override
    @GroovyBlacklist
    public void onReload() {
        WorldHelperAccessor.getMobs().removeAll(removeScripted());
        WorldHelperAccessor.getMobs().addAll(restoreFromBackup());
        WorldHelperAccessor.getPeacefuls().removeAll(peacefulStorage.removeScripted());
        WorldHelperAccessor.getPeacefuls().addAll(peacefulStorage.restoreFromBackup());
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addMob(Class<? extends EntityLiving> entry) {
        addScripted(entry);
        WorldHelperAccessor.getMobs().add(entry);
    }

    @SuppressWarnings("unchecked")
    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("entity('minecraft:pig')"))
    public void addMob(EntityEntry entity) {
        addMob((Class<? extends EntityLiving>) entity.getEntityClass());
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addPeaceful(Class<? extends EntityLiving> entry) {
        peacefulStorage.addScripted(entry);
        WorldHelperAccessor.getPeacefuls().add(entry);
    }

    @SuppressWarnings("unchecked")
    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("entity('minecraft:zombie')"))
    public void addPeaceful(EntityEntry entity) {
        addPeaceful((Class<? extends EntityLiving>) entity.getEntityClass());
    }

    @MethodDescription
    public boolean removeMob(Class<? extends EntityLiving> entry) {
        if (WorldHelperAccessor.getMobs().removeIf(r -> r == entry)) {
            addBackup(entry);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @MethodDescription(example = @Example("entity('minecraft:zombie')"))
    public boolean removeMob(EntityEntry entity) {
        return removeMob((Class<? extends EntityLiving>) entity.getEntityClass());
    }

    @MethodDescription
    public boolean removePeaceful(Class<? extends EntityLiving> entry) {
        if (WorldHelperAccessor.getPeacefuls().removeIf(r -> r == entry)) {
            peacefulStorage.addBackup(entry);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @MethodDescription(example = @Example("entity('minecraft:pig')"))
    public boolean removePeaceful(EntityEntry entity) {
        return removePeaceful((Class<? extends EntityLiving>) entity.getEntityClass());
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Class<? extends EntityLiving>> streamMobs() {
        return new SimpleObjectStream<>(WorldHelperAccessor.getMobs()).setRemover(this::removeMob);
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Class<? extends EntityLiving>> streamPeacefuls() {
        return new SimpleObjectStream<>(WorldHelperAccessor.getPeacefuls()).setRemover(this::removePeaceful);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllMobs() {
        WorldHelperAccessor.getMobs().forEach(this::addBackup);
        WorldHelperAccessor.getMobs().clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllPeacefuls() {
        WorldHelperAccessor.getPeacefuls().forEach(peacefulStorage::addBackup);
        WorldHelperAccessor.getPeacefuls().clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        removeAllMobs();
        removeAllPeacefuls();
    }
}
