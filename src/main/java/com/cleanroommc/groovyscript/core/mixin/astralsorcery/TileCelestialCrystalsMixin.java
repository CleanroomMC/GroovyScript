package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystals;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TileCelestialCrystals.class, remap = false)
public abstract class TileCelestialCrystalsMixin {

    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDefaultState()Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState replaceIronOreDowngrade(Block instance, Operation<IBlockState> original) {
        var replacementState = ModSupport.ASTRAL_SORCERY.get().lightTransmutation.getReplacementState();
        if (replacementState == null) return original.call(instance);
        return replacementState;
    }

}
