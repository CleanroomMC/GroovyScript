package com.cleanroommc.groovyscript.core.mixin.forestry;

import forestry.api.apiculture.IBeeMutation;
import forestry.apiculture.genetics.BeeRoot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = BeeRoot.class, remap = false)
public interface BeeRootAccessor {

    @Accessor
    static List<IBeeMutation> getBeeMutations() {
        throw new AssertionError();
    }
}
