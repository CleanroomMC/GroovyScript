package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material;

import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class GroovyMaterialIntegration extends MaterialIntegration {

    public GroovyMaterialIntegration(Material material) {
        super(material, material.getFluid());
    }

    @Override
    public void preInit() {
    }

    @Override
    public void integrate() {
        if (material.isCastable()) TinkerSmeltery.registerToolpartMeltingCasting(material);
    }

}
