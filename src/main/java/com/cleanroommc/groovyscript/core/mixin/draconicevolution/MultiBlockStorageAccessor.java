package com.cleanroommc.groovyscript.core.mixin.draconicevolution;

import com.brandon3055.brandonscore.lib.MultiBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MultiBlockStorage.class, remap = false)
public interface MultiBlockStorageAccessor {

    @Accessor
    String[][][] getBlockStorage();

    @Accessor
    void setBlockStorage(String[][][] blockStorage);
}
