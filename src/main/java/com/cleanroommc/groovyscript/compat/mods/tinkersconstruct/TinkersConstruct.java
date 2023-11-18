package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.IGameObjectHandler;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.GroovyMaterial;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.MaterialRegistryEvent;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.ToolMaterialBuilder;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.traits.TraitRegistryEvent;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlerManager;
import net.minecraftforge.common.MinecraftForge;

public class TinkersConstruct extends ModPropertyContainer {

    public final Drying drying = new Drying();
    public final Melting melting = new Melting();
    public final SmelteryFuel smelteryFuel = new SmelteryFuel();
    public final Alloying alloying = new Alloying();
    public final Casting casting = new Casting();
    public final Materials materials = new Materials();

    public TinkersConstruct() {
        addRegistry(drying);
        addRegistry(melting);
        addRegistry(smelteryFuel);
        addRegistry(alloying);
        addRegistry(casting.table);
        addRegistry(casting.basin);
        addRegistry(materials);
    }

    @Override
    public void initialize() {
        GameObjectHandlerManager.registerGameObjectHandler("tconstruct", "toolMaterial", IGameObjectHandler.wrapStringGetter(TinkerRegistryAccessor.getMaterials()::get));
        GameObjectHandlerManager.registerGameObjectHandler("tconstruct", "toolTrait", IGameObjectHandler.wrapStringGetter(TinkerRegistryAccessor.getTraits()::get));
        GameObjectHandlerManager.registerGameObjectHandler("tconstruct", "armorTrait", IGameObjectHandler.wrapStringGetter(s -> TinkerRegistryAccessor.getTraits().get(s + "_armor")));
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.post(new TraitRegistryEvent());
        ToolMaterialBuilder.addedMaterials.forEach(GroovyMaterial::registerTraits);
    }

    public static void preInit() {
        MinecraftForge.EVENT_BUS.post(new MaterialRegistryEvent());
    }
}
