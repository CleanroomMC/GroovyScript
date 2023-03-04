package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.GroovyMaterial;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.ToolMaterialBuilder;
import com.cleanroommc.groovyscript.sandbox.LoadStage;

public class TinkersConstruct extends ModPropertyContainer {
    public static final LoadStage MATERIALS = new LoadStage("tinkersMaterials", false);

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
        addRegistry(melting.entityMelting);
        addRegistry(casting.table);
        addRegistry(casting.basin);
        addRegistry(materials);
    }

    public static void init() {
        ToolMaterialBuilder.addedMaterials.forEach(GroovyMaterial::registerTraits);
    }
}
