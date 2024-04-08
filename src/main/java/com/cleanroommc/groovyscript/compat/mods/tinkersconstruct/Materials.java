package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.GroovyMaterial;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.GroovyMaterialIntegration;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.ToolMaterialBuilder;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.armory.GroovyArmorTrait;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.traits.GroovyTrait;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.MaterialAccessor;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.ITrait;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES, reloadability = RegistryDescription.Reloadability.DISABLED)
public class Materials extends NamedRegistry {

    @RecipeBuilderDescription
    public ToolMaterialBuilder materialBuilder(String name) {
        return new ToolMaterialBuilder(name);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addTrait(GroovyTrait trait) {
        if (trait == null) return;
        TinkerRegistry.addTrait(trait);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addArmorTrait(GroovyArmorTrait trait) {
        if (trait == null) return;
        TinkerRegistry.addTrait(trait);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addMaterial(GroovyMaterial material) {
        if (material == null) return;
        TinkerRegistry.addMaterial(material);
        TinkerRegistry.integrate(new GroovyMaterialIntegration(material));
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public boolean removeMaterial(String material) {
        if (TinkerRegistryAccessor.getMaterials().entrySet().removeIf(entry -> entry.getKey().equals(material))) return true;
        GroovyLog.msg("Error removing Tinkers Construct material")
                .add("could not find material with name {}", material)
                .error()
                .post();
        return false;
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public boolean removeTrait(String trait) {
        if (TinkerRegistryAccessor.getTraits().entrySet().removeIf(entry -> {
            boolean found = entry.getKey().equals(trait);
            if (found)
                TinkerRegistryAccessor.getMaterials().values().forEach(material -> ((MaterialAccessor) material).getTraits().values().forEach(l -> l.removeIf(t -> t.getIdentifier().equals(trait))));
            return found;
        })) return true;
        GroovyLog.msg("Error removing Tinkers Construct material trait")
                .add("could not find trait with name {}", trait)
                .error()
                .post();
        return false;
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public boolean removeMaterial(Material material) {
        if (material == null) return false;
        TinkerRegistryAccessor.getMaterials().remove(material.getIdentifier(), material);
        return true;
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public boolean removeTrait(ITrait trait) {
        if (trait == null) return false;
        TinkerRegistryAccessor.getTraits().remove(trait.getIdentifier(), trait);
        TinkerRegistryAccessor.getMaterials().values().forEach(material -> ((MaterialAccessor) material).getTraits().values().forEach(l -> l.removeIf(t -> t.getIdentifier().equals(trait.getIdentifier()))));
        return true;
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Material> streamMaterials() {
        return new SimpleObjectStream<>(TinkerRegistryAccessor.getMaterials().values()).setRemover(this::removeMaterial);
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ITrait> streamTraits() {
        return new SimpleObjectStream<>(TinkerRegistryAccessor.getTraits().values()).setRemover(this::removeTrait);
    }
}
