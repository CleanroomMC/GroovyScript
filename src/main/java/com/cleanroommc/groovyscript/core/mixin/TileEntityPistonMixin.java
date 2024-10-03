package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TileEntityPiston.class)
public abstract class TileEntityPistonMixin {

    @Shadow
    private boolean extending;

    @Shadow
    private boolean shouldHeadBeRendered;

    @Inject(method = "moveCollidedEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;isStickyBlock(Lnet/minecraft/block/state/IBlockState;)Z", shift = At.Shift.AFTER), remap = false)
    private void moveCollidedEntitiesPre(float f,
                                         CallbackInfo ci,
                                         @Local(ordinal = 0) EnumFacing facing,
                                         @Local(ordinal = 1) List<Entity> list1,
                                         @Share("tryRecipesUntil") LocalIntRef tryRecipesUntil,
                                         @Share("pushingAgainst") LocalRef<IBlockState> pushingAgainst,
                                         @Share("checkRecipes") LocalBooleanRef checkRecipes) {
        TileEntityPiston piston = (TileEntityPiston) (Object) this;
        tryRecipesUntil.set(list1.size());
        boolean b = !piston.getWorld().isRemote && this.extending && this.shouldHeadBeRendered && f >= 1.0f;
        if (b) {
            pushingAgainst.set(piston.getWorld().getBlockState(piston.getPos().offset(facing)));
            checkRecipes.set(!(pushingAgainst.get() instanceof BlockPistonMoving) && pushingAgainst.get().getMaterial() != Material.AIR);
        } else {
            checkRecipes.set(false);
        }
    }

    @Inject(method = "moveCollidedEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPushReaction()Lnet/minecraft/block/material/EnumPushReaction;", shift = At.Shift.AFTER))
    private void moveCollidedEntitiesPost(float p_184322_1_,
                                          CallbackInfo ci,
                                          @Local int index,
                                          @Local(ordinal = 1) List<Entity> list1,
                                          @Share("tryRecipesUntil") LocalIntRef tryRecipesUntil,
                                          @Share("pushingAgainst") LocalRef<IBlockState> pushingAgainst,
                                          @Share("checkRecipes") LocalBooleanRef checkRecipes) {
        Entity entity = list1.get(index);
        if (entity.getPushReaction() == EnumPushReaction.IGNORE) return;
        if (checkRecipes.get() && index < tryRecipesUntil.get() && entity instanceof EntityItem entityItem) {
            VanillaModule.inWorldCrafting.pistonPush.findAndRunRecipe(entityItem1 -> {
                entityItem1.getEntityWorld().spawnEntity(entityItem1);
                list1.add(entityItem1);
            }, entityItem, pushingAgainst.get());
        }
    }

}
