package com.cleanroommc.groovyscript.core.mixin.magneticraft;

import com.cout970.magneticraft.api.internal.registries.generators.thermopile.ThermopileRecipeManager;
import com.cout970.magneticraft.api.registries.generators.thermopile.IThermopileRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = ThermopileRecipeManager.class, remap = false)
public interface ThermopileRecipeManagerAccessor {

    @Accessor("recipeList")
    static List<IThermopileRecipe> getRecipeList() {
        throw new UnsupportedOperationException();
    }
}
