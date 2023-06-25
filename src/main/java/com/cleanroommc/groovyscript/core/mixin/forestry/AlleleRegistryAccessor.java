package com.cleanroommc.groovyscript.core.mixin.forestry;

import forestry.api.genetics.IAllele;
import forestry.core.genetics.alleles.AlleleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.LinkedHashMap;

@Mixin(value = AlleleRegistry.class, remap = false)
public interface AlleleRegistryAccessor {

    @Accessor
    LinkedHashMap<String, IAllele> getAlleleMap();
}
