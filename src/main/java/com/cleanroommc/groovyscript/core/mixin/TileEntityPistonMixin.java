package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(TileEntityPiston.class)
public abstract class TileEntityPistonMixin {

    @Shadow
    private boolean extending;

    @Shadow
    private EnumFacing pistonFacing;

    @Shadow
    private float progress;

    @Shadow
    protected abstract IBlockState getCollisionRelatedBlockState();

    @Shadow
    protected abstract AxisAlignedBB moveByPositionAndProgress(AxisAlignedBB p_190607_1_);

    @Shadow
    protected abstract AxisAlignedBB getMinMaxPiecesAABB(List<AxisAlignedBB> p_191515_1_);

    @Shadow
    protected abstract AxisAlignedBB getMovementArea(AxisAlignedBB p_190610_1_, EnumFacing p_190610_2_, double p_190610_3_);

    @Shadow
    private IBlockState pistonState;

    @Shadow
    protected abstract double getMovement(AxisAlignedBB p_190612_1_, EnumFacing facing, AxisAlignedBB p_190612_3_);

    @Shadow
    @Final
    private static ThreadLocal<EnumFacing> MOVING_ENTITY;

    @Shadow
    private boolean shouldHeadBeRendered;

    @Shadow
    protected abstract void fixEntityWithinPistonBase(Entity p_190605_1_, EnumFacing p_190605_2_, double p_190605_3_);

    /**
     * @author brachy84
     * @reason easier recipe implementation
     */
    @Overwrite
    private void moveCollidedEntities(float progress) {
        TileEntityPiston thisTile = (TileEntityPiston) (Object) this;

        EnumFacing enumfacing = this.extending ? this.pistonFacing : this.pistonFacing.getOpposite();
        double d0 = progress - this.progress;
        List<AxisAlignedBB> list = Lists.newArrayList();
        this.getCollisionRelatedBlockState().addCollisionBoxToList(thisTile.getWorld(), BlockPos.ORIGIN, new AxisAlignedBB(BlockPos.ORIGIN), list, null, true);

        if (!list.isEmpty()) {
            AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(this.getMinMaxPiecesAABB(list));
            List<Entity> list1 = thisTile.getWorld().getEntitiesWithinAABBExcludingEntity(null, this.getMovementArea(axisalignedbb, enumfacing, d0).union(axisalignedbb));

            if (!list1.isEmpty()) {
                boolean flag = this.pistonState.getBlock().isStickyBlock(this.pistonState);

                // groovyscript start
                int tryRecipesUntil = list1.size();
                IBlockState pushingAgainst = null;
                boolean checkRecipes = !thisTile.getWorld().isRemote &&
                                       this.extending &&
                                       this.shouldHeadBeRendered &&
                                       progress >= 1f &&
                                       !((pushingAgainst = thisTile.getWorld().getBlockState(thisTile.getPos().offset(enumfacing))).getBlock() instanceof BlockPistonMoving) &&
                                       pushingAgainst.getMaterial() != Material.AIR;
                // groovyscript end

                for (int i = 0; i < list1.size(); i++) {
                    Entity entity = list1.get(i);
                    if (entity.getPushReaction() != EnumPushReaction.IGNORE) {
                        // groovyscript start
                        if (checkRecipes && i < tryRecipesUntil && entity instanceof EntityItem) {
                            EntityItem entityItem = (EntityItem) entity;
                            VanillaModule.inWorldCrafting.pistonPush.findAndRunRecipe(entityItem1 -> {
                                entityItem1.getEntityWorld().spawnEntity(entityItem1);
                                list1.add(entityItem1);
                            }, entityItem, pushingAgainst);
                        }
                        // groovyscript end

                        groovyscript$pushEntity(list, d0, enumfacing, entity, flag);
                    }
                }
            }
        }
    }

    private void groovyscript$pushEntity(List<AxisAlignedBB> list, double d0, EnumFacing enumfacing, Entity entity, boolean sticky) {
        if (sticky) {
            switch (enumfacing.getAxis()) {
                case X:
                    entity.motionX = enumfacing.getXOffset();
                    break;
                case Y:
                    entity.motionY = enumfacing.getYOffset();
                    break;
                case Z:
                    entity.motionZ = enumfacing.getZOffset();
            }
        }

        double d1 = 0.0D;

        for (AxisAlignedBB axisAlignedBB : list) {
            AxisAlignedBB axisalignedbb1 = this.getMovementArea(this.moveByPositionAndProgress(axisAlignedBB), enumfacing, d0);
            AxisAlignedBB axisalignedbb2 = entity.getEntityBoundingBox();

            if (axisalignedbb1.intersects(axisalignedbb2)) {
                d1 = Math.max(d1, this.getMovement(axisalignedbb1, enumfacing, axisalignedbb2));

                if (d1 >= d0) {
                    break;
                }
            }
        }

        if (d1 > 0.0D) {
            d1 = Math.min(d1, d0) + 0.01D;
            MOVING_ENTITY.set(enumfacing);
            entity.move(MoverType.PISTON, d1 * (double) enumfacing.getXOffset(), d1 * (double) enumfacing.getYOffset(), d1 * (double) enumfacing.getZOffset());
            MOVING_ENTITY.set(null);

            if (!this.extending && this.shouldHeadBeRendered) {
                this.fixEntityWithinPistonBase(entity, enumfacing, d0);
            }
        }
    }
}
