package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.GroovyMaterial;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.MaterialRegistryEvent;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.ToolMaterialBuilder;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.traits.TraitRegistryEvent;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.ITrait;

public class TinkersConstruct extends GroovyPropertyContainer {

    public final Drying drying = new Drying();
    public final Melting melting = new Melting();
    public final EntityMelting entityMelting = new EntityMelting();
    public final SmelteryFuel smelteryFuel = new SmelteryFuel();
    public final Alloying alloying = new Alloying();
    public final CastingTable castingTable = new CastingTable();
    public final CastingBasin castingBasin = new CastingBasin();
    public final Materials materials = new Materials();

    @Override
    public void initialize(GroovyContainer<?> container) {
        container.objectMapperBuilder("toolMaterial", Material.class)
                .parser(IObjectParser.wrapStringGetter(TinkerRegistryAccessor.getMaterials()::get))
                .completerOfNames(TinkerRegistryAccessor.getMaterials()::keySet)
                .docOfType("tool material")
                .register();
        container.objectMapperBuilder("toolTrait", ITrait.class)
                .parser(IObjectParser.wrapStringGetter(TinkerRegistryAccessor.getTraits()::get))
                .completerOfNamed(TinkerRegistryAccessor.getTraits()::keySet, v -> v.endsWith("_armor") ? null : v) // only suggest non armor traits
                .docOfType("tool trait")
                .register();
        container.objectMapperBuilder("armorTrait", ITrait.class)
                .parser(IObjectParser.wrapStringGetter(s -> TinkerRegistryAccessor.getTraits().get(s + "_armor")))
                .completerOfNamed(TinkerRegistryAccessor.getTraits()::keySet,
                                  v -> v.endsWith("_armor") ? v.substring(0, v.length() - 6) : null) // only suggest armor traits
                .docOfType("armor trait")
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
