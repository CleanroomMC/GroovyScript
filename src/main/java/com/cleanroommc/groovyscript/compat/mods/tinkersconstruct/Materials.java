package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.GroovyMaterial;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.ToolMaterialBuilder;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.armory.Armory;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.traits.TraitBuilder;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fml.common.Loader;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.ITrait;

public class Materials extends VirtualizedRegistry<GroovyMaterial> {
    public Armory Armory;

    public Materials() {
        super();
        if (Loader.isModLoaded("conarm")) this.Armory = new Armory();
    }

    public ToolMaterialBuilder materialBuilder(String name) {
        return new ToolMaterialBuilder(name);
    }

    public TraitBuilder traitBuilder(String name) {
        return new TraitBuilder(name);
    }

    @Override
    @GroovyBlacklist
    public void onReload() {

    }

    public boolean removeMaterial(String material) {
        if (TinkerRegistryAccessor.getMaterials().entrySet().removeIf(entry -> entry.getKey().equals(material))) return true;
        GroovyLog.msg("Error removing Tinkers Construct material")
                .add("could not find material with name %s", material)
                .error()
                .post();
        return false;
    }

    public boolean removeMaterial(Material material) {
        if (material == null) return false;
        TinkerRegistryAccessor.getMaterials().remove(material.getIdentifier(), material);
        return true;
    }

    public SimpleObjectStream<Material> streamMaterials() {
        return new SimpleObjectStream<>(TinkerRegistryAccessor.getMaterials().values()).setRemover(this::removeMaterial);
    }

    public SimpleObjectStream<ITrait> streamTraits() {
        return new SimpleObjectStream<>(TinkerRegistryAccessor.getTraits().values());
    }
}
