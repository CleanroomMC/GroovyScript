package com.cleanroommc.groovyscript.core.mixin.projecte;

import com.cleanroommc.groovyscript.compat.mods.projecte.EMCMapper;
import moze_intel.projecte.emc.mappers.CraftingMapper;
import moze_intel.projecte.emc.mappers.CraftingMapper.IRecipeMapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = CraftingMapper.class, remap = false)
public class CraftingMapperMixin {

    @Shadow @Final @Mutable
    private List<IRecipeMapper> recipeMappers;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.recipeMappers = new ArrayList<>(this.recipeMappers);
        this.recipeMappers.add(new EMCMapper());
    }
}
