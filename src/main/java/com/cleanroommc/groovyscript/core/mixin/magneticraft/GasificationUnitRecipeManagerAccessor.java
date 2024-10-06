package com.cleanroommc.groovyscript.core.mixin.magneticraft;

import com.cout970.magneticraft.api.internal.registries.machines.gasificationunit.GasificationUnitRecipeManager;
import com.cout970.magneticraft.api.registries.machines.gasificationunit.IGasificationUnitRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = GasificationUnitRecipeManager.class, remap = false)
public interface GasificationUnitRecipeManagerAccessor {

    @Accessor("recipes")
    static List<IGasificationUnitRecipe> getRecipes() {
        throw new UnsupportedOperationException();
    }

}
