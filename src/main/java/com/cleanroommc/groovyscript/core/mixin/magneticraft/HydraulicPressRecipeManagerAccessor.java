package com.cleanroommc.groovyscript.core.mixin.magneticraft;

import com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.HydraulicPressRecipeManager;
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = HydraulicPressRecipeManager.class, remap = false)
public interface HydraulicPressRecipeManagerAccessor {

    @Accessor("recipes")
    static List<IHydraulicPressRecipe> getRecipes() {
        throw new UnsupportedOperationException();
    }

}
