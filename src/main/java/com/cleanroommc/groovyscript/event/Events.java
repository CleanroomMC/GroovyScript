package com.cleanroommc.groovyscript.event;

public class Events {

    public static void init() {

        /**
         * Called on block breaks
         *
         * @param world {@link net.minecraft.world.World}
         * @param blockState {@link net.minecraft.block.state.IBlockState}
         * @param pos {@link net.minecraft.util.math.BlockPos}
         * @param player {@link net.minecraft.entity.player.EntityPlayer}
         * @return true if cancelled
         */
        GroovyEventManager.registerEvent("onBlockBreak", true);

        GroovyEventManager.registerEvent("mod", "otherEvent", false);
    }
}
