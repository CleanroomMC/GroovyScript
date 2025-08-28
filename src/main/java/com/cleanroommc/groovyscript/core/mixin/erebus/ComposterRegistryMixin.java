package com.cleanroommc.groovyscript.core.mixin.erebus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import erebus.recipes.ComposterRegistry;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ComposterRegistry.class, remap = false)
public abstract class ComposterRegistryMixin {

    /**
     * @reason ensure the list of valid materials is mutable for GroovyScript compat
     * @author WaitingIdly
     */
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Arrays;asList([Ljava/lang/Object;)Ljava/util/List;"))
    private static List<Material> mutableMaterial(List<Material> original) {
        return new ArrayList<>(original);
    }
}
