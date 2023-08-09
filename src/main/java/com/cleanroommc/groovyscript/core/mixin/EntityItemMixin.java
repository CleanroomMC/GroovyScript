package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.compat.inworldcrafting.FluidRecipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin {

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;handleWaterMovement()Z"), cancellable = true)
    public void onUpdate(CallbackInfo ci) {
        EntityItem thisEntity = (EntityItem) (Object) this;
        if (!thisEntity.world.isRemote) {
            BlockPos pos = new BlockPos(thisEntity);
            IBlockState blockState = thisEntity.world.getBlockState(pos);
            Fluid fluid = FluidRecipe.getFluid(blockState);
            if (fluid != null &&
                FluidRecipe.isSourceBlock(blockState) &&
                FluidRecipe.findAndRunRecipe(fluid, thisEntity.world, pos, blockState) &&
                thisEntity.isDead) {
                ci.cancel();
            }
        }
    }
}
