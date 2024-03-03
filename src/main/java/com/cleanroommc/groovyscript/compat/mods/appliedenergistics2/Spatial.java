package com.cleanroommc.groovyscript.compat.mods.appliedenergistics2;

import appeng.api.AEApi;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.appliedenergistics2.MovableTileRegistryAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.tileentity.TileEntity;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class Spatial extends VirtualizedRegistry<Class<? extends TileEntity>> {

    @Override
    public void onReload() {
        removeScripted().forEach(((MovableTileRegistryAccessor) AEApi.instance().registries().movable()).getTest()::remove);
        restoreFromBackup().forEach(AEApi.instance().registries().movable()::whiteListTileEntity);
    }

    public void afterScriptLoad() {
        ((MovableTileRegistryAccessor) AEApi.instance().registries().movable()).getValid().clear();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'net.minecraft.tileentity.TileEntityStructure'"))
    public void add(String className) {
        add(loadClass(className));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(Class<? extends TileEntity> clazz) {
        addScripted(clazz);
        AEApi.instance().registries().movable().whiteListTileEntity(clazz);
    }

    @MethodDescription(example = @Example("'net.minecraft.tileentity.TileEntityChest'"))
    public void remove(String className) {
        remove(loadClass(className));
    }

    @MethodDescription
    public void remove(Class<? extends TileEntity> clazz) {
        addBackup(clazz);
        ((MovableTileRegistryAccessor) AEApi.instance().registries().movable()).getTest().remove(clazz);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
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
