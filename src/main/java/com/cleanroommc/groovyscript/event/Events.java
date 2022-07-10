package com.cleanroommc.groovyscript.event;

import com.cleanroommc.groovyscript.GroovyScript;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = GroovyScript.ID)
public class Events {

    // default event names
    public static final String BLOCK_BREAK = "world.onBlockBreak";
    public static final String PROJECTILE_IMPACT = "entity.onProjectileImpact";
    public static final String ENDER_TELEPORT = "entity.onEnderTeleport";

    // register events
    public static void init() {
        GroovyEventManager manager = GroovyEventManager.MAIN;

        /**
         * Called on block breaks
         *
         * Parameters:
         * @param world {@link net.minecraft.world.World}
         * @param blockState {@link net.minecraft.block.state.IBlockState}
         * @param pos {@link net.minecraft.util.math.BlockPos}
         * @param player {@link net.minecraft.entity.player.EntityPlayer}
         * @return true if cancelled
         */
        manager.registerEvent(BLOCK_BREAK, true);

        manager.registerEvent(PROJECTILE_IMPACT, true);

        manager.registerEvent(ENDER_TELEPORT, true);
    }

    // invoke events
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        GroovyEventManager.MAIN.invokeEvent(BLOCK_BREAK, event.getWorld(), event.getState(), event.getPos(), event.getPlayer());
    }

    @SubscribeEvent
    public static void onBlockBreak(ProjectileImpactEvent event) {
        GroovyEventManager.MAIN.invokeEvent(PROJECTILE_IMPACT, event.getEntity(), event.getRayTraceResult());
    }

    @SubscribeEvent
    public static void onBlockBreak(EnderTeleportEvent event) {
        GroovyEventManager.MAIN.invokeEvent(ENDER_TELEPORT, event);
    }
}
