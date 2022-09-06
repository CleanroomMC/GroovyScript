package com.cleanroommc.groovyscript.core.mixin.draconicevolution;

import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.lib.FusionRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(FusionRecipeRegistry.class)
public interface FusionRegistryAccessor {

    @Accessor
    List<IFusionRecipe> getREGISTRY();
}
