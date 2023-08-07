package com.cleanroommc.groovyscript.compat.mods.appliedenergistics2;

import appeng.api.AEApi;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.appliedenergistics2.MovableTileRegistryAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.tileentity.TileEntity;

public class Spatial extends VirtualizedRegistry<Class<? extends TileEntity>> {

    public Spatial() {
        super();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(((MovableTileRegistryAccessor) AEApi.instance().registries().movable()).getTest()::remove);
        restoreFromBackup().forEach(AEApi.instance().registries().movable()::whiteListTileEntity);
    }

    public void afterScriptLoad() {
        ((MovableTileRegistryAccessor) AEApi.instance().registries().movable()).getValid().clear();
    }

    public void add(String className) {
        add(loadClass(className));
    }

    public void add(Class<? extends TileEntity> clazz) {
        addScripted(clazz);
        AEApi.instance().registries().movable().whiteListTileEntity(clazz);
    }

    public void remove(String className) {
        remove(loadClass(className));
    }

    public void remove(Class<? extends TileEntity> clazz) {
        addBackup(clazz);
        ((MovableTileRegistryAccessor) AEApi.instance().registries().movable()).getTest().remove(clazz);
    }

    public void removeAll() {
        ((MovableTileRegistryAccessor) AEApi.instance().registries().movable()).getTest().forEach(this::addBackup);
        ((MovableTileRegistryAccessor) AEApi.instance().registries().movable()).getTest().clear();
    }

    private static Class<? extends TileEntity> loadClass(String className) {
        try {
            return (Class<? extends TileEntity>) Class.forName(className);
        } catch (Exception e) {
            GroovyLog.get().error("Failed to load TileEntity class '{}'", className);
        }
        return null;
    }
}
