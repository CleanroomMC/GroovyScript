package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.compat.inworldcrafting.Burning;
import com.cleanroommc.groovyscript.compat.inworldcrafting.FluidRecipe;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends Entity {

    private EntityItemMixin(World worldIn) {
        super(worldIn);
    }

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
                return;
            }

            if (((EntityAccessor) thisEntity).getFire() > 0) {
                VanillaModule.inWorldCrafting.burning.updateRecipeProgress(thisEntity);
                ci.cancel();
            } else if (Burning.removeBurningItem(thisEntity)) {
                thisEntity.setEntityInvulnerable(false);
            }
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        Burning.removeBurningItem((EntityItem) (Object) this);
    }
}
