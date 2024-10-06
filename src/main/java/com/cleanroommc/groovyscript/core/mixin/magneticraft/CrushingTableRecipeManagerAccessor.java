package com.cleanroommc.groovyscript.core.mixin.magneticraft;

import com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipeManager;
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = CrushingTableRecipeManager.class, remap = false)
public interface CrushingTableRecipeManagerAccessor {

    @Accessor("recipes")
    static List<ICrushingTableRecipe> getRecipes() {
        throw new UnsupportedOperationException();
    }

}
