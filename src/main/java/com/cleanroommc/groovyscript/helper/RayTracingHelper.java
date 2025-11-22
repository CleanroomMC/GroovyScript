package com.cleanroommc.groovyscript.helper;

import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class RayTracingHelper {

    /**
     * gets the block being looked at, stopping on fluid blocks
     */
    public static RayTraceResult getBlockLookingAt(EntityPlayer player) {
        double distance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d eyes = player.getPositionEyes(0.0F);
        Vec3d look = player.getLook(0.0F);
        Vec3d end = eyes.add(look.x * distance, look.y * distance, look.z * distance);

        RayTraceResult result = player.getEntityWorld().rayTraceBlocks(eyes, end, true);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            return result;
        }
        return null;
    }

    /**
     * gets the closest entity being looked at
     */
    public static Entity getEntityLookingAt(EntityPlayer player) {
        Entity entity = null;
        double d0 = 0.0D;

        double distance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d eyes = player.getPositionEyes(0.0F);
        Vec3d look = player.getLook(0.0F);
        Vec3d end = eyes.add(look.x * distance, look.y * distance, look.z * distance);

        List<Entity> list = player.world.getEntitiesInAABBexcluding(
                player,
                player.getEntityBoundingBox()
                        .expand(look.x * distance, look.y * distance, look.z * distance)
                        .grow(1.0D, 1.0D, 1.0D),
                Predicates.and(EntitySelectors.NOT_SPECTATING, e -> e != null && e.canBeCollidedWith()));

        for (Entity entity1 : list) {
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.3);
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(eyes, end);

            if (raytraceresult != null) {
                double d1 = eyes.squareDistanceTo(raytraceresult.hitVec);
                if (d1 < d0 || d0 == 0.0D) {
                    entity = entity1;
                    d0 = d1;
                }
            }
        }
        return entity;
    }
}
