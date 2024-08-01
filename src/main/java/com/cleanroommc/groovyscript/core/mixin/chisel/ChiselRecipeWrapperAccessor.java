package com.cleanroommc.groovyscript.core.mixin.chisel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import team.chisel.api.carving.ICarvingGroup;
import team.chisel.common.integration.jei.ChiselRecipeWrapper;

@Mixin(value = ChiselRecipeWrapper.class, remap = false)
public interface ChiselRecipeWrapperAccessor {

    @Accessor("group")
    ICarvingGroup getGroup();

}
