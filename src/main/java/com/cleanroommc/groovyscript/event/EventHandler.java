package com.cleanroommc.groovyscript.event;

import com.cleanroommc.groovyscript.GroovyScript;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = GroovyScript.ID)
public class EventHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        GroovyEventManager.invokeEvent("onBlockBreak", event.getWorld(), event.getState(), event.getPos(), event.getPlayer());
        GroovyEventManager.invokeEvent("mod", "otherEvent", event.getPlayer());
    }
}
