package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.IGameObjectParser;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.GroovyMaterial;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.MaterialRegistryEvent;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.ToolMaterialBuilder;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.traits.TraitRegistryEvent;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandler;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.ITrait;

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
        GameObjectHandler.builder("toolMaterial", Material.class)
                .mod("tconstruct")
                .parser(IGameObjectParser.wrapStringGetter(TinkerRegistryAccessor.getMaterials()::get))
                .completerOfNames(TinkerRegistryAccessor.getMaterials()::keySet)
                .register();
        GameObjectHandler.builder("toolTrait", ITrait.class)
                .mod("tconstruct")
                .parser(IGameObjectParser.wrapStringGetter(TinkerRegistryAccessor.getTraits()::get))
                .completerOfNamed(TinkerRegistryAccessor.getTraits()::keySet, v -> v.endsWith("_armor") ? null : v) // only suggest non armor traits
                .register();
        GameObjectHandler.builder("armorTrait", ITrait.class)
                .mod("tconstruct")
                .parser(IGameObjectParser.wrapStringGetter(s -> TinkerRegistryAccessor.getTraits().get(s + "_armor")))
                .completerOfNamed(TinkerRegistryAccessor.getTraits()::keySet, v -> v.endsWith("_armor") ? v.substring(0, v.length() - 6)
                                                                                                        : null) // only suggest armor traits
                .register();
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.post(new TraitRegistryEvent());
        ToolMaterialBuilder.addedMaterials.forEach(GroovyMaterial::registerTraits);
    }

    public static void preInit() {
        MinecraftForge.EVENT_BUS.post(new MaterialRegistryEvent());
    }
}
