package com.cleanroommc.groovyscript;

import com.cleanroommc.groovyscript.sandbox.SandboxRunner;
import groovy.lang.Closure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = GroovyScript.ID)
public class EventTest {

    public static final List<Closure<Object>> blockBreakEvents = new ArrayList<>();

    public static void onBlockBreak(Closure<Object> closureSource) {
        blockBreakEvents.add(closureSource);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        for (Closure<Object> closure : blockBreakEvents) {
            Object result = SandboxRunner.runClosure(closure, event.getWorld(), event.getState(), event.getPos(), event.getPlayer());
            if (result == Boolean.TRUE) {
                break;
            }
        }
    }
}
