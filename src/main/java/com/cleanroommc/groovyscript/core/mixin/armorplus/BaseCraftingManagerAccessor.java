package com.cleanroommc.groovyscript.core.mixin.armorplus;

import com.sofodev.armorplus.api.crafting.base.BaseCraftingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BaseCraftingManager.class, remap = false)
public interface BaseCraftingManagerAccessor {

    @Accessor("xy")
    int getSize();
}
