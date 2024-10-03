package com.cleanroommc.groovyscript.core.mixin.roots;

import epicsquid.roots.modifiers.CostType;
import epicsquid.roots.modifiers.IModifierCost;
import epicsquid.roots.modifiers.Modifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = Modifier.class, remap = false)
public interface ModifierAccessor {

    @Accessor
    Map<CostType, IModifierCost> getCosts();

}
