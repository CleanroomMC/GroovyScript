package com.cleanroommc.groovyscript.core.mixin.advancedmortars;

import com.codetaylor.mc.advancedmortars.modules.mortar.recipe.RecipeMortar;
import com.codetaylor.mc.advancedmortars.modules.mortar.recipe.RegistryRecipeMortar;
import com.codetaylor.mc.advancedmortars.modules.mortar.reference.EnumMortarType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.EnumMap;
import java.util.List;

@Mixin(value = RegistryRecipeMortar.class, remap = false)
public interface RegistryRecipeMortarAccessor {

    @Accessor
    EnumMap<EnumMortarType, List<RecipeMortar>> getRecipeMap();

}
