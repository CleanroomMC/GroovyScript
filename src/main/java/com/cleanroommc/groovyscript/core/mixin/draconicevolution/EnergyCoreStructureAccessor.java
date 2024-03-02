package com.cleanroommc.groovyscript.core.mixin.draconicevolution;

import com.brandon3055.brandonscore.lib.MultiBlockStorage;
import com.brandon3055.draconicevolution.world.EnergyCoreStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnergyCoreStructure.class)
public interface EnergyCoreStructureAccessor {

    @Accessor
    MultiBlockStorage[] getStructureTiers();
}
